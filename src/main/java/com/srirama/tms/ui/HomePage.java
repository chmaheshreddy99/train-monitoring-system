package com.srirama.tms.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.srirama.tms.listener.UdpPacketListener;
import com.srirama.tms.ui.components.CardPanel;
import com.srirama.tms.ui.components.DataTablePanel;
import com.srirama.tms.ui.components.FooterPanel;
import com.srirama.tms.ui.components.HeaderPanel;
import com.srirama.tms.ui.components.MenuBarBuilder;
import com.srirama.tms.ui.components.SidePanel;

@Component
public class HomePage extends JFrame {

    private static final long serialVersionUID = 2696909680742711072L;
    private CardPanel cardPanel;
    private DataTablePanel dataTablePanel;
    
    @Autowired
    private UdpPacketListener udpListener;
    
    public void init() {
        initializeFrame();
        initializeComponents();
        udpListener.start();
    }

    private void initializeFrame() {
        setTitle("Train Monitoring System.");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1378, 768);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initializeComponents() {
        setJMenuBar(MenuBarBuilder.buildMenuBar(this));

        cardPanel = new CardPanel();
        dataTablePanel = cardPanel.getDataTablePanel(); // <-- Get DataTablePanel reference

        SidePanel sidePanel = new SidePanel(cardPanel);

        add(new HeaderPanel(), BorderLayout.NORTH);
        add(new FooterPanel(), BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(cardPanel, BorderLayout.CENTER);
        centerPanel.add(sidePanel, BorderLayout.EAST);	

        add(centerPanel, BorderLayout.CENTER);
    }
}
