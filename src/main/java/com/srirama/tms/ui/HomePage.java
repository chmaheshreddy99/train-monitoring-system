package com.srirama.tms.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class HomePage extends JFrame {

	private static final long serialVersionUID = 2696909680742711072L;

	public HomePage() {
	    setTitle("Home Page - Swing App");
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setSize(900, 600);
	    setLocationRelativeTo(null);
	    setLayout(new BorderLayout());

	    try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch (Exception e) {
	        System.out.println("Failed to set look and feel");
	    }

	    // Menu bar
	    setJMenuBar(createMenuBar());

	    // Header
	    JPanel header = new JPanel();
	    header.setBackground(new Color(70, 130, 180));
	    header.setPreferredSize(new Dimension(0, 60));
	    header.setLayout(new FlowLayout(FlowLayout.LEFT));
	    JLabel title = new JLabel("Home Page");
	    title.setForeground(Color.WHITE);
	    title.setFont(new Font("SansSerif", Font.BOLD, 24));
	    header.add(title);

	    // Footer
	    JPanel footer = new JPanel();
	    footer.setBackground(new Color(240, 240, 240));
	    footer.setPreferredSize(new Dimension(0, 40));
	    footer.setLayout(new FlowLayout(FlowLayout.RIGHT));
	    footer.add(new JLabel("© 2025 My Application"));

	    // Side Panel (Right side now)
	    JPanel sidePanel = new JPanel(new GridLayout(5, 1, 10, 10));
	    sidePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
	    sidePanel.setPreferredSize(new Dimension(180, 0));
	    sidePanel.setBackground(new Color(245, 245, 245));

	    String[] buttonLabels = {"Export", "Save", "Print", "Settings", "Configure"};
	    for (String label : buttonLabels) {
	        JButton button = new JButton(label);
	        //button.setFocusPainted(false);
	        button.setBackground(new Color(60, 130, 200));
	        //button.setForeground(Color.WHITE);
	        button.setFont(new Font("SansSerif", Font.BOLD, 14));
	        button.setPreferredSize(new Dimension(160, 40));
//	        button.setBorder(BorderFactory.createCompoundBorder(
//	            BorderFactory.createLineBorder(new Color(50, 100, 180), 1),
//	            new EmptyBorder(10, 20, 10, 20)
//	        ));
	        sidePanel.add(button);
	    }

	    // Main content
	    JPanel mainPanel = new JPanel();
	    mainPanel.setLayout(new GridLayout(2, 2, 10, 10));
	    mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
	    mainPanel.setBackground(Color.WHITE);

	    for (int i = 1; i <= 4; i++) {
	        JPanel panel = new JPanel();
	        panel.setBackground(new Color(230, 230, 250));
	        panel.setBorder(BorderFactory.createTitledBorder("Content " + i));
	        mainPanel.add(panel);
	    }

	    // Center area
	    JPanel center = new JPanel(new BorderLayout());
	    center.add(mainPanel, BorderLayout.CENTER);
	    center.add(sidePanel, BorderLayout.EAST); // moved to right

	    add(header, BorderLayout.NORTH);
	    add(center, BorderLayout.CENTER);
	    add(footer, BorderLayout.SOUTH);
	}

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.add(new JMenuItem("Open"));
        file.add(new JMenuItem("Save"));
        file.addSeparator();
        file.add(new JMenuItem("Exit"));

        JMenu edit = new JMenu("Edit");
        edit.add(new JMenuItem("Undo"));
        edit.add(new JMenuItem("Redo"));

        JMenu help = new JMenu("Help");
        help.add(new JMenuItem("About"));

        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(help);

        return menuBar;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HomePage app = new HomePage();
            app.setVisible(true);
        });
    }
}
