package com.srirama.tms.ui.components;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class CardPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public CardPanel() {
        setLayout(new CardLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 255));
        initializeCards();
    }

    private void initializeCards() {
        String[] cards = {"Export", "Save", "Print", "Settings", "Configure"};
        for (String cardName : cards) {
            add(createCard(cardName), cardName);
        }
    }

    private JPanel createCard(String name) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(245, 245, 255));
        JLabel label = new JLabel("This is the " + name + " view", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 18));
        card.add(label, BorderLayout.CENTER);
        return card;
    }

    public void showCard(String name) {
        CardLayout layout = (CardLayout) getLayout();
        layout.show(this, name);
    }
}
