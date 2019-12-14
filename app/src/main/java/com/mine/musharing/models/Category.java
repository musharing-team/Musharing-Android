package com.mine.musharing.models;

import java.io.Serializable;

/**
 * 代表由后端可以提供的一张固定的播放列表(被叫做 categoryList 😂)
 */
public class Category implements Serializable {

    private String id;

    private String title;

    private String image;

    private String description;

    public Category() {
    }

    public Category(String id, String title, String image) {
        this.id = id;
        this.title = title;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
