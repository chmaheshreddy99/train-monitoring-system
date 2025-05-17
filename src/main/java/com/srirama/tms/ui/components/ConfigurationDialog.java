package com.srirama.tms.ui.components;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.springframework.beans.factory.annotation.Autowired;

import com.srirama.tms.dependencyijnection.SpringBeanInjector;
import com.srirama.tms.entity.MetricParameter;
import com.srirama.tms.service.MetricService;

public class ConfigurationDialog extends JDialog {

    private static final long serialVersionUID = 8565810675809248763L;

    private JTree availableTree;
    private JList<MetricParameter> selectedList;
    private DefaultListModel<MetricParameter> selectedModel;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    
    @Autowired
    private MetricService metricService;

    public ConfigurationDialog(Frame parent) {
        super(parent, "Train Monitoring - Metric Configuration", true); // Modal
        SpringBeanInjector.inject(this);
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
        cancelButton.addActionListener(e -> onSend());

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

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void moveSelectedToRight(DefaultMutableTreeNode node) {
        MetricParameter metricParameter = (MetricParameter)node.getUserObject();
        if (!selectedModel.contains(metricParameter)) {
            selectedModel.addElement(metricParameter);
        }
    }

    private void moveSelectedToLeft() {
        List<MetricParameter> selectedValues = selectedList.getSelectedValuesList();
        for (MetricParameter selected : selectedValues) {
            selectedModel.removeElement(selected);
        }
    }

    private void loadAvailableMetrics() {
    	
        List<String> groups = metricService.getAllMetricGroups();
        rootNode = new DefaultMutableTreeNode("Metrics");
        for (String group : groups) {
            DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(group);
            for (MetricParameter metric : metricService.getMetricsByGroup(group)) {
            	DefaultMutableTreeNode leafNode = new DefaultMutableTreeNode(metric.getName());
            	leafNode.setUserObject(metric);
                groupNode.add(leafNode);
            }
            rootNode.add(groupNode);
        }

        treeModel = new DefaultTreeModel(rootNode);
        availableTree.setModel(treeModel);

        expandAllNodes();
    }

    private void expandAllNodes() {
        expandNodeAndChildren(rootNode);
    }

    private void expandNodeAndChildren(DefaultMutableTreeNode node) {
        TreePath path = new TreePath(node.getPath());
        availableTree.expandPath(path);

        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            expandNodeAndChildren(childNode); // Recurse for deeper levels (metric level)
        }
    }

	private void onSave() {
		List<MetricParameter> selectedParameters = IntStream.range(0, selectedModel.size())
				.mapToObj(selectedModel::getElementAt).toList();
		metricService.saveParameterPreferences(selectedParameters);
		dispose();
	}


    private void onCancel() {
        dispose();
    }
    
	private void onSend() {
		List<MetricParameter> selectedParameters = IntStream.range(0, selectedModel.size())
				.mapToObj(selectedModel::getElementAt).toList();
		ProgressDialog progressDialog = new ProgressDialog(this, "Processing");
		progressDialog.startBackgroundTask(() -> metricService.send(selectedParameters));
	}

    public static void showDialog(Frame parent) {
        ConfigurationDialog dialog = new ConfigurationDialog(parent);
        dialog.setVisible(true);
    }

}