package com.homestaysiaton.Objects;

import java.util.ArrayList;

public class Order {
    String id;
    String customer;
    ArrayList<String> deliveryAddress;
    String deliveryOption;
    String status;
    long timestamp;
    double total;
    ArrayList<Unit> arrOrderItems;

    public Order() {
    }

    public Order(String id, String customer, ArrayList<String> deliveryAddress, String deliveryOption, String status, long timestamp, double total, ArrayList<Unit> arrOrderItems) {
        this.id = id;
        this.customer = customer;
        this.deliveryAddress = deliveryAddress;
        this.deliveryOption = deliveryOption;
        this.status = status;
        this.timestamp = timestamp;
        this.total = total;
        this.arrOrderItems = arrOrderItems;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public ArrayList<String> getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(ArrayList<String> deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryOption() {
        return deliveryOption;
    }

    public void setDeliveryOption(String deliveryOption) {
        this.deliveryOption = deliveryOption;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public ArrayList<Unit> getArrOrderItems() {
        return arrOrderItems;
    }

    public void setArrOrderItems(ArrayList<Unit> arrOrderItems) {
        this.arrOrderItems = arrOrderItems;
    }
}
