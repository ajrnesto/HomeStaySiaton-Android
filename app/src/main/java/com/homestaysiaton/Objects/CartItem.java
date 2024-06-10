package com.homestaysiaton.Objects;

public class CartItem {
    String unitId;
    int quantity;

    public CartItem() {
    }

    public CartItem(String unitId, int quantity) {
        this.unitId = unitId;
        this.quantity = quantity;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
