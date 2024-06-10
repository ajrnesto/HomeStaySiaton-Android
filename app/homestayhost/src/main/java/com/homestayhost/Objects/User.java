package com.homestayhost.Objects;

public class User {
    String firstName;
    String lastName;
    String uid;
    String mobile;
    String addressPurok;
    String addressBarangay;
    String addresCity;

    public User() {
    }

    public User(String firstName, String lastName, String uid, String mobile, String addressPurok, String addressBarangay, String addresCity) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.uid = uid;
        this.mobile = mobile;
        this.addressPurok = addressPurok;
        this.addressBarangay = addressBarangay;
        this.addresCity = addresCity;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddressPurok() {
        return addressPurok;
    }

    public void setAddressPurok(String addressPurok) {
        this.addressPurok = addressPurok;
    }

    public String getAddressBarangay() {
        return addressBarangay;
    }

    public void setAddressBarangay(String addressBarangay) {
        this.addressBarangay = addressBarangay;
    }

    public String getAddresCity() {
        return addresCity;
    }

    public void setAddresCity(String addresCity) {
        this.addresCity = addresCity;
    }
}
