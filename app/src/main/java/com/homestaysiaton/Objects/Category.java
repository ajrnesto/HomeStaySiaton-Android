package com.homestaysiaton.Objects;

public class Category {
    String id;
    String categoryName;

    public Category(String id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

    public Category() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
