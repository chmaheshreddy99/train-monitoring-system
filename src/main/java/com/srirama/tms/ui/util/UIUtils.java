package com.srirama.tms.ui.util;

import java.awt.Font;

import javax.swing.UIManager;

public class UIUtils {
    public static void applyGlobalFontScaling(float scaleFactor) {
        Font baseFont = UIManager.getFont("Label.font");
        Font scaledFont = baseFont.deriveFont(baseFont.getSize2D() * scaleFactor);

        for (Object key : UIManager.getLookAndFeelDefaults().keySet()) {
            if (key.toString().toLowerCase().contains("font")) {
                UIManager.put(key, scaledFont);
            }
        }
    }
}
