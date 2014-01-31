package com.brightcove.player.samples.ais.webview.basic;

import java.util.Map;

/**
 * Created by bhnath on 1/29/14.
 */
public class ChooserResponse {
    private Map<String, String> grouped_idps;
    private String _type;
    private boolean authenticated;
    private Map<String, IDPS> possible_idps;
    private Map<String, String> footprints;
    private String platform_id;
    private Map<String, String> preferred_idps;

    public String getType() {
        return _type;
    }
    public boolean getAuthenticated() {
        return authenticated;
    }

    public Map<String, IDPS> getPossibleIdps() {
        return possible_idps;
    }

    public class IDPS {
        private String url;
        private Map<String, String> logos;
        private String display_name;
        private String name;

        public String getUrl() {
            return url;
        }

        public String getDisplayName() {
            return display_name;
        }
        public String getName() {
            return name;
        }
    }
}
