package com.srirama.tms.ui.components;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuBarBuilder {
	
    public static JMenuBar buildMenuBar(JFrame parent) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu(parent));
        menuBar.add(createEditMenu());
        menuBar.add(createHelpMenu());
        return menuBar;
    }

    private static JMenu createFileMenu(JFrame parent) {    	
        JMenu file = new JMenu("File");
        file.add(new JMenuItem("Open", AppIcon.getIcon("/ui/icons/icons8-folder-20.png")));
        file.add(new JMenuItem("Save", AppIcon.getIcon("/ui/icons/icons8-save-20.png")));
        file.add(createConfigureMenu(parent));
        file.addSeparator();
        file.add(new JMenuItem("Exit", AppIcon.getIcon("/ui/icons/icons8-exit-20.png")));
        return file;
    }

    private static JMenu createEditMenu() {
        JMenu edit = new JMenu("Edit");
        edit.add(new JMenuItem("Undo", AppIcon.getIcon("/ui/icons/icons8-undo-20.png")));
        edit.add(new JMenuItem("Redo", AppIcon.getIcon("/ui/icons/icons8-redo-20.png")));
        return edit;
    }

    private static JMenu createHelpMenu() {
        JMenu help = new JMenu("Help");
        help.add(new JMenuItem("About", AppIcon.getIcon("/ui/icons/icons8-help-20.png")));
        return help;
    }
    
    private static JMenuItem createConfigureMenu(JFrame parent) {
    	JMenuItem configure = new JMenuItem("Configure", AppIcon.getIcon("/ui/icons/icons8-configuration-20.png"));

        configure.addActionListener(e -> {
            ConfigurationDialog.showDialog(parent, null);
        });

        return configure;
    }

}
