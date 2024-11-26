package com.example.shop_manager.Entity;

public class Inventory {
    private String products;

    public Inventory(String products) {
        this.products = products;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }
}
