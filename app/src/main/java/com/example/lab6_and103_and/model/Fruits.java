package com.example.lab6_and103_and.model;

import java.util.ArrayList;

public class Fruits {
    private String _id;
    private ArrayList<String> image;
    private String name;
    private int quantity;
    private int price;
    private String distributor;
    private String description;

    public Fruits() {
    }

    public Fruits(String _id, ArrayList<String> image, String name, int quantity, int price, String distributor, String description) {
        this._id = _id;
        this.image = image;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.distributor = distributor;
        this.description = description;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
