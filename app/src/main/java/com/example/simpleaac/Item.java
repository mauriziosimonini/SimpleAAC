package com.example.simpleaac;

public class Item {
    private int id;
    private String text;
    private String imagePath;

    public Item() {
    }

    public Item(int id, String text, String imagePath) {
        this.id = id;
        this.text = text;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}