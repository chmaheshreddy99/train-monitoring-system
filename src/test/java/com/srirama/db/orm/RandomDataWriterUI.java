package com.srirama.db.orm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class RandomDataWriterUI extends JFrame {
	private static final long serialVersionUID = -7829580570365602219L;
	private JTextField filePathField;
    private JTextField descriptorFileField;
    private JTextArea descriptorInputArea;
    private JTextArea dataLogger;
    private JButton toggleButton;
    private JSlider frequencySlider;
    private JLabel frequencyLabel;

    private volatile boolean running = false;
    private Thread writerThread;

    private final List<DataDescriptor> descriptors = new CopyOnWriteArrayList<>();
    private final Random random = new Random();
    private volatile int frequency = 1000;

    public RandomDataWriterUI() {
        setTitle("Random Data File Writer");
        setSize(850, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        filePathField = new JTextField("data.txt", 30);
        descriptorFileField = new JTextField("descriptor.txt", 30);

        descriptorInputArea = new JTextArea(4, 30);
        descriptorInputArea.setLineWrap(true);
        descriptorInputArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptorInputArea);
        descScrollPane.setBorder(BorderFactory.createTitledBorder("Custom Descriptor (optional)"));

        frequencySlider = new JSlider(10, 5000, 32);
        frequencyLabel = new JLabel("Write Frequency: " + frequencySlider.getValue() + "ms");
        frequencySlider.setMajorTickSpacing(1000);
        frequencySlider.setMinorTickSpacing(100);
        frequencySlider.setPaintTicks(true);
        frequencySlider.setPaintLabels(true);
        frequencySlider.addChangeListener(e -> {
            frequency = frequencySlider.getValue();
            frequencyLabel.setText("Write Frequency: " + frequency + " ms");
        });

        toggleButton = new JButton("Start");
        toggleButton.addActionListener(this::toggleWriter);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Output File Path:"), gbc);
        gbc.gridx = 1;
        formPanel.add(filePathField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Descriptor File Path:"), gbc);
        gbc.gridx = 1;
        formPanel.add(descriptorFileField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(frequencyLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(frequencySlider, gbc);

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        formPanel.add(descScrollPane, gbc);

        gbc.gridy++;
        formPanel.add(toggleButton, gbc);

        dataLogger = new JTextArea();
        dataLogger.setEditable(false);
        dataLogger.setBackground(Color.BLACK);
        dataLogger.setForeground(Color.GREEN);
        JScrollPane logScrollPane = new JScrollPane(dataLogger);

        add(formPanel, BorderLayout.NORTH);
        add(logScrollPane, BorderLayout.CENTER);
    }

    private void toggleWriter(ActionEvent e) {
        if (!running) {
            startWriting();
        } else {
            stopWriting();
        }
    }

    private void startWriting() {
        String filePath = filePathField.getText().trim();
        String descriptorPath = descriptorFileField.getText().trim();
        File outputFile = new File(filePath);

        String descriptorInput = descriptorInputArea.getText().trim();
        descriptors.clear();

        try {
            if (!descriptorInput.isEmpty()) {
                String[] tokens = descriptorInput.split("\\|");
                for (String token : tokens) {
                    descriptors.add(DataDescriptor.parse(token.trim()));
                }
            } else {
                File descriptorFile = new File(descriptorPath);
                if (!descriptorFile.exists()) {
                    JOptionPane.showMessageDialog(this, "Descriptor file not found.");
                    return;
                }
                List<String> lines = Files.readAllLines(descriptorFile.toPath());
                if (!lines.isEmpty()) {
                    String[] tokens = lines.get(0).split("\\|");
                    for (String token : tokens) {
                        descriptors.add(DataDescriptor.parse(token.trim()));
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading descriptors: " + ex.getMessage());
            return;
        }

        running = true;
        toggleButton.setText("Stop");

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
        toggleButton.setText("Start");
        if (writerThread != null) writerThread.interrupt();
    }

    private String generateRandomLine() {
        return descriptors.stream()
                .map(d -> d.generate(random))
                .collect(Collectors.joining("|"));
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

    static class DataDescriptor {
        enum Type { DATE, TIME, DOUBLE, STRING }

        Type type;
        double min, max;
        List<String> stringValues;

        public static DataDescriptor parse(String token) {
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
                d.min = 0; d.max = 1000;
                if (token.contains(",")) {
                    String[] range = token.replaceAll("[<>]", "").split(",")[1].split("-");
                    d.min = Double.parseDouble(range[0]);
                    d.max = Double.parseDouble(range[1]);
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
            return switch (type) {
                case DATE -> new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                case TIME -> LocalTime.now().withNano(0).toString();
                case DOUBLE -> String.format("%.2f", min + (max - min) * random.nextDouble());
                case STRING -> stringValues.get(random.nextInt(stringValues.size()));
            };
        }
    }
}
