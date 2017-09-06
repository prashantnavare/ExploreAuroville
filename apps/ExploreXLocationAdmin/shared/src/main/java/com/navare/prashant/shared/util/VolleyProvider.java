package com.navare.prashant.shared.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by prashant on 17-Apr-17.
 */

public class VolleyProvider {
    private static RequestQueue queue = null;

    private VolleyProvider() { }

    public static synchronized RequestQueue getQueue(Context ctx) {
        if (queue == null) {
            queue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return queue;
    }
}
