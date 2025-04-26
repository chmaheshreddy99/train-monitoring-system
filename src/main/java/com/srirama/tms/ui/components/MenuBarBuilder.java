package com.srirama.tms.ui.components;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuBarBuilder {
	
    public static JMenuBar buildMenuBar(JFrame parent) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createEditMenu());
        menuBar.add(createHelpMenu());
        menuBar.add(createConfigureMenu(parent));
        return menuBar;
    }

    private static JMenu createFileMenu() {
        JMenu file = new JMenu("File");
        file.add(new JMenuItem("Open"));
        file.add(new JMenuItem("Save"));
        file.addSeparator();
        file.add(new JMenuItem("Exit"));
        return file;
    }

    private static JMenu createEditMenu() {
        JMenu edit = new JMenu("Edit");
        edit.add(new JMenuItem("Undo"));
        edit.add(new JMenuItem("Redo"));
        return edit;
    }

    private static JMenu createHelpMenu() {
        JMenu help = new JMenu("Help");
        help.add(new JMenuItem("About"));
        return help;
    }
    
    private static JMenu createConfigureMenu(JFrame parent) {
        JMenu configure = new JMenu("Configure");

        JMenuItem configureMetricsItem = new JMenuItem("Configure Metrics");
        configureMetricsItem.addActionListener(e -> {
            ConfigurationDialog.showDialog(parent);
        });

        configure.add(configureMetricsItem);
        return configure;
    }

}
