package com.brightcove.invidisampleapp.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Reader;
import java.io.Serializable;
import java.util.List;

public class PulseAccount implements Serializable {
    @SerializedName("pulseHost")
    private String mPulseHost;

    @SerializedName("adConfigs")
    private List<AdConfig> mAdConfigs;

    @NonNull
    public static PulseAccount createFromReader(Gson gson, Reader reader) {
        return gson.fromJson(reader, PulseAccount.class);
    }

    public PulseAccount(String pulseHost, List<AdConfig> adConfigs) {
        mPulseHost = pulseHost;
        mAdConfigs = adConfigs;
    }

    public String getPulseHost() {
        return mPulseHost;
    }

    public List<AdConfig> getAdConfigs() {
        return mAdConfigs;
    }

    @NonNull
    @Override
    public String toString() {
        return "PulseAccount{" +
                "mPulseHost='" + mPulseHost + '\'' +
                ", mAdConfigs=" + mAdConfigs +
                '}';
    }
}
