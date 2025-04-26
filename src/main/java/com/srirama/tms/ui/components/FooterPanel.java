package com.srirama.tms.ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class FooterPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public FooterPanel() {
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        setBackground(new Color(75, 85, 99));
        setPreferredSize(new Dimension(0, 30));

        JLabel label = new JLabel("© 2025 My Application");
        label.setForeground(Color.WHITE);
        setBorder(new EmptyBorder(5, 10, 5, 10));
        add(label);
    }
}
