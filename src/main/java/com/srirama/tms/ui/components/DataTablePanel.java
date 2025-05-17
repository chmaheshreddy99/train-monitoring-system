package com.srirama.tms.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.springframework.beans.factory.annotation.Autowired;

import com.srirama.tms.dependencyijnection.SpringBeanInjector;
import com.srirama.tms.service.DataConsumerService;

public class DataTablePanel extends JPanel {

    private static final long serialVersionUID = -6245981872628229928L;

    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private boolean isUserAdjustingVerticalScrollBar = false;
    private static final int COLUMN_WIDTH_PIXELS = 113;

    @Autowired
    private DataConsumerService dataConsumerService;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public DataTablePanel() {
        SpringBeanInjector.inject(this);
        setLayout(new BorderLayout());

        // Column setup
        String[] columnNames = new String[20];
        for (int i = 0; i < 20; i++) {
            columnNames[i] = "Column " + (i + 1);
        }

        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        styleTable();

        scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.getModel().addChangeListener((ChangeEvent e) -> {
            isUserAdjustingVerticalScrollBar = verticalScrollBar.getValueIsAdjusting();
        });

        startAutoDataFeed();
    }

    private void styleTable() {
        table.setBackground(Color.BLACK);
        table.setForeground(Color.GREEN);
        table.setGridColor(Color.GRAY);
        table.setSelectionBackground(Color.DARK_GRAY);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(COLUMN_WIDTH_PIXELS);
            column.setWidth(COLUMN_WIDTH_PIXELS);
        }
    }

    private void addRow(Object[] rowData) {
        tableModel.addRow(rowData);
        if (!isUserAdjustingVerticalScrollBar) {
            scrollToBottom();
        }
    }

    private void scrollToBottom() {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
    }

    private void startAutoDataFeed() {
        Timer timer = new Timer(200, e -> fetchAndAddRowAsync());
        timer.start();
    }

    private void fetchAndAddRowAsync() {
        executor.submit(() -> {
            try {
                String[] data = dataConsumerService.fetchData();
                if (data != null && data.length > 0) {
                	for(String t : data) {
                		SwingUtilities.invokeLater(() -> addRow(t.split("\\|")));
                	}
                }
            } catch (Exception ex) {
                ex.printStackTrace(); // or use proper logging
            }
        });
    }
}
