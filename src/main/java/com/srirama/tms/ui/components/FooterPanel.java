package com.srirama.tms.ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class FooterPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public FooterPanel() {
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        setBackground(new Color(75, 85, 99));
        setPreferredSize(new Dimension(0, 25));

        JLabel label = new JLabel("© 2025 Sriram Aditya IT Solutions Pvt. Ltd. All rights reserved.");
        label.setForeground(Color.WHITE);
        add(label);
    }
}
