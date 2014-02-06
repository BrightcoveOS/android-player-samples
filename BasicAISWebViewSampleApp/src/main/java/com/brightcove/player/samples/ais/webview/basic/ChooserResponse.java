package com.brightcove.player.samples.ais.webview.basic;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by bhnath on 1/29/14.
 */
public class ChooserResponse {

    @SerializedName("grouped_idps")
    private Map<String, String> groupedIdps;

    @SerializedName("_type")
    private String type;

    @SerializedName("authenticated")
    private boolean authenticated;

    @SerializedName("possible_idps")
    private Map<String, IdentityProvider> possibleIdps;

    @SerializedName("footprints")
    private Map<String, String> footprints;

    @SerializedName("platform_id")
    private String platformId;

    @SerializedName("preferred_idps")
    private Map<String, String> preferredIdps;

    public String getType() {
        return type;
    }
    public boolean getAuthenticated() {
        return authenticated;
    }

    public Map<String, IdentityProvider> getPossibleIdps() {
        return possibleIdps;
    }

    public class IdentityProvider {

        @SerializedName("url")
        private String url;

        @SerializedName("logos")
        private Map<String, String> logos;

        @SerializedName("display_name")
        private String displayName;

        @SerializedName("name")
        private String name;

        public String getUrl() {
            return url;
        }
        public Map<String, String> getLogos() {
            return logos;
        }
        public String getDisplayName() {
            return displayName;
        }
        public String getName() {
            return name;
        }
    }
}
