package com.srirama.tms.ui.components;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.srirama.tms.metrics.Metric;
import com.srirama.tms.metrics.MetricGroup;
import com.srirama.tms.service.MetricService;

public class ConfigurationDialog extends JDialog {

    private static final long serialVersionUID = 8565810675809248763L;

    private JTree availableTree;
    private JList<String> selectedList;
    private DefaultListModel<String> selectedModel;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private MetricService metricService;

    public ConfigurationDialog(Frame parent) {
        super(parent, "Train Monitoring - Metric Configuration", true); // Modal
        this.metricService = new MetricService();
        initUI();
    }

    private void initUI() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());

        selectedModel = new DefaultListModel<>();
        selectedList = new JList<>(selectedModel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLeftPane(), createRightPane());
        splitPane.setDividerLocation(400);

        JPanel bottomButtons = new JPanel();
        JButton okButton = new JButton(AppIcon.getIcon("/ui/icons/icons8-save-20.png"));
        okButton.setToolTipText("Save");
        JButton cancelButton = new JButton(AppIcon.getIcon("/ui/icons/icons8-exit-20.png"));
        cancelButton.setToolTipText("Cancel");

        okButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> onCancel());

        bottomButtons.add(okButton);
        bottomButtons.add(cancelButton);

        getContentPane().add(splitPane, BorderLayout.CENTER);
        getContentPane().add(bottomButtons, BorderLayout.SOUTH);
    }

    private JPanel createLeftPane() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Available Metrics"));

        availableTree = new JTree();
        availableTree.setRootVisible(false);
        availableTree.setShowsRootHandles(true);
        availableTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        availableTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath[] paths = availableTree.getSelectionPaths();
                    if (paths != null) {
                        for (TreePath path : paths) {
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                            if (node.isLeaf()) {
                                moveSelectedToRight(node);
                            }
                        }
                    }
                }
            }
        });

        loadAvailableMetrics();

        JScrollPane scrollPane = new JScrollPane(availableTree);

        // Enable drag from JTree
        availableTree.setDragEnabled(true);
        availableTree.setTransferHandler(new TreeTransferHandler());

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRightPane() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Selected Metrics"));

        selectedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        selectedList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    moveSelectedToLeft();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(selectedList);

        // Enable drag from JList
        selectedList.setDragEnabled(true);
        selectedList.setTransferHandler(new ListTransferHandler());

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void moveSelectedToRight(DefaultMutableTreeNode node) {
        String metricName = node.getUserObject().toString();
        if (!selectedModel.contains(metricName)) {
            selectedModel.addElement(metricName);
        }
    }

    private void moveSelectedToLeft() {
        List<String> selectedValues = selectedList.getSelectedValuesList();
        for (String selected : selectedValues) {
            selectedModel.removeElement(selected);
        }
    }

    private void loadAvailableMetrics() {
        List<MetricGroup> groups = metricService.getMetricGroups();

        rootNode = new DefaultMutableTreeNode("Metrics");

        for (MetricGroup group : groups) {
            DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(group.getGroupName());
            for (Metric metric : group.getMetrics()) {
                groupNode.add(new DefaultMutableTreeNode(metric.getName()));
            }
            rootNode.add(groupNode);
        }

        treeModel = new DefaultTreeModel(rootNode);
        availableTree.setModel(treeModel);

        // Expand all nodes efficiently
        expandAllNodes();
    }

    private void expandAllNodes() {
        // Start from the root, expand each group and its children (metrics).
        expandNodeAndChildren(rootNode);
    }

    private void expandNodeAndChildren(DefaultMutableTreeNode node) {
        // If the node has children, expand it
        TreePath path = new TreePath(node.getPath());
        availableTree.expandPath(path);

        // Iterate through each child node (group -> metrics)
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            expandNodeAndChildren(childNode); // Recurse for deeper levels (metric level)
        }
    }

    private void onSave() {
        System.out.println("Saving selected metrics...");
        for (int i = 0; i < selectedModel.size(); i++) {
            System.out.println("- " + selectedModel.get(i));
        }
        dispose(); // Close the dialog
    }

    private void onCancel() {
        dispose(); // Simply close without saving
    }

    // TransferHandler for JTree
    private class TreeTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 5624458840881900767L;

		@Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            JTree tree = (JTree) c;
            TreePath[] paths = tree.getSelectionPaths();
            if (paths == null) {
                return null;
            }
            List<String> metricNames = new ArrayList<>();
            for (TreePath path : paths) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (node.isLeaf()) {
                    String metricName = node.getUserObject().toString();
                    if (!selectedModel.contains(metricName)) {
                        metricNames.add(metricName);
                    }
                }
            }
            if (metricNames.isEmpty()) {
                return null;
            }
            return new StringSelection(String.join("\n", metricNames));
        }

        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            try {
                Transferable t = support.getTransferable();
                String data = (String) t.getTransferData(DataFlavor.stringFlavor);
                String[] metricNames = data.split("\n");
                for (String metricName : metricNames) {
                    if (!selectedModel.contains(metricName)) {
                        selectedModel.addElement(metricName);
                    }
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    // TransferHandler for JList
    private class ListTransferHandler extends TransferHandler {

		private static final long serialVersionUID = -5532498849101388027L;

		@Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            JList<?> list = (JList<?>) c;
            List<?> selectedValues = list.getSelectedValuesList();
            if (selectedValues.isEmpty()) {
                return null;
            }
            List<String> metricNames = new ArrayList<>();
            for (Object value : selectedValues) {
                metricNames.add(value.toString());
            }
            return new StringSelection(String.join("\n", metricNames));
        }

        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            try {
                Transferable t = support.getTransferable();
                String data = (String) t.getTransferData(DataFlavor.stringFlavor);
                String[] metricNames = data.split("\n");
                for (String metricName : metricNames) {
                    if (!selectedModel.contains(metricName)) {
                        selectedModel.addElement(metricName);
                    }
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public static void showDialog(Frame parent) {
        ConfigurationDialog dialog = new ConfigurationDialog(parent);
        dialog.setVisible(true);
    }

}