package com.navare.prashant.exploreauroville;

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
 * Created by prashant on 16-Apr-17.
 */

public class ApplicationStore extends Application {
    public static final String BASE_URL = "http://explorex.texity.com";
    // public static final String BASE_URL = "http://10.0.2.2:5678";

    // API URLs
    public static final String LOCATION_URL = BASE_URL + "/api/explorex/v1/admin/location";
    public static final String GET_CURRENT_EVENTS_URL = BASE_URL + "/api/explorex/v1/admin/event";
    public static final String FEEDBACK_URL = BASE_URL + "/api/explorex/v1/admin/feedback";
    public static final String PURGE_EVENTS_URL = BASE_URL + "/api/explorex/v1/admin/purgeEvents";

    private static SharedPreferences        mPreferences;
    private static SharedPreferences.Editor mEditor;
    private static Context                  mAppContext;

    private static List<Location> mLocationList = new ArrayList<>();
    private static boolean mbLocationListUpdated = false;
    private static TreeSet<String>      mTagSet = new TreeSet<>();

    private static final String LOCATION_STRING = "LocationString";
    private static final String AUROVILLIAN_STRING = "Aurovillian";

    public static final String SPECIFIC_LOCATION_STRING = "SpecificLocation";

    public static int activeDatePicker = 0;

    private static Location mCurrentLocation;
    private static CurrentEvent mCurrentEvent;

    @Override
    public void onCreate() {
        super.onCreate();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        mAppContext = getApplicationContext();
    }

    public static String getLocationString() {
        return mPreferences.getString(LOCATION_STRING, "");
    }

    public static void setLocationString(String locationString) {
        mEditor.putString(LOCATION_STRING, locationString);
        mEditor.commit();
    }

    public static Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public static void setCurrentLocation(Location location) {
        mCurrentLocation = location;
    }

    public static CurrentEvent getCurrentEvent() {
        return mCurrentEvent;
    }

    public static void setCurrentEvent(CurrentEvent event) {
        mCurrentEvent = event;
    }

    public static boolean isAurovillian() {
        return mPreferences.getBoolean(AUROVILLIAN_STRING, false);
    }

    public static void setAurovillian(boolean isAurovillian) {
        mEditor.putBoolean(AUROVILLIAN_STRING, isAurovillian);
        mEditor.commit();
    }

    public static List<Location> getLocationList(Activity callingActivity) {
        // If cache exists, use it right away and update it in the background.
        if (mLocationList.size() == 0) {
            String locationString = getLocationString();
            if (locationString.isEmpty() == false) {
                Gson gson = new Gson();
                mLocationList.addAll(Arrays.asList(gson.fromJson(locationString, Location[].class)));
                createLocationTagSet();
            }
        }
        getLocationListFromServer(callingActivity);
        return mLocationList;
    }

    private static void createLocationTagSet() {
        mTagSet.clear();
        for (Location location : mLocationList) {
            String allTagsString = location.getTags();
            String[] tagStringArray = allTagsString.split(",");
            for (String tagString : tagStringArray) {
                mTagSet.add(tagString.trim().toLowerCase());
            }
            // Also add the location name as a tag
            mTagSet.add(location.getName().toLowerCase());
        }
    }

    public static List<String> getTagListContaining(String queryString) {
        String lcQuesryString = queryString.toLowerCase();
        List<String> tagList = new ArrayList<>();
        for (String tagString : mTagSet) {
            if (tagString.contains(lcQuesryString)) {
                tagList.add(tagString);
            }
        }
        return  tagList;
    }

    public static List<Location> getLocationListContaining(String queryString) {
        String lcQuesryString = queryString.toLowerCase();
        List<Location> locationList = new ArrayList<>();
        for (Location location : mLocationList) {
            if (location.getTags() != null) {
                if (location.getTags().toLowerCase().contains(lcQuesryString)) {
                    locationList.add(location);
                }
                else if (location.getName().toLowerCase().contains(lcQuesryString)) {
                    locationList.add(location);
                }
            }
        }
        return locationList;
    }

    public interface LocationListCallback {
        void locationListUpdated();
    }

    private static void getLocationListFromServer(final Activity callingActivity) {
        if (mbLocationListUpdated) {
            return;
        }
        final LocationListCallback locationListCallback = (LocationListCallback) callingActivity;
        CustomRequest citiesRequest = new CustomRequest(Request.Method.GET, ApplicationStore.LOCATION_URL, "",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        List<Location> locationListFromServer = Arrays.asList(gson.fromJson(response, Location[].class));
                        if (locationListFromServer.size() > 0) {
                            mLocationList.clear();
                            mLocationList.addAll(Arrays.asList(gson.fromJson(response, Location[].class)));
                        }
                        locationListCallback.locationListUpdated();
                        // Also cache it away.
                        String locationString = gson.toJson(mLocationList);
                        setLocationString(locationString);
                        createLocationTagSet();
                        mbLocationListUpdated = true;
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = callingActivity.getString(R.string.unable_to_get_location_list);
                        Toast.makeText(callingActivity, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }){};

        RequestQueue requestQueue = VolleyProvider.getQueue(mAppContext);
        requestQueue.add(citiesRequest);
    }

    public static void doVersionCheck(Activity callingActivity) {
        // TODO: Implement this
    }

}


