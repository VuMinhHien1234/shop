package com.example.shop_manager.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDataBase extends JPanel {
    private JTextField txtCustomerID; // TextField for manual input
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTotalAmount; // Label to display total amount
    private String URL = "jdbc:mysql://localhost:3306/shop";
    private String User = "root";
    private String Password = "nguyenthithuha";

    public ReportDataBase() {
        setLayout(new BorderLayout());

        // Initialize table model with "Invoice ID" column added
        String[] columnNames = {"Invoice ID", "Customer ID", "Customer Name", "Product ID", "Price", "Quantity"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Main control panel for buttons and inputs
        JPanel controlPanel = new JPanel(new GridLayout(2, 1)); // Two rows for the layout

        JPanel panelRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnPrintInvoices = new JButton("Print All Invoices");
        btnPrintInvoices.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printAllInvoices();
            }
        });
        panelRow1.add(btnPrintInvoices);

        JPanel panelRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelRow2.add(new JLabel("Enter Customer ID:"));

        txtCustomerID = new JTextField(10);
        panelRow2.add(txtCustomerID);

        JButton btnSearchInvoices = new JButton("Search");
        btnSearchInvoices.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchInvoices();
            }
        });
        panelRow2.add(btnSearchInvoices);

        lblTotalAmount = new JLabel("Total Amount: 0.0");
        panelRow2.add(lblTotalAmount);

        // Add rows to the main control panel
        controlPanel.add(panelRow1);
        controlPanel.add(panelRow2);

        // Add the control panel to the bottom of the layout
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void printAllInvoices() {
        try (Connection connection = DriverManager.getConnection(URL, User, Password)) {
            String sql = "SELECT o.id AS invoice_id, c.id AS customer_id, c.name AS customer_name, p.id AS product_id, p.price, op.quantity " +
                    "FROM `Order` o " +
                    "LEFT JOIN Order_Customer oc ON o.id = oc.id_order " +
                    "LEFT JOIN Customer c ON oc.id_customer = c.id " +
                    "LEFT JOIN Order_Product op ON o.id = op.order_id " +
                    "LEFT JOIN Product p ON op.product_id = p.id";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            // Clear the table before populating it with new data
            model.setRowCount(0);

            double totalAmount = 0.0;
            while (resultSet.next()) {
                String invoiceID = resultSet.getString("invoice_id");
                String customerID = resultSet.getString("customer_id");
                String customerName = resultSet.getString("customer_name");
                String productID = resultSet.getString("product_id");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                double amount = price * quantity;
                totalAmount += amount;

                // Add data to table model
                model.addRow(new Object[]{invoiceID, customerID, customerName, productID, price, quantity});
            }

            lblTotalAmount.setText("Total Amount: " + totalAmount);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data from database: " + ex.getMessage());
        }
    }

    private void searchInvoices() {
        String customerID = txtCustomerID.getText().trim();
        if (customerID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Customer ID.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(URL, User, Password)) {
            String sql = "SELECT c.id AS customer_id, c.name AS customer_name, " +
                    "GROUP_CONCAT(DISTINCT p.id) AS product_ids, " +
                    "SUM(p.price * op.quantity) AS total_amount " +
                    "FROM `Order` o " +
                    "LEFT JOIN Order_Customer oc ON o.id = oc.id_order " +
                    "LEFT JOIN Customer c ON oc.id_customer = c.id " +
                    "LEFT JOIN Order_Product op ON o.id = op.order_id " +
                    "LEFT JOIN Product p ON op.product_id = p.id " +
                    "WHERE c.id = ? " +
                    "GROUP BY c.id, c.name " +
                    "ORDER BY total_amount DESC";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, customerID);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Clear the table before populating it with new data
            model.setRowCount(0);

            while (resultSet.next()) {
                String customerIDResult = resultSet.getString("customer_id");
                String customerName = resultSet.getString("customer_name");
                String productIDs = resultSet.getString("product_ids");
                double totalAmount = resultSet.getDouble("total_amount");

                // Add data to table model
                model.addRow(new Object[]{customerIDResult, customerName, productIDs, totalAmount});
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data from database: " + ex.getMessage());
        }
    }

}
