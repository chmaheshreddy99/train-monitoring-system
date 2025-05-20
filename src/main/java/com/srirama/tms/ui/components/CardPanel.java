package com.srirama.tms.ui.components;

import java.awt.CardLayout;

import javax.swing.JPanel;

public class CardPanel extends JPanel {

	private static final long serialVersionUID = 7680550413109104907L;
	
	private CardLayout cardLayout;
	
    private DataTablePanel dataTablePanel;

    public CardPanel() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        addCards();
    }

    private void addCards() {
        dataTablePanel = new DataTablePanel();
        add(dataTablePanel, "DataTable");
    }

    public void showCard(String name) {
        cardLayout.show(this, name);
    }

    public DataTablePanel getDataTablePanel() {
        return dataTablePanel;
    }
}
