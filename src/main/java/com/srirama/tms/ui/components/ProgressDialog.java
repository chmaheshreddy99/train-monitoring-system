package com.srirama.tms.ui.components;

import java.awt.BorderLayout;
import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class ProgressDialog extends JDialog {

	private static final long serialVersionUID = -5886801144327713608L;
	private final JProgressBar progressBar;
    private final JLabel statusLabel;
    private boolean autoDispose = true;

    public ProgressDialog(JDialog parent, String title) {
        super(parent, title, true);
        setSize(500, 200);
        setLocationRelativeTo(parent);
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setUndecorated(false);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        statusLabel = new JLabel("Processing, please wait...");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(statusLabel, BorderLayout.CENTER);
        getContentPane().add(progressBar, BorderLayout.SOUTH);
    }
    
	public void setAutoDispose(boolean autoDispose) {
		this.autoDispose = autoDispose;
	}

	public void startBackgroundTask(Supplier<Boolean> task) {
        new Thread(() -> {
            try {
                boolean success = task.get();
                SwingUtilities.invokeLater(() -> showResult(success));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> showResult(false));
            }
        }).start();
        setVisible(true);
    }

    private void showResult(boolean success) {
        progressBar.setIndeterminate(false);
        progressBar.setVisible(false);
        if(success) {
        	statusLabel.setIcon(AppIcon.getIcon("/ui/icons/icons8-success-20.png"));
        	statusLabel.setText("Successfully configured the parameters.");
        }
        else {
        	statusLabel.setIcon(AppIcon.getIcon("/ui/icons/icons8-error-20.png"));
        	statusLabel.setText("Parameter configuration failed.");
        }
        autoDispose();
    }
    
	private void autoDispose() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getRootPane().registerKeyboardAction(e -> dispose(), 
				KeyStroke.getKeyStroke("ESCAPE"),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		if (autoDispose) {
			Timer timer = new Timer(2000, e -> dispose());
			timer.setRepeats(false);
			timer.start();
		}
	}
}
