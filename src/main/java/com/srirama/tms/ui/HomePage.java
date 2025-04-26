package com.srirama.tms.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.srirama.tms.ui.components.CardPanel;
import com.srirama.tms.ui.components.FooterPanel;
import com.srirama.tms.ui.components.HeaderPanel;
import com.srirama.tms.ui.components.MenuBarBuilder;
import com.srirama.tms.ui.components.SidePanel;

public class HomePage extends JFrame {

    private static final long serialVersionUID = 2696909680742711072L;
    private CardPanel cardPanel;

    public HomePage() {
        initializeFrame();
        initializeComponents();
    }

    private void initializeFrame() {
        setTitle("Home Page - Swing App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1378, 768);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initializeComponents() {
        setJMenuBar(MenuBarBuilder.buildMenuBar());

        cardPanel = new CardPanel();
        SidePanel sidePanel = new SidePanel(cardPanel);

        add(new HeaderPanel(), BorderLayout.NORTH);
        add(new FooterPanel(), BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(cardPanel, BorderLayout.CENTER);
        centerPanel.add(sidePanel, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HomePage().setVisible(true);
        });
    }
}
