package com.example.shop_manager.Service;

import javax.swing.*;

import com.example.shop_manager.Entity.Product;
import com.example.shop_manager.Response.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ProductApp extends javax.swing.JFrame{
    private JFrame frame;
    private JTabbedPane tabbedPane;

    public ProductApp() {
        frame = new JFrame("SHOP MANANGEMENT");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Tạo tabbed pane để chứa các giao diện
        tabbedPane = new JTabbedPane();

        // Thêm từng giao diện vào tab
        tabbedPane.addTab("Customer", new CustomerDataBase());
        tabbedPane.addTab("Product", new ProductDataBase());
        tabbedPane.addTab("Order", new OrderDataBase());
        tabbedPane.addTab("Report", new ReportDataBase());
        tabbedPane.addTab("Inventory", new InventoryDataBase());

        frame.add(tabbedPane);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProductApp::new);
    }
}

