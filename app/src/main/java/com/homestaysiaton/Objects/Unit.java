package com.homestaysiaton.Objects;

public class Unit {
    String id;
    int quantity;
    String unitName;
    String unitDetails;
    double price;
    Long thumbnail;

    public Unit() {
    }

    public Unit(String id, int quantity, String unitName, String unitDetails, double price, Long thumbnail) {
        this.id = id;
        this.quantity = quantity;
        this.unitName = unitName;
        this.unitDetails = unitDetails;
        this.price = price;
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitDetails() {
        return unitDetails;
    }

    public void setUnitDetails(String unitDetails) {
        this.unitDetails = unitDetails;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Long thumbnail) {
        this.thumbnail = thumbnail;
    }
}
