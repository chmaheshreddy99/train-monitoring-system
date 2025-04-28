package com.srirama.tms.ui.components;

import javax.swing.ImageIcon;
import java.awt.Image;

public class AppIcon {
    private static final Image ICON = new ImageIcon(
            AppIcon.class.getResource("/ui/icons/icons8-technology-96.png")).getImage();

    public static Image getIcon() {
        return ICON;
    }
    
    public static ImageIcon getIcon(String path) {
    	return new ImageIcon(AppIcon.class.getResource(path));
    }
}
