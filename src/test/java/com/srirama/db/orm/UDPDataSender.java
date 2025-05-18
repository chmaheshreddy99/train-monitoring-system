package com.srirama.db.orm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import com.srirama.tms.ui.components.AppIcon;

/**
 * UDPDataSender - A rich Swing UI for continuously sending UDP packets.
 * Supports manual and auto-generated payloads with real-time logging.
 */
public class UDPDataSender extends JFrame {

    private JTextField hostField;
    private JTextField portField;
    private JTextArea manualPayloadArea;
    private JTextArea logArea;
    private JRadioButton autoModeButton;
    private JRadioButton manualModeButton;
    private JButton startButton;
    private JButton stopButton;
    private volatile boolean running = false;
    private Thread senderThread;

    public UDPDataSender() {
        setTitle("UDP Data Sender");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setWindowsLookAndFeel();
        initUI();
        setIcon();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel configPanel = createConfigPanel();
        JPanel logPanel = createLogPanel();

        mainPanel.add(configPanel, BorderLayout.NORTH);
        mainPanel.add(logPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Configuration", TitledBorder.LEFT, TitledBorder.TOP));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        hostField = new JTextField("localhost", 15);
        portField = new JTextField("9000", 5);
        manualPayloadArea = new JTextArea(3, 25);
        manualPayloadArea.setLineWrap(true);
        manualPayloadArea.setText("Hello from UDP Sender!");

        autoModeButton = new JRadioButton("Auto Generate Data", true);
        manualModeButton = new JRadioButton("Manual Data");
        ButtonGroup group = new ButtonGroup();
        group.add(autoModeButton);
        group.add(manualModeButton);

        startButton = new JButton("Start Sending");
        stopButton = new JButton("Stop Sending");
        stopButton.setEnabled(false);

        int y = 0;

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Host:"), gbc);
        gbc.gridx = 1;
        panel.add(hostField, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Port:"), gbc);
        gbc.gridx = 1;
        panel.add(portField, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Mode:"), gbc);
        gbc.gridx = 1;
        panel.add(autoModeButton, gbc);

        y++;
        gbc.gridx = 1; gbc.gridy = y;
        panel.add(manualModeButton, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y;
        gbc.gridwidth = 2;
        panel.add(new JScrollPane(manualPayloadArea), gbc);

        y++;
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.add(startButton);
        buttonsPanel.add(stopButton);
        gbc.gridy = y;
        panel.add(buttonsPanel, gbc);

        startButton.addActionListener(e -> startSending());
        stopButton.addActionListener(e -> stopSending());

        return panel;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Data Log", TitledBorder.LEFT, TitledBorder.TOP));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(logArea);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void startSending() {
        String host = hostField.getText().trim();
        int port;

        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Invalid port number.");
            return;
        }

        running = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        logArea.setText("");

        senderThread = new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket()) {
                InetAddress address = InetAddress.getByName(host);
                Random random = new Random();

                while (running) {
                    String payload = autoModeButton.isSelected()
                            ? generateRandomPayload(random)
                            : manualPayloadArea.getText().trim();

                    if (payload.isEmpty()) {
                        log("Warning: Skipping empty manual payload...");
                        continue;
                    }

                    byte[] sendData = payload.getBytes();

                    DatagramPacket packet = new DatagramPacket(sendData, sendData.length, address, port);
                    socket.send(packet);

                    log(payload);

                    Thread.sleep(200); // Sending interval
                }
            } catch (Exception ex) {
                log("Error: " + ex.getMessage());
                showError(ex.getMessage());
            } finally {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        });

        senderThread.start();
    }

    private void stopSending() {
        running = false;
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        log("Stopped sending.");
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setWindowsLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }

    private void setIcon() {
        try {
            setIconImage(new ImageIcon(getClass().getResource("/icons/udp.png")).getImage());
        } catch (Exception e) {
            // Icon is optional
        }
    }

    /**
     * Generates a random payload with 20 float values separated by '|'.
     * Each value is rounded to 2 decimal places.
     *
     * @param random Random object
     * @return Generated payload string
     */
    private String generateRandomPayload(Random random) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            float value = random.nextFloat() * 100;
            sb.append(String.format("%.2f", value));
            if (i < 19) {
                sb.append("|");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
    	
        SwingUtilities.invokeLater(() -> {
            UDPDataSender sender = new UDPDataSender();
            sender.setIconImage(AppIcon.getIcon());
            sender.setVisible(true);
        });
    }
}
