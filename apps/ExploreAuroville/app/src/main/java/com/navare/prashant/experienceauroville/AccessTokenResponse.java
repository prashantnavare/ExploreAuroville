package com.navare.prashant.experienceauroville;

/**
 * Created by prashant on 2/2/2018.
 */

public class AccessTokenResponse {
    private String access_token;
    private String token_type;
    private long expires_in;
    private long created_at;

    public String getAccess_token() {
        return access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public long getCreated_at() {
        return created_at;
    }
}
