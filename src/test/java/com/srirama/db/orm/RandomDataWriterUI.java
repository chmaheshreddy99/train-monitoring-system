package com.srirama.db.orm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class RandomDataWriterUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField filePathField;
    private JTextField descriptorFileField;
    private JTextField frequencyField;
    private JTextArea dataLogger;
    private JButton startButton;
    private JButton stopButton;

    private volatile boolean running = false;
    private Thread writerThread;
    private Thread descriptorWatcherThread;

    private final List<DataDescriptor> descriptors = new CopyOnWriteArrayList<>();
    private final Random random = new Random();

    public RandomDataWriterUI() {
        setTitle("Random Data File Writer with Descriptor");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        filePathField = new JTextField("data.txt");
        descriptorFileField = new JTextField("descriptor.txt");
        frequencyField = new JTextField("1000");

        formPanel.add(new JLabel("Output File Path:"));
        formPanel.add(filePathField);
        formPanel.add(new JLabel("Descriptor File Path:"));
        formPanel.add(descriptorFileField);
        formPanel.add(new JLabel("Write Frequency (ms):"));
        formPanel.add(frequencyField);

        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        formPanel.add(startButton);
        formPanel.add(stopButton);

        dataLogger = new JTextArea();
        dataLogger.setEditable(false);
        dataLogger.setBackground(Color.BLACK);
        dataLogger.setForeground(Color.GREEN);
        JScrollPane scrollPane = new JScrollPane(dataLogger);

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        startButton.addActionListener(this::startWriting);
        stopButton.addActionListener(e -> stopWriting());
    }

    private void startWriting(ActionEvent e) {
        String filePath = filePathField.getText().trim();
        String descriptorPath = descriptorFileField.getText().trim();
        int frequency;

        try {
            frequency = Integer.parseInt(frequencyField.getText().trim());
            if (frequency <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid frequency input");
            return;
        }

        File outputFile = new File(filePath);
        File descriptorFile = new File(descriptorPath);

        if (!descriptorFile.exists()) {
            JOptionPane.showMessageDialog(this, "Descriptor file does not exist.");
            return;
        }

        loadDescriptors(descriptorFile);

        running = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);

        // Start descriptor watcher thread
        descriptorWatcherThread = new Thread(() -> watchDescriptorFile(descriptorFile));
        descriptorWatcherThread.start();

        // Start writer thread
        writerThread = new Thread(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
                while (running) {
                    String line = generateRandomLine();
                    writer.write(line);
                    writer.newLine();
                    writer.flush();

                    SwingUtilities.invokeLater(() -> {
                        dataLogger.append(line + "\n");
                        dataLogger.setCaretPosition(dataLogger.getDocument().getLength());
                    });

                    Thread.sleep(frequency);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        writerThread.start();
    }

    private void stopWriting() {
        running = false;
        stopButton.setEnabled(false);
        startButton.setEnabled(true);
        if (writerThread != null) writerThread.interrupt();
        if (descriptorWatcherThread != null) descriptorWatcherThread.interrupt();
    }

    private void loadDescriptors(File descriptorFile) {
        try {
            List<String> lines = Files.readAllLines(descriptorFile.toPath());
            if (lines.isEmpty()) return;
            String[] tokens = lines.get(0).split("\\|");

            descriptors.clear();
            for (String token : tokens) {
                descriptors.add(DataDescriptor.parse(token.trim()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void watchDescriptorFile(File descriptorFile) {
        try {
            Path path = descriptorFile.toPath().getParent();
            WatchService watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            while (running) {
                WatchKey key = watchService.take(); // blocking
                for (WatchEvent<?> event : key.pollEvents()) {
                    Path changed = (Path) event.context();
                    if (descriptorFile.toPath().getFileName().equals(changed)) {
                        loadDescriptors(descriptorFile);
                        SwingUtilities.invokeLater(() -> {
                            dataLogger.append("[Descriptor Updated]\n");
                        });
                    }
                }
                key.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateRandomLine() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < descriptors.size(); i++) {
            sb.append(descriptors.get(i).generate(random));
            if (i < descriptors.size() - 1) sb.append("|");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			new RandomDataWriterUI().setVisible(true);
		});
    }

    // Helper class to represent a data descriptor
    static class DataDescriptor {
        enum Type { DATE, TIME, DOUBLE, STRING }

        Type type;
        double min, max;
        List<String> stringValues;

        public static DataDescriptor parse(String token) {
            token = token.trim();
            if (token.equalsIgnoreCase("Date")) {
                DataDescriptor d = new DataDescriptor();
                d.type = Type.DATE;
                return d;
            } else if (token.equalsIgnoreCase("Time")) {
                DataDescriptor d = new DataDescriptor();
                d.type = Type.TIME;
                return d;
            } else if (token.startsWith("Double")) {
                DataDescriptor d = new DataDescriptor();
                d.type = Type.DOUBLE;
                if (token.contains(",")) {
                    String[] parts = token.replaceAll("[<>]", "").split(",");
                    if (parts.length == 2) {
                        String[] range = parts[1].split("-");
                        d.min = Double.parseDouble(range[0]);
                        d.max = Double.parseDouble(range[1]);
                    }
                } else {
                    d.min = 0;
                    d.max = 1000;
                }
                return d;
            } else if (token.startsWith("String")) {
                DataDescriptor d = new DataDescriptor();
                d.type = Type.STRING;
                d.stringValues = new ArrayList<>();
                if (token.contains(",")) {
                    String[] parts = token.replaceAll("[<>]", "").split(",");
                    for (int i = 1; i < parts.length; i++) {
                        d.stringValues.add(parts[i].trim());
                    }
                } else {
                    d.stringValues.addAll(Arrays.asList("Alpha", "Beta", "Gamma"));
                }
                return d;
            } else {
                throw new IllegalArgumentException("Unknown descriptor: " + token);
            }
        }

        public String generate(Random random) {
            switch (type) {
                case DATE:
                    return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                case TIME:
                    return LocalTime.now().withNano(0).toString();
                case DOUBLE:
                    return String.format("%.2f", min + (max - min) * random.nextDouble());
                case STRING:
                    return stringValues.get(random.nextInt(stringValues.size()));
                default:
                    return "";
            }
        }
    }
}
