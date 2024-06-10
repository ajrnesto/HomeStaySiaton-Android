package com.homestaysiaton.Objects;

import java.util.ArrayList;

public class ShopItem {
    String id;
    String categoryId;
    double price;
    int maxGuests;
    String hostUid;
    String unitDetails;
    String unitName;
    String addressLine;
    ArrayList<String> keywords;
    UnitLocation location;
    int stock;
    Long thumbnail;

    public ShopItem() {
    }

    public ShopItem(String id, String categoryId, double price, int maxGuests, String hostUid, String unitDetails, String unitName, String addressLine, ArrayList<String> keywords, UnitLocation location, int stock, Long thumbnail) {
        this.id = id;
        this.categoryId = categoryId;
        this.price = price;
        this.maxGuests = maxGuests;
        this.hostUid = hostUid;
        this.unitDetails = unitDetails;
        this.unitName = unitName;
        this.addressLine = addressLine;
        this.keywords = keywords;
        this.location = location;
        this.stock = stock;
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public String getHostUid() {
        return hostUid;
    }

    public void setHostUid(String hostUid) {
        this.hostUid = hostUid;
    }

    public String getUnitDetails() {
        return unitDetails;
    }

    public void setUnitDetails(String unitDetails) {
        this.unitDetails = unitDetails;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    public UnitLocation getLocation() {
        return location;
    }

    public void setLocation(UnitLocation location) {
        this.location = location;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Long getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Long thumbnail) {
        this.thumbnail = thumbnail;
    }
}
