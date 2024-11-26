package com.example.shop_manager.Entity;

public class Report {
    private double totalRevenue;
    private String OrderID;
    private double expenses;

    public Report(double totalRevenue, String orderID, double expenses) {
        this.totalRevenue = totalRevenue;
        OrderID = orderID;
        this.expenses = expenses;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public double getExpenses() {
        return expenses;
    }

    public void setExpenses(double expenses) {
        this.expenses = expenses;
    }
}
