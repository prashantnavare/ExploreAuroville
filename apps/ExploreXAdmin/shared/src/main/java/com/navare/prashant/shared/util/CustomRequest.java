package com.navare.prashant.shared.util;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by prashant on 17-Apr-17.
 */

public class CustomRequest extends StringRequest {
    private String mBody;
    private String mBearerToken;
    private String mBodyContentType;
    private Response.ErrorListener mErrorListener;

    public static String CONTENT_TYPE_URLENCODED = "application/x-www-form-urlencoded";
    public static String CONTENT_TYPE_JSON = "application/json";

    public CustomRequest(int method, String url, String body, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        this(method, url, body, null, listener, errorListener);
    }

    public CustomRequest(int method, String url, String body, String bearerToken, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        mBody = body;
        mBearerToken = bearerToken;
        mBodyContentType = CONTENT_TYPE_URLENCODED;
        mErrorListener = errorListener;
        this.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                0,  // maxNumRetries = 0 means no retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if ((mBearerToken == null) || mBearerToken.isEmpty()) {
            return super.getHeaders();
        }
        else {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + mBearerToken);
            return headers;
        }
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return mBody.getBytes();
    }

    public void setBodyContentType(String bodyContentType) {
        mBodyContentType = bodyContentType;
    }

    @Override
    public String getBodyContentType() {
        return mBodyContentType;
    }

    @Override
    protected String getParamsEncoding() {
        return "utf-8";
    }
}
