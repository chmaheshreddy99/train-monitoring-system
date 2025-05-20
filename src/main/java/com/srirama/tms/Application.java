package com.srirama.tms;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.srirama.tms.dependencyijnection.SpringBeanInjector;
import com.srirama.tms.ui.HomePage;
import com.srirama.tms.ui.components.AppIcon;
import com.srirama.tms.ui.util.FontScaler;
import com.srirama.tms.ui.util.UIUtils;

public class Application {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				float scale = FontScaler.getScaleFactor();
			    UIUtils.applyGlobalFontScaling(scale);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			HomePage homePage = SpringBeanInjector.getApplicationContext().getBean(HomePage.class);
			homePage.init();
			homePage.setVisible(true);
			homePage.setIconImage(AppIcon.getIcon());
		});
	}
}
