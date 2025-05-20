package com.srirama.tms.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.srirama.tms.config.DataLoggerConfig;
import com.srirama.tms.dependencyijnection.SpringBeanInjector;
import com.srirama.tms.service.DataConsumerService;
import com.srirama.tms.ui.HomePage;

public class DataTablePanel extends JPanel {

    private static final long serialVersionUID = -6245981872628229928L;
    private static final Logger logger = LoggerFactory.getLogger(DataTablePanel.class);

    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private boolean isUserAdjustingVerticalScrollBar = false;
    private static final int MAX_DATA_POINTS = 1000;

    @Autowired
    private DataConsumerService dataConsumerService;
    
    @Autowired
    private DataLoggerConfig dataLoggerConfig;
    
    @Autowired
    private HomePage homePage;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private JPanel chartPanelContainer;
    private JFreeChart chart;
    private DefaultCategoryDataset dataset;
    private final Set<Integer> selectedColumns = new HashSet<>();
    private final Map<Integer, List<Double>> columnDataMap = new HashMap<>();
    private JButton toggleGraphButton;
    private JButton toggleDataLoggerButton;
    private JButton configureButton;
    private boolean isGraphVisible = false;
    private boolean isDataLoggerStarted;

    public DataTablePanel() {
        SpringBeanInjector.inject(this);
        setLayout(new BorderLayout());
        
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.getModel().addChangeListener((ChangeEvent e) -> {
            isUserAdjustingVerticalScrollBar = verticalScrollBar.getValueIsAdjusting();
        });
        
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                   styleTable();
                }
            }
        });        

        initSouthPanel();
        startAutoDataFeed();
    }
    
    private void initiateDataLogger() {
    	if(!dataLoggerConfig.isConfigChanged()) {
    		return;
    	}
        Object[] columnNames = dataLoggerConfig.getSelectedMetricParams()
        		.stream()
        		.map(metricParam -> metricParam.getName())
        		.toArray();

        tableModel.setColumnIdentifiers(columnNames);
        tableModel.setRowCount(0);
        table.setModel(tableModel);
        setupColumnSelectionListener();
        styleTable();
        dataLoggerConfig.setConfigChanged(false);
    }

    private void styleTable() {
        table.setBackground(Color.BLACK);
        table.setForeground(Color.GREEN);
        table.setGridColor(Color.BLACK);
        table.setSelectionBackground(Color.DARK_GRAY);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        FontMetrics headerFontMetrics = header.getFontMetrics(header.getFont());
        FontMetrics cellFontMetrics = table.getFontMetrics(table.getFont());
        int padding = 20;
        int minWidth = 80;
        for (int col = 0; col < table.getColumnCount(); col++) {
            TableColumn column = table.getColumnModel().getColumn(col);
            String headerText = table.getColumnName(col);
            int headerWidth = headerFontMetrics.stringWidth(headerText);
            int maxCellWidth = 0;
            for (int row = 0; row < table.getRowCount(); row++) {
                Object value = table.getValueAt(row, col);
                if (value != null) {
                    int cellWidth = cellFontMetrics.stringWidth(value.toString());
                    maxCellWidth = Math.max(maxCellWidth, cellWidth);
                }
            }
            int preferredWidth = Math.max(minWidth, Math.max(headerWidth, maxCellWidth) + padding);
            column.setPreferredWidth(preferredWidth);
        }
    }



    private void initSouthPanel() {
        JPanel southPanel = new JPanel(new BorderLayout());
        dataset = new DefaultCategoryDataset();
        chart = ChartFactory.createLineChart(
            "Live Data Graph", "Time", "Value", dataset,
            PlotOrientation.VERTICAL, true, true, false
        );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 300));
        chartPanelContainer = new JPanel(new BorderLayout());
        chartPanelContainer.add(chartPanel, BorderLayout.CENTER);
        chartPanelContainer.setVisible(isGraphVisible);
        
        // Create button panel
        JPanel buttonPanel = new JPanel();
        toggleGraphButton = new JButton(AppIcon.getIcon("/ui/icons/icons8-graph-20.png"));
        toggleGraphButton.setText("Show Graph");
        toggleDataLoggerButton = new JButton(AppIcon.getIcon("/ui/icons/icons8-start-20.png"));
        toggleDataLoggerButton.setText("Start");
        
        configureButton = new JButton(AppIcon.getIcon("/ui/icons/icons8-configure-20.png"));
        configureButton.setText("Configure");
        configureButton.setToolTipText("Configure metric parameters");
        
        JButton exportButton = new JButton(AppIcon.getIcon("/ui/icons/icons8-excel-20.png"));
        exportButton.setText("Export");

        toggleGraphButton.setToolTipText("Hide or show the live data graph");
        toggleGraphButton.addActionListener(e -> toggleGraphVisibility());
        toggleDataLoggerButton.addActionListener(e -> toggleDataLogger());
        configureButton.addActionListener(e -> {
        	ConfigurationDialog.showDialog(homePage, this::initiateDataLogger);
        });
        
        buttonPanel.add(toggleGraphButton);
        buttonPanel.add(toggleDataLoggerButton);
        buttonPanel.add(configureButton);
        buttonPanel.add(exportButton);

        southPanel.add(buttonPanel, BorderLayout.NORTH);
        southPanel.add(chartPanelContainer, BorderLayout.CENTER);

        add(southPanel, BorderLayout.SOUTH);
    }

    private void toggleGraphVisibility() {
        isGraphVisible = !isGraphVisible;
        chartPanelContainer.setVisible(isGraphVisible);
        toggleGraphButton.setText(isGraphVisible ? "Remove Graph" : "Show Graph");
        toggleGraphButton.setIcon(AppIcon.getIcon("/ui/icons/icons8-graph-20.png"));
        logger.debug("Graph visibility toggled to: {}", isGraphVisible);
        revalidate();
        repaint();
    }
    
	private void toggleDataLogger() {
		initiateDataLogger();
		isDataLoggerStarted = !isDataLoggerStarted;
		toggleDataLoggerButton.setText(isDataLoggerStarted ? "Stop" : "Start");
		if (isDataLoggerStarted) {
			toggleDataLoggerButton.setIcon(AppIcon.getIcon("/ui/icons/icons8-stop-20.png"));
		} else {
			toggleDataLoggerButton.setIcon(AppIcon.getIcon("/ui/icons/icons8-start-20.png"));
		}
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
        	SwingUtilities.invokeLater(() -> scrollToBottom());
        }
    }

    private void updateChart() {
        if (!isGraphVisible) return;
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