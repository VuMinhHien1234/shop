package com.example.shop_manager.Response;

import com.example.shop_manager.Entity.Customer;
import com.example.shop_manager.Entity.Product;


import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class CustomerDataBase extends JPanel {

    private JTextField txtId, txtName, txtAddress, txtPhoneNumber;
    private JTable table;
    private DefaultTableModel model;
    private ArrayList<Customer> customerList = new ArrayList<>();

    private String URL = "jdbc:mysql://localhost:3306/shop";
    private String User = "root";
    private String Password = "nguyenthithuha";

    public CustomerDataBase() {
        setLayout(new BorderLayout());


        String[] columnNames = {"ID", "Name", "Address", "Phone Number"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);


        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));

        panel.add(new JLabel("ID:"));
        txtId = new JTextField();
        panel.add(txtId);

        panel.add(new JLabel("Name:"));
        txtName = new JTextField();
        panel.add(txtName);

        panel.add(new JLabel("Address:"));
        txtAddress = new JTextField();
        panel.add(txtAddress);

        panel.add(new JLabel("Phone Number:"));
        txtPhoneNumber = new JTextField();
        panel.add(txtPhoneNumber);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        searchPanel.add(new JLabel("Search by ID:"));
        JTextField txtSearchId = new JTextField(10);
        searchPanel.add(txtSearchId);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchId = txtSearchId.getText();
                searchCustomerById(searchId);
            }
        });
        searchPanel.add(btnSearch);


        add(searchPanel, BorderLayout.NORTH);


        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String id = model.getValueAt(selectedRow, 0).toString();
                    String name = model.getValueAt(selectedRow, 1).toString();
                    String address = model.getValueAt(selectedRow, 2).toString();
                    String phoneNumber = model.getValueAt(selectedRow, 3).toString();
                    txtId.setText(id);
                    txtName.setText(name);
                    txtAddress.setText(address);
                    txtPhoneNumber.setText(phoneNumber);
                }
            }
        });


        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try (Connection connection = DriverManager.getConnection(URL, User, Password)){
                    String id = txtId.getText();
                    String name = txtName.getText();
                    String address = txtAddress.getText();
                    String phoneNumber = txtPhoneNumber.getText();

                    String sql = "INSERT INTO customer (id, name, address, phoneNumber) VALUES (?,?,?,?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, id);
                    statement.setString(2, name);
                    statement.setString(3, address);
                    statement.setString(4, phoneNumber);

                    int rowsUpdated = statement.executeUpdate();
                    if (rowsUpdated > 0) {
                        Customer customer = new Customer(id, name, address, phoneNumber);
                        customerList.add(customer);
                        model.addRow(new Object[]{id, name, address, phoneNumber});

                        JOptionPane.showMessageDialog(table, "Customer updated successfully!");
                        clearFields();
                    }
                }catch(SQLException e1){
                    JOptionPane.showMessageDialog(table, "Please enter valid data");
                }
            }
        });
        panel.add(btnAdd);

        // Update Button
        JButton btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(table, "Please select a product first!", "Selection Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try (Connection connection = DriverManager.getConnection(URL, User, Password)){
                    String id = txtId.getText();
                    String name = txtName.getText();
                    String address = txtAddress.getText();
                    String phoneNumber = txtPhoneNumber.getText();

                    String sql = "UPDATE Customer SET name = ?, address = ?, phoneNumber = ? WHERE id = ?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, name);
                    statement.setString(2, address);
                    statement.setString(3, phoneNumber);
                    statement.setString(4, id);
                    int rowsUpdated = statement.executeUpdate();
                    if (rowsUpdated > 0) {
                        Customer customer = customerList.get(selectedRow);
                        customer.setId(id);
                        customer.setName(name);
                        customer.setAddress(address);
                        customer.setPhoneNumber(phoneNumber);

                        model.setValueAt(id, selectedRow, 0);
                        model.setValueAt(name, selectedRow, 1);
                        model.setValueAt(address, selectedRow, 2);
                        model.setValueAt(phoneNumber, selectedRow, 3);
                        JOptionPane.showMessageDialog(table, "Customer updated successfully!");
                        clearFields();
                    }
                }catch(SQLException e1){
                    JOptionPane.showMessageDialog(table, "Please enter valid data");
                }

            }
        });
        panel.add(btnUpdate);


        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int confirm = JOptionPane.showConfirmDialog(table, "Are you sure you want to delete this product?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm != JOptionPane.YES_OPTION) {
                        return;
                    }

                    Customer customer = customerList.get(selectedRow);

                    try (Connection connection = DriverManager.getConnection(URL, User, Password)) {
                        String sql = "DELETE FROM Customer WHERE id = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, customer.getId());
                        int rowsDeleted = statement.executeUpdate();

                        if (rowsDeleted > 0) {
                            customerList.remove(selectedRow);
                            model.removeRow(selectedRow);
                            JOptionPane.showMessageDialog(table, "Customer deleted successfully: " + customer.getId());
                        } else {
                            JOptionPane.showMessageDialog(table, "Deletion failed! Customer does not exist");
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(table, "Database connection error: " + e1.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(table, "Please select a row to delete!");
                }
            }
        });
        panel.add(btnDelete);


        JButton btnLoadData = new JButton("Load Data");
        btnLoadData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.setRowCount(0);
                customerList.clear();
                try (Connection connection = DriverManager.getConnection(URL, User, Password)) {
                    String sql = "SELECT * from customer";
                    Statement st = connection.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while (rs.next()) {
                        String id = rs.getString("id");
                        String name = rs.getString("name");
                        String address = rs.getString("address");
                        String phoneNumber = rs.getString("phoneNumber");

                        Customer customer = new Customer(id, name, address, phoneNumber);
                        customerList.add(customer);

                        Object row[] = {id, name, address, phoneNumber};
                        model.addRow(row);
                    }

                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
        panel.add(btnLoadData);


        add(panel, BorderLayout.EAST);
    }
    private void searchCustomerById(String searchId) {

        try (Connection connection = DriverManager.getConnection(URL, User, Password)) {
            String sql = "SELECT * FROM Customer WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, searchId);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {

                String id = rs.getString("id");
                String name = rs.getString("name");
                String address = rs.getString("address");
                String phoneNumber = rs.getString("phoneNumber");
                model.setRowCount(0);
                model.addRow(new Object[]{id, name, address, phoneNumber});
            } else {
                JOptionPane.showMessageDialog(table, "Customer not found with ID: " + searchId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(table, "Database error: " + e.getMessage());
        }
    }
    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtAddress.setText("");
        txtPhoneNumber.setText("");
    }
}