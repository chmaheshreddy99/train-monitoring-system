package com.srirama.tms.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class HeaderPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public HeaderPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(58, 104, 168));
        setPreferredSize(new Dimension(0, 45));

        JLabel title = new JLabel("TRAIN MONITORING SYSTEM", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(title, BorderLayout.CENTER);
    }
}
