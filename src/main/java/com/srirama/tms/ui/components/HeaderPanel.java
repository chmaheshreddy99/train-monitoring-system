package com.srirama.tms.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.srirama.tms.dependencyijnection.SpringBeanInjector;

public class HeaderPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public HeaderPanel() {
    	SpringBeanInjector.inject(this);
        setLayout(new BorderLayout());
        setBackground(new Color(58, 104, 168));
        setPreferredSize(new Dimension(0, 40));

        JLabel title = new JLabel("RailLogix", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel home = new JLabel(AppIcon.getIcon("/ui/icons/icons8-technology-48.png"));
        home.setText("Home");
        home.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(home, BorderLayout.WEST);
        add(title, BorderLayout.CENTER);
    }
}
