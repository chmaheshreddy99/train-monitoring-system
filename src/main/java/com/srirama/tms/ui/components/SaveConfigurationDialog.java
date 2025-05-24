package com.srirama.tms.ui.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class SaveConfigurationDialog extends JDialog {

	private static final long serialVersionUID = -5886801144327713608L;
	private JLabel statusLabel;
	private JTextField configNameField;
	private boolean autoDispose = true;
	private BiConsumer<String, Consumer<Boolean>> submitCallback;

	public SaveConfigurationDialog(JDialog parent, BiConsumer<String, Consumer<Boolean>> callback) {
		super(parent, "Save configuration", true);
		submitCallback = callback;
		setSize(500, 200);
		setLocationRelativeTo(parent);
		setUndecorated(false);

		JPanel inputPanel = new JPanel(new GridBagLayout());
		JLabel label = new JLabel("Configuration Name:");

		configNameField = new JTextField(20);
		
		inputPanel.add(label);
		inputPanel.add(configNameField);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton submitButton = new JButton(AppIcon.getIcon("/ui/icons/icons8-save-20.png"));
		submitButton.setText("Save");

		submitButton.setToolTipText("Save configuration name");
		submitButton.addActionListener(e -> onSubmit());
		buttonPanel.add(submitButton);

		JButton cancelButton = new JButton(AppIcon.getIcon("/ui/icons/icons8-exit-20.png"));
		cancelButton.setToolTipText("Cancel");
		cancelButton.setText("Cancel");

		cancelButton.addActionListener(e -> onCancel());
		buttonPanel.add(cancelButton);

		getContentPane().add(inputPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setVisible(true);
	}

	private void onCancel() {
		dispose();
	}

	private void onSubmit() {
		submitCallback.accept(configNameField.getText().trim(), submitResult -> showResult(submitResult));
	}

	public void setAutoDispose(boolean autoDispose) {
		this.autoDispose = autoDispose;
	}

	private void showResult(boolean success) {
		System.out.println("submit callback.....");
		statusLabel =  new JLabel();
        if(success) {
        	statusLabel.setIcon(AppIcon.getIcon("/ui/icons/icons8-success-20.png"));
        	statusLabel.setText("Successfully configured the parameters.");
        }
        else {
        	statusLabel.setIcon(AppIcon.getIcon("/ui/icons/icons8-error-20.png"));
        	statusLabel.setText("Parameter configuration failed.");
        }
		getContentPane().removeAll();
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(statusLabel, BorderLayout.CENTER);
		getContentPane().revalidate();
	    getContentPane().repaint();
		autoDispose();
	}

	private void autoDispose() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		if (autoDispose) {
			Timer timer = new Timer(3000, e -> dispose());
			timer.setRepeats(false);
			timer.start();
		}
	}
}
