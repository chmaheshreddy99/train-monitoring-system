package com.srirama.tms.ui.util;

import java.awt.*;

public class FontScaler {
    public static Font scaleFont(Font baseFont, float scale) {
        return baseFont.deriveFont(baseFont.getSize2D() * scale);
    }

    public static float getScaleFactor() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int dpi = toolkit.getScreenResolution();
        return dpi / 96f; // 96 is the baseline DPI for "100%" scaling
    }
}
