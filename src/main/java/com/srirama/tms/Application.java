package com.srirama.tms;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.srirama.tms.ui.HomePage;
import com.srirama.tms.ui.components.AppIcon;

public class Application {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			HomePage homePage = new HomePage();
			homePage.setVisible(true);
			homePage.setIconImage(AppIcon.getIcon());
		});
	}
}
