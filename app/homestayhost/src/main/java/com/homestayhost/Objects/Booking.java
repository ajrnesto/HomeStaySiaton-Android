package com.homestayhost.Objects;

public class Booking {
    String id;
    String unitId;
    String userUid;
    String hostUid;
    long bookingEndDate;
    long bookingStartDate;
    String status;
    long timestamp;

    public Booking() {
    }

    public Booking(String id, String unitId, String userUid, String hostUid, long bookingEndDate, long bookingStartDate, String status, long timestamp) {
        this.id = id;
        this.unitId = unitId;
        this.userUid = userUid;
        this.hostUid = hostUid;
        this.bookingEndDate = bookingEndDate;
        this.bookingStartDate = bookingStartDate;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getHostUid() {
        return hostUid;
    }

    public void setHostUid(String hostUid) {
        this.hostUid = hostUid;
    }

    public long getBookingEndDate() {
        return bookingEndDate;
    }

    public void setBookingEndDate(long bookingEndDate) {
        this.bookingEndDate = bookingEndDate;
    }

    public long getBookingStartDate() {
        return bookingStartDate;
    }

    public void setBookingStartDate(long bookingStartDate) {
        this.bookingStartDate = bookingStartDate;
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
}
