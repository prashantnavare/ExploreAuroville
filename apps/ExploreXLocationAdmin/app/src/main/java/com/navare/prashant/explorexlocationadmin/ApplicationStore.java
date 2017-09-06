package com.navare.prashant.explorexlocationadmin;

/**
 * Created by prashant on 8/16/2017.
 */

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.navare.prashant.shared.model.CurrentEvent;
import com.navare.prashant.shared.model.Location;
import com.navare.prashant.shared.util.CustomRequest;
import com.navare.prashant.shared.util.VolleyProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by prashant on 30-Apr-17.
 */

public class ApplicationStore extends Application {
    public static String LAST_SELECTED_LOCATION = "LastSelectedLocation";
    public static final String EXISTING_EVENT_STRING = "ExistingEventString";

    public static final String BASE_URL = "http://explorex.texity.com";

    // API URLs
    public static final String LOCATION_URL = BASE_URL + "/api/explorex/v1/admin/location";
    public static final String EVENT_URL = ApplicationStore.BASE_URL + "/api/explorex/v1/admin/event";

    private static SharedPreferences mPreferences;
    private static SharedPreferences.Editor mEditor;
    private static Context mAppContext;

    private static CurrentEvent mCurrentEvent = null;
    private static Location mCurrentLocation = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        mAppContext = getApplicationContext();
    }

    public static String getLastSelectedLocationString() {
        return mPreferences.getString(LAST_SELECTED_LOCATION, "");
    }

    public static void setLastSelectedLocationString(String locationString) {
        mEditor.putString(LAST_SELECTED_LOCATION, locationString);
        mEditor.commit();
    }

    public static Location getCurrentLocation() {
        return mCurrentLocation;
    }
    public static void setCurrentLocation(Location currentLocation) {
        mCurrentLocation = currentLocation;
    }

    public static CurrentEvent getCurrentEvent() {
        return mCurrentEvent;
    }
    public static void setCurrentEvent(CurrentEvent currentEvent) {
        mCurrentEvent = currentEvent;
    }

}
