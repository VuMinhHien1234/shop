package com.example.shop_manager.Entity;

import java.util.Map;
import java.util.Map;

public class Order {
    private String id;
    private String customerId;
    private double totalPrice;
    private String status;
    private Map<Product, Integer> products; // Lưu danh sách sản phẩm và số lượng của chúng

    // Constructor đầy đủ
    public Order(String id, String customerId, Map<Product, Integer> products, double totalPrice, String status) {
        this.id = id;
        this.customerId = customerId;
        this.products = products;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // Getter và Setter cho các thuộc tính
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<Product, Integer> getProducts() {
        return products;
    }

    public void setProducts(Map<Product, Integer> products) {
        this.products = products;
    }
}
