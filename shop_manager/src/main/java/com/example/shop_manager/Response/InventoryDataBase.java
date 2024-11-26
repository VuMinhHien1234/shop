package com.example.shop_manager.Response;

import com.example.shop_manager.Entity.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class InventoryDataBase extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JButton btnShow;
    private String URL = "jdbc:mysql://localhost:3306/shop";
    private String User = "root";
    private String Password = "nguyenthithuha";

    public InventoryDataBase() {
        setLayout(new BorderLayout());


        String[] columnNames = {"Product ID", "Product Name", "Category", "Price", "Quantity Remaining"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);


        JPanel panel = new JPanel();
        btnShow = new JButton("Show");
        panel.add(btnShow);


        btnShow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try (Connection connection = DriverManager.getConnection(URL, User, Password);) {
                    String sql = "SELECT * FROM product";
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);

                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String name = resultSet.getString("name");
                        String category = resultSet.getString("category");
                        double price = resultSet.getDouble("price");
                        int quantity = resultSet.getInt("quantity");

                        Product product = new Product(id, name, category, price, quantity);
                        model.addRow(new Object[]{id, name, category, price, quantity});
                    }
                } catch (SQLException e1) {
                    JOptionPane.showMessageDialog(table, "Database Error: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(panel, BorderLayout.SOUTH);
    }


}
