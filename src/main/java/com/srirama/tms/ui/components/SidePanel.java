package com.srirama.tms.ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class SidePanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private CardPanel cardPanel;

    public SidePanel(CardPanel cardPanel) {
        this.cardPanel = cardPanel;
        initializeSidePanel();
    }

    private void initializeSidePanel() {
        setLayout(new GridLayout(5, 1, 10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(150, 0));
        setBackground(new Color(230, 235, 245));

        String[] buttonLabels = {"Export", "DataTable", "Print", "Settings", "Configure"};
        for (String label : buttonLabels) {
            add(createButton(label));
        }
    }

    private JButton createButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("SansSerif", Font.PLAIN, 13));
        button.setFocusPainted(false);
        button.addActionListener(e -> cardPanel.showCard(label));
        return button;
    }
}
