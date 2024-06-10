package com.homestayhost.Objects;

import java.util.ArrayList;

import kotlin.jvm.JvmField;

public class ShopItem {
    String id;
    String categoryId;
    double price;
    double maxGuests;
    String hostUid;
    String unitDetails;
    String unitName;
    String addressLine;
    ArrayList<String> keywords;
    UnitLocation location;
    int stock;
    Long thumbnail;
    boolean isBooked;
    long bookingEndDate;

    public ShopItem() {
    }

    public ShopItem(String id, String categoryId, double price, double maxGuests, String hostUid, String unitDetails, String unitName, String addressLine, ArrayList<String> keywords, UnitLocation location, int stock, Long thumbnail, boolean isBooked, long bookingEndDate) {
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
        this.isBooked = isBooked;
        this.bookingEndDate = bookingEndDate;
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

    public double getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(double maxGuests) {
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

    public boolean isIsBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public long getBookingEndDate() {
        return bookingEndDate;
    }

    public void setBookingEndDate(long bookingEndDate) {
        this.bookingEndDate = bookingEndDate;
    }
}
