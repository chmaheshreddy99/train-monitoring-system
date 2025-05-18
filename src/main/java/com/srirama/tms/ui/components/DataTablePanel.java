package com.srirama.tms.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.srirama.tms.dependencyijnection.SpringBeanInjector;
import com.srirama.tms.service.DataConsumerService;

public class DataTablePanel extends JPanel {

    private static final long serialVersionUID = -6245981872628229928L;
    private static final Logger logger = LoggerFactory.getLogger(DataTablePanel.class);

    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private boolean isUserAdjustingVerticalScrollBar = false;
    private static final int COLUMN_WIDTH_PIXELS = 113;
    private static final int MAX_DATA_POINTS = 1000;

    @Autowired
    private DataConsumerService dataConsumerService;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private JPanel chartPanelContainer;
    private JFreeChart chart;
    private DefaultCategoryDataset dataset;
    private final Set<Integer> selectedColumns = new HashSet<>();
    private final Map<Integer, List<Double>> columnDataMap = new HashMap<>();
    private JButton toggleGraphButton;
    private boolean isGraphVisible = true;

    public DataTablePanel() {
        SpringBeanInjector.inject(this);
        setLayout(new BorderLayout());

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

        initSouthPanel();
        setupColumnSelectionListener();
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

    private void initSouthPanel() {
        // Create south panel to hold button and chart
        JPanel southPanel = new JPanel(new BorderLayout());
        // Initialize chart
        dataset = new DefaultCategoryDataset();
        chart = ChartFactory.createLineChart(
            "Live Data Graph", "Time", "Value", dataset,
            PlotOrientation.VERTICAL, true, true, false
        );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 300));
        chartPanelContainer = new JPanel(new BorderLayout());
        chartPanelContainer.add(chartPanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);
        toggleGraphButton = new JButton(AppIcon.getIcon("/ui/icons/icons8-graph-20.png"));
        //toggleGraphButton.setBackground(Color.BLACK);
        //toggleGraphButton.setForeground(Color.GREEN);
        //toggleGraphButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        toggleGraphButton.setToolTipText("Hide or show the live data graph");
        toggleGraphButton.addActionListener(e -> toggleGraphVisibility());
        buttonPanel.add(toggleGraphButton);

        // Add components to south panel
        southPanel.add(buttonPanel, BorderLayout.NORTH);
        southPanel.add(chartPanelContainer, BorderLayout.CENTER);

        // Add south panel to main panel
        add(southPanel, BorderLayout.SOUTH);
    }

    private void toggleGraphVisibility() {
        isGraphVisible = !isGraphVisible;
        chartPanelContainer.setVisible(isGraphVisible);
        toggleGraphButton.setText(isGraphVisible ? "Remove Graph" : "Show Graph");
        toggleGraphButton.setIcon(AppIcon.getIcon("/ui/icons/icons8-graph-20.png"));
        logger.debug("Graph visibility toggled to: {}", isGraphVisible);
        // Revalidate and repaint to ensure layout updates
        revalidate();
        repaint();
    }

    private void setupColumnSelectionListener() {
        table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedColumns.clear();
                for (int i = 0; i < table.getColumnCount(); i++) {
                    if (table.isColumnSelected(i)) {
                        selectedColumns.add(i);
                        columnDataMap.putIfAbsent(i, new ArrayList<>());
                    }
                }
                updateChart();
            }
        });
    }

    private void addRow(Object[] rowData) {
        tableModel.addRow(rowData);

        for (int colIndex : selectedColumns) {
            if (colIndex < rowData.length) {
                try {
                    double value = Double.parseDouble(rowData[colIndex].toString());
                    List<Double> dataList = columnDataMap.computeIfAbsent(colIndex, k -> new ArrayList<>());
                    dataList.add(value);
                    if (dataList.size() > MAX_DATA_POINTS) {
                        dataList.remove(0);
                    }
                } catch (NumberFormatException ignored) {
                    logger.warn("Invalid number format in row data for column {}: {}", colIndex, rowData[colIndex]);
                }
            }
        }

        updateChart();

        if (!isUserAdjustingVerticalScrollBar) {
            scrollToBottom();
        }
    }

    private void updateChart() {
        if (!isGraphVisible) return; // Skip chart updates if graph is hidden
        dataset.clear();
        for (int col : selectedColumns) {
            List<Double> values = columnDataMap.getOrDefault(col, List.of());
            for (int i = 0; i < values.size(); i++) {
                dataset.addValue(values.get(i), "Column " + (col + 1), String.valueOf(i));
            }
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
                    for (String t : data) {
                        Object[] rowData = t.split("\\|");
                        SwingUtilities.invokeLater(() -> addRow(rowData));
                    }
                }
            } catch (Exception ex) {
                logger.error("Failed to fetch data: {}", ex.getMessage(), ex);
            }
        });
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        executor.shutdown();
    }
}