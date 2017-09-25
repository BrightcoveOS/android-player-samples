package com.brightcove.player.samples.ais.webview.basic;

import com.google.gson.annotations.SerializedName;

/**
 * JSON response class for the /identity/resourceAccess/ REST API call.
 * Note: this is not a completed class. It has been implemented
 * enough for the purposes of this sample app.
 *
 * @author bhnath (Billy Hnath)
 */
public class ResourceAccessResponse {

    @SerializedName("_type")
    private String type;

    @SerializedName("resource")
    private String resource;

    @SerializedName("security_token")
    private String securityToken;

    @SerializedName("expires")
    private int expires;

    @SerializedName("servertime")
    private int serverTime;

    @SerializedName("platform_id")
    private String platformId;

    @SerializedName("message")
    private String message;

    @SerializedName("authorization")
    private boolean authorization;

    public String getType() {
        return type;
    }

    public String getResource() {
        return resource;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public int getExpires() {
        return expires;
    }

    public int getServerTime() {
        return serverTime;
    }

    public String getPlatformId() {
        return platformId;
    }

    public String getMessage() {
        return message;
    }

    public boolean getAuthorization() {
        return authorization;
    }
}
