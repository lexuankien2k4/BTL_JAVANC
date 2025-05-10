package com.example.tx1.Entity;

import javafx.beans.property.*;
public class Product {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty price;
    private final StringProperty imageUrl;
    private final StringProperty description;

    public Product(int id, String name, String price, String imageUrl, String description) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleStringProperty(price);
        this.imageUrl = new SimpleStringProperty(imageUrl);
        this.description = new SimpleStringProperty(description);
    }

    public Product(String name, String price, String imageUrl, String description) {
        this(0, name, price, imageUrl, description);
    }


    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }


    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }


    public String getPrice() {
        return price.get();
    }

    public void setPrice(String price) {
        this.price.set(price);
    }

    public StringProperty priceProperty() {
        return price;
    }


    public String getImageUrl() {
        return imageUrl.get();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl.set(imageUrl);
    }

    public StringProperty imageUrlProperty() {
        return imageUrl;
    }


    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }
}