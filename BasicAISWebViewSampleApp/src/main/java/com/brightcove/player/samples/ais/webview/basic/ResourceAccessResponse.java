package com.brightcove.player.samples.ais.webview.basic;

/**
 * Created by bhnath on 1/31/14.
 */
public class ResourceAccessResponse {

    private String _type;
    private String resource;
    private String security_token;
    private int expires;
    private int servertime;
    private String platform_id;
    private String message;
    private boolean authorization;

    public String getType() {
        return _type;
    }

    public String getResource() {
        return resource;
    }

    public String getSecurityToken() {
        return security_token;
    }

    public int getExpires() {
        return expires;
    }

    public int getServerTime() {
        return servertime;
    }

    public String getPlatformId() {
        return platform_id;
    }

    public String getMessage() {
        return message;
    }

    public boolean getAuthorization() {
        return authorization;
    }
}
