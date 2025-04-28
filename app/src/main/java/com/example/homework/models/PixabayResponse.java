// models/PixabayResponse.java
package com.example.homework.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PixabayResponse {
    @SerializedName("total")
    private int total;

    @SerializedName("totalHits")
    private int totalHits;

    @SerializedName("hits")
    private List<ImageItem> hits;

    // Getters
    public int getTotal() {
        return total;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public List<ImageItem> getHits() {
        return hits;
    }
}