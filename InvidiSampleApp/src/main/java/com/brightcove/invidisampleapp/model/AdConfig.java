package com.brightcove.invidisampleapp.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.ooyala.pulse.RequestSettings;

import java.io.Serializable;
import java.util.List;

public class AdConfig implements Serializable {
    @SerializedName("name")
    private String mName;

    @SerializedName("tags")
    private List<String> mTags;

    @SerializedName("category")
    private String mCategory;

    @SerializedName("midroll_positions")
    private List<Float> mMidrollPositions;

    @SerializedName("insertion_point_filters")
    private List<RequestSettings.InsertionPointType> mInsertionPointFilters;

    @SerializedName("flags")
    private List<String> mFlags;

    public static AdConfig createFromJson(Gson gson, String json) {
        return gson.fromJson(json, AdConfig.class);
    }

    public String getName() {
        return mName;
    }

    public List<String> getTags() {
        return mTags;
    }

    public String getCategory() {
        return mCategory;
    }

    public List<Float> getMidrollPositions() {
        return mMidrollPositions;
    }

    public List<RequestSettings.InsertionPointType> getInsertionPointFilters() {
        return mInsertionPointFilters;
    }

    public List<String> getFlags() {
        return mFlags;
    }

    @Override
    @NonNull
    public String toString() {
        return "AdConfig{" +
                "mName='" + mName + '\'' +
                ", mTags=" + mTags +
                ", mCategory='" + mCategory + '\'' +
                ", mMidrollPositions=" + mMidrollPositions +
                ", mInsertionPointFilters=" + mInsertionPointFilters +
                ", mFlags=" + mFlags +
                '}';
    }
}
