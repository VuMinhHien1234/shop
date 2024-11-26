
package com.example.shop_manager.Response;

import com.example.shop_manager.Entity.Customer;
import com.example.shop_manager.Entity.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class ProductDataBase extends JPanel {
    private final String URL = "jdbc:mysql://localhost:3306/shop";
    private final String User = "root";
    private final String Password = "nguyenthithuha";

    private JTextField txtId, txtName, txtPrice, txtQuantity;
    private JComboBox<String> cmbCategory;
    private JTable table;
    private DefaultTableModel model;
    private ArrayList<Product> productList = new ArrayList<>();

    public ProductDataBase() {
        setLayout(new BorderLayout());

        // Bảng sản phẩm
        String[] columnNames = {"Product ID", "Product Name", "Category", "Price", "Quantity"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Form nhập liệu
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2));

        panel.add(new JLabel("Product ID:"));
        txtId = new JTextField();
//        txtId.setEditable(false); // ID tự động tạo
        panel.add(txtId);

        panel.add(new JLabel("Product Name:"));
        txtName = new JTextField();
        panel.add(txtName);

        panel.add(new JLabel("Category:"));
        cmbCategory = new JComboBox<>(new String[]{"Electronics", "Home Appliances", "Food"});
        panel.add(cmbCategory);

        panel.add(new JLabel("Price:"));
        txtPrice = new JTextField();
        panel.add(txtPrice);

        panel.add(new JLabel("Quantity:"));
        txtQuantity = new JTextField();
        panel.add(txtQuantity);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        searchPanel.add(new JLabel("Search by ID:"));
        JTextField txtSearchId = new JTextField(10);
        searchPanel.add(txtSearchId);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchId = txtSearchId.getText();
                searchProductById(searchId);
            }
        });
        searchPanel.add(btnSearch);
        add(searchPanel, BorderLayout.NORTH);




        // Nút thêm sản phẩm
        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try (Connection connection = getConnection()) {
                    String name = txtName.getText().trim();
                    String category = (String) cmbCategory.getSelectedItem();
                    double price = Double.parseDouble(txtPrice.getText().trim());
                    int quantity = Integer.parseInt(txtQuantity.getText().trim());

                    if (name.isEmpty()) {
                        JOptionPane.showMessageDialog(table, "Product name cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    String sql = "INSERT INTO product(name, category, price, quantity) VALUES(?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, name);
                    statement.setString(2, category);
                    statement.setDouble(3, price);
                    statement.setInt(4, quantity);
                    int rowsInserted = statement.executeUpdate();

                    if (rowsInserted > 0) {
                        ResultSet generatedKeys = statement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int id = generatedKeys.getInt(1);
                            Product product = new Product(id, name, category, price, quantity);
                            productList.add(product);
                            model.addRow(new Object[]{id, name, category, price, quantity});
                            JOptionPane.showMessageDialog(table, "Product added successfully!");
                            clearFields();
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(table, "Please enter valid numbers for price and quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(table, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(btnAdd);

        // Nút cập nhật sản phẩm
        JButton btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(table, "Please select a product first!", "Selection Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try (Connection connection = getConnection()) {
                    int id = Integer.parseInt(txtId.getText().trim());
                    String name = txtName.getText().trim();
                    String category = (String) cmbCategory.getSelectedItem();
                    double price = Double.parseDouble(txtPrice.getText().trim());
                    int quantity = Integer.parseInt(txtQuantity.getText().trim());

                    if (name.isEmpty()) {
                        JOptionPane.showMessageDialog(table, "Product name cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    String sql = "UPDATE product SET name = ?, category = ?, price = ?, quantity = ? WHERE id = ?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, name);
                    statement.setString(2, category);
                    statement.setDouble(3, price);
                    statement.setInt(4, quantity);
                    statement.setInt(5, id);

                    int rowsUpdated = statement.executeUpdate();
                    if (rowsUpdated > 0) {
                        Product product = productList.get(selectedRow);
                        product.setName(name);
                        product.setCategory(category);
                        product.setPrice(price);
                        product.setQuantity(quantity);

                        model.setValueAt(name, selectedRow, 1);
                        model.setValueAt(category, selectedRow, 2);
                        model.setValueAt(price, selectedRow, 3);
                        model.setValueAt(quantity, selectedRow, 4);

                        JOptionPane.showMessageDialog(table, "Product updated successfully!");
                        clearFields();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(table, "Please enter valid numbers for price and quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(table, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(btnUpdate);

        // Nút xóa sản phẩm
        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(table, "Please select a product first!", "Selection Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(table, "Are you sure you want to delete this product?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                try (Connection connection = getConnection()) {
                    int id = Integer.parseInt(txtId.getText().trim());

                    String sql = "DELETE FROM product WHERE id = ?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, id);

                    int rowsDeleted = statement.executeUpdate();
                    if (rowsDeleted > 0) {
                        productList.remove(selectedRow);
                        model.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(table, "Product deleted successfully!");
                        clearFields();
                    } else {
                        JOptionPane.showMessageDialog(table, "Deletion failed! Product does not exist.", "Deletion Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(table, "Invalid Product ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(table, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(btnDelete);

        // Nút tải dữ liệu
        JButton btnLoadData = new JButton("Load Data");
        btnLoadData.addActionListener(e -> loadData());
        panel.add(btnLoadData);


        // Thêm MouseListener để hiển thị chi tiết sản phẩm khi nhấp vào hàng
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    txtId.setText(model.getValueAt(selectedRow, 0).toString());
                    txtName.setText(model.getValueAt(selectedRow, 1).toString());
                    cmbCategory.setSelectedItem(model.getValueAt(selectedRow, 2).toString());
                    txtPrice.setText(model.getValueAt(selectedRow, 3).toString());
                    txtQuantity.setText(model.getValueAt(selectedRow, 4).toString());
                }
            }
        });

        add(panel, BorderLayout.EAST);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, User, Password);
    }


    private void loadData() {
        model.setRowCount(0);
        productList.clear();

        try (Connection connection = getConnection()) {
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
                productList.add(product);
                model.addRow(new Object[]{id, name, category, price, quantity});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchProductById(String searchId) {
        model.setRowCount(0);
        productList.clear();
        try (Connection connection = DriverManager.getConnection(URL, User, Password)) {
            String sql = "SELECT * FROM Product WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, searchId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {

                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                Product product = new Product(id, name, category, price, quantity);
                productList.add(product);
                model.addRow(new Object[]{id, name, category, price, quantity});
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
        txtPrice.setText("");
        txtQuantity.setText("");
        cmbCategory.setSelectedIndex(0);
    }
}

