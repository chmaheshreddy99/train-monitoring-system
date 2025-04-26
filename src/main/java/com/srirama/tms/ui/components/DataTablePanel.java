package com.srirama.tms.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.JScrollBar;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class DataTablePanel extends JPanel {

    private static final long serialVersionUID = -6245981872628229928L;
    
    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private boolean isUserAdjustingVerticalScrollBar = false;
    private static final int COLUMN_WIDTH_PIXELS = 113; // Approx. 3cm at 96 DPI

    public DataTablePanel() {
        setLayout(new BorderLayout());

        // Initialize the table with 20 columns and 0 initial rows
        String[] columnNames = new String[20];
        for (int i = 0; i < 20; i++) {
            columnNames[i] = "Column " + (i + 1);
        }

        tableModel = new DefaultTableModel(columnNames, 0);  // No rows initially
        table = new JTable(tableModel);

        // Set up table appearance
        table.setBackground(Color.BLACK);
        table.setForeground(Color.GREEN);
        table.setGridColor(Color.GRAY);
        table.setSelectionBackground(Color.DARK_GRAY);

        // Disable auto-resize to allow horizontal scrolling
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Set fixed column width (approx. 3cm)
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(COLUMN_WIDTH_PIXELS);
            column.setWidth(COLUMN_WIDTH_PIXELS);
        }

        scrollPane = new JScrollPane(table);
        // Ensure horizontal scrollbar is always available if needed
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);  // Add scrollable table to panel

        // Add listener to track user interaction with the vertical scrollbar
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                isUserAdjustingVerticalScrollBar = verticalScrollBar.getValueIsAdjusting();
            }
        });
    }

    public void addRow(Object[] rowData) {
        tableModel.addRow(rowData);

        // Auto-scroll vertically to the bottom only if the user is not adjusting the vertical scrollbar
        if (!isUserAdjustingVerticalScrollBar) {
            scrollToBottom();
        }
        // No action taken on horizontal scrollbar to preserve user position
    }

    private void scrollToBottom() {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
    }
}