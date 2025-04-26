package com.srirama.tms.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.srirama.tms.ui.components.CardPanel;
import com.srirama.tms.ui.components.DataTablePanel;
import com.srirama.tms.ui.components.FooterPanel;
import com.srirama.tms.ui.components.HeaderPanel;
import com.srirama.tms.ui.components.MenuBarBuilder;
import com.srirama.tms.ui.components.SidePanel;

public class HomePage extends JFrame {

    private static final long serialVersionUID = 2696909680742711072L;
    private CardPanel cardPanel;
    private DataTablePanel dataTablePanel;

    public HomePage() {
        initializeFrame();
        initializeComponents();
        populateInitialTableData();
        startAutoDataFeed(); // <-- NEW
    }

    private void initializeFrame() {
        setTitle("Train Monitoring System.");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1378, 768);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initializeComponents() {
        setJMenuBar(MenuBarBuilder.buildMenuBar(this));

        cardPanel = new CardPanel();
        dataTablePanel = cardPanel.getDataTablePanel(); // <-- Get DataTablePanel reference

        SidePanel sidePanel = new SidePanel(cardPanel);

        add(new HeaderPanel(), BorderLayout.NORTH);
        add(new FooterPanel(), BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(cardPanel, BorderLayout.CENTER);
        centerPanel.add(sidePanel, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void populateInitialTableData() {
        for (int row = 1; row <= 10; row++) {
            Object[] rowData = new Object[20];
            for (int col = 0; col < 20; col++) {
                rowData[col] = "Data " + row + "-" + (col + 1);
            }
            dataTablePanel.addRow(rowData);
        }
    }

    private void startAutoDataFeed() {
        Timer timer = new Timer(200, e -> {  // every 2 seconds
            Object[] rowData = new Object[20];
            for (int col = 0; col < 20; col++) {
                rowData[col] = "Auto " + System.currentTimeMillis() % 100000 + "-" + (col + 1);
            }
            dataTablePanel.addRow(rowData);
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            new HomePage().setVisible(true);
        });
    }
}
