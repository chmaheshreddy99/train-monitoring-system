package com.srirama.tms.ui.components;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class CardPanel extends JPanel {

	private static final long serialVersionUID = 7680550413109104907L;
	
	private CardLayout cardLayout;
	
    private DataTablePanel dataTablePanel;

    public CardPanel() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 255)); // subtle branded tone

        addCards();
    }

    private void addCards() {
        String[] cardNames = {"Export", "Save", "Print", "Settings", "Configure"};
        for (String name : cardNames) {
            JPanel panel = createSimplePanel(name);
            add(panel, name);
        }

        // Initialize and add the DataTablePanel as a separate card
        dataTablePanel = new DataTablePanel();
        add(dataTablePanel, "DataTable");
    }

    private JPanel createSimplePanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 255));

        JLabel label = new JLabel("This is the " + title + " view", SwingConstants.CENTER);
        label.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 18));
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    public void showCard(String name) {
        cardLayout.show(this, name);
    }

    public DataTablePanel getDataTablePanel() {
        return dataTablePanel;
    }
}
