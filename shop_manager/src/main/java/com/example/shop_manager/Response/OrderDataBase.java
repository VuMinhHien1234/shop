package com.example.shop_manager.Response;

import com.example.shop_manager.Entity.Order;
import com.example.shop_manager.Entity.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class OrderDataBase extends JPanel {
    private final String URL = "jdbc:mysql://localhost:3306/shop";
    private final String User = "root";
    private final String Password = "nguyenthithuha";

    private JTextField txtCustomerId, txtProductId, txtQuantity,txtOrderId;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private ArrayList<Object[]> orderList = new ArrayList<>();
    private HashMap<String, String> customerMap = new HashMap<>();
    private HashMap<String, Double> productMap = new HashMap<>();
    private HashMap<String, Integer> product_quantity = new HashMap<>();
    private HashMap<String, Integer> order_id = new HashMap<>();
    private int currentOrderId = 1; // To generate order ID

    public OrderDataBase() {
        setLayout(new BorderLayout());

        // Table columns
        String[] columnNames = {"Order ID", "Customer ID", "Customer Name", "Product ID", "Price", "Quantity", "Total"};
        tableModel = new DefaultTableModel(columnNames, 0);
        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(scrollPane, BorderLayout.CENTER);

        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2));

        inputPanel.add(new JLabel("Order ID:"));
        txtOrderId = new JTextField();
        inputPanel.add(txtOrderId);

        inputPanel.add(new JLabel("Customer ID:"));
        txtCustomerId = new JTextField();
        inputPanel.add(txtCustomerId);

        inputPanel.add(new JLabel("Product ID:"));
        txtProductId = new JTextField();
        inputPanel.add(txtProductId);

        inputPanel.add(new JLabel("Quantity:"));
        txtQuantity = new JTextField();
        inputPanel.add(txtQuantity);

        // Add buttons
        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(e -> addOrder());
        inputPanel.add(btnAdd);

        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(e -> deleteOrder());
        inputPanel.add(btnDelete);

        JButton btnEdit = new JButton("Update");
        btnEdit.addActionListener(e -> updateOrder());
        inputPanel.add(btnEdit);

        JButton btnLoadData = new JButton("LoadData");
        btnLoadData.addActionListener(e -> loadData());
        inputPanel.add(btnLoadData);

        orderTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow != -1) {
                    String id = orderTable.getValueAt(selectedRow, 0).toString();
                    String customer_id = orderTable.getValueAt(selectedRow, 1).toString();
                    String product_id = orderTable.getValueAt(selectedRow, 3).toString();
                    String quantity = orderTable.getValueAt(selectedRow, 5).toString();
                    txtOrderId.setText(id);
                    txtCustomerId.setText(customer_id);
                    txtProductId.setText(product_id);
                    txtQuantity.setText(quantity);
                }
            }
        });

        add(inputPanel, BorderLayout.EAST);
    }



    private void addOrder() {
        try (Connection conn = DriverManager.getConnection(URL, User, Password)) {
            String customerId = txtCustomerId.getText().trim();
            String productId = txtProductId.getText().trim();
            int quantity = Integer.parseInt(txtQuantity.getText().trim());


            String customerName = customerMap.get(customerId);
            if (customerName == null) {
                String query = "SELECT name FROM Customer WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, customerId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    customerName = rs.getString("name");
                    customerMap.put(customerId, customerName);
                } else {
                    JOptionPane.showMessageDialog(this, "Customer ID not found");
                    return;
                }
            }

            // Retrieve product price
            Double price = productMap.get(productId);
            if (price == null) {
                String query = "SELECT price FROM Product WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, productId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    price = rs.getDouble("price");
                    productMap.put(productId, price);
                } else {
                    JOptionPane.showMessageDialog(this, "Product ID not found");
                    return;
                }
            }

            double total = price * quantity;

            // Insert into table
            Object[] rowData = {currentOrderId++, customerId, customerName, productId, price, quantity, total};
            tableModel.addRow(rowData);
            orderList.add(rowData);

            clearFields();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void deleteOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow != -1) {
            orderList.remove(selectedRow);
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to delete");
        }
    }

    private void updateOrder() {
        int selectedRow = orderTable.getSelectedRow();
        String customerId = txtCustomerId.getText().trim();
        String productId = txtProductId.getText().trim();
        String orderId = txtOrderId.getText().trim();
        int quantity = Integer.parseInt(txtQuantity.getText().trim());


        if (selectedRow != -1) {
            try (Connection conn = DriverManager.getConnection(URL, User, Password)) {
                // Bắt đầu giao dịch
                conn.setAutoCommit(false);


                Double price = productMap.get(productId);

                if (price == null) {
                    String query = "SELECT price,quantity FROM Product WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, productId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        price = rs.getDouble("price");
                        quantity = rs.getInt("quantity");
                        productMap.put(productId, price);
                        product_quantity.put(productId,quantity);
                    } else {
                        JOptionPane.showMessageDialog(this, "Product not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                double total = price * quantity;

                int quantity_productleft = product_quantity.get(productId);
                int quantity_input = Integer.parseInt(txtQuantity.getText().trim());
                String sqlUpdateOrder = "UPDATE `Order` SET totalprice = ? WHERE id = ?";
                try (PreparedStatement statementOrder = conn.prepareStatement(sqlUpdateOrder)) {
                    statementOrder.setDouble(1, total);  // Set total price
                    statementOrder.setString(2, orderId);  // Set the order id for update

                    int rowsUpdatedOrder = statementOrder.executeUpdate();
                    if (rowsUpdatedOrder <= 0) {
                        JOptionPane.showMessageDialog(this, "Order not found or update failed.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                    int old_quantity_order;
                    String query = "SELECT quantity FROM Order_product WHERE order_id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, orderId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        old_quantity_order=rs.getInt("quantity");
                    } else {
                        JOptionPane.showMessageDialog(this, "Product not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }



                // Cập nhật quantity trong bảng Product_Order

                String sqlUpdateProduct_Order = "UPDATE Order_Product SET quantity = ? WHERE order_id= ?";
                try (PreparedStatement statementProduct = conn.prepareStatement(sqlUpdateProduct_Order)) {
                    statementProduct.setInt(1, quantity_input);  // Subtract purchased quantity from product stock
                    statementProduct.setString(2, orderId);  // Use the product id for update

                    int rowsUpdatedProduct = statementProduct.executeUpdate();
                    if (rowsUpdatedProduct <= 0) {
                        JOptionPane.showMessageDialog(this, "Product update failed.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                String sqlUpdateProduct = "UPDATE Product SET quantity = ? WHERE id= ?";
                try (PreparedStatement statementProduct = conn.prepareStatement(sqlUpdateProduct)) {
                    statementProduct.setInt(1,quantity_productleft-(quantity_input-old_quantity_order));
                    statementProduct.setString(2, productId);

                    int rowsUpdatedProduct = statementProduct.executeUpdate();
                    if (rowsUpdatedProduct <= 0) {
                        JOptionPane.showMessageDialog(this, "Product update failed.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                conn.commit();

                tableModel.setValueAt(customerId, selectedRow, 1);
                tableModel.setValueAt(productId, selectedRow, 3);
                tableModel.setValueAt(price, selectedRow, 4);
                tableModel.setValueAt(quantity, selectedRow, 5);
                tableModel.setValueAt(total, selectedRow, 6);

                JOptionPane.showMessageDialog(this, "Order and product quantity updated successfully!");
                clearFields();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to update");
        }
    }



    private void loadData() {
        tableModel.setRowCount(0);
        orderList.clear();
        try (Connection conn = DriverManager.getConnection(URL, User, Password)) {
            String sql= """
         SELECT
                 o.id AS order_id,
                 c.id AS customer_id,
                 c.name AS customer_name,
                 p.id AS product_id,
                 p.price,
                 op.quantity,
                 (p.price * op.quantity) AS total
             FROM
                 `Order` o
             JOIN
                 Order_Customer oc ON o.id = oc.id_order
             JOIN
                 Customer c ON oc.id_customer = c.id
             JOIN
                 Order_Product op ON o.id = op.order_id
             JOIN
                 Product p ON op.product_id = p.id
        """;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                Object[] rowData = {
                        rs.getString("order_id"),
                        rs.getString("customer_id"),
                        rs.getString("customer_name"),
                        rs.getInt("product_id"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getDouble("total")
                };

                tableModel.addRow(rowData);
                orderList.add(rowData); // Assuming orderList is a list to store order data
            }
        } catch (Exception ex) {
            System.out.println("Database error: " + ex.getMessage());
        }

    }

    private void clearFields() {
        txtCustomerId.setText("");
        txtProductId.setText("");
        txtQuantity.setText("");
    }
}
