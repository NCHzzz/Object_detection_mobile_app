// models/ImageItem.java
package com.example.homework.models;

import com.google.gson.annotations.SerializedName;

public class ImageItem {
    @SerializedName("id")
    private int id;

    @SerializedName("previewURL")
    private String previewURL;

    @SerializedName("largeImageURL")
    private String largeImageURL;

    @SerializedName("tags")
    private String tags;

    // Getters
    public int getId() {
        return id;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    public String getLargeImageURL() {
        return largeImageURL;
    }

    public String getTags() {
        return tags;
    }
}