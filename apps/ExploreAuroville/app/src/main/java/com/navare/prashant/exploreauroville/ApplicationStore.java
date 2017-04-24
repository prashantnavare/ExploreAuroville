package com.navare.prashant.exploreauroville;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.navare.prashant.exploreauroville.model.POI;
import com.navare.prashant.exploreauroville.util.CustomRequest;
import com.navare.prashant.exploreauroville.util.VolleyProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;

/**
 * Created by prashant on 16-Apr-17.
 */

public class ApplicationStore extends Application {
    public static final String BASE_URL = "http://192.168.1.104:5678";
    // API URLs
    public static final String POI_URL = ApplicationStore.BASE_URL + "/api/explorex/v1/poi";
    public static final String GET_CURRENT_EVENTS_URL = ApplicationStore.BASE_URL + "/api/explorex/v1/events";

    private static SharedPreferences mPreferences;
    private static SharedPreferences.Editor mEditor;
    private static Context mAppContext;

    private static List<POI> mPOIList = new ArrayList<>();
    private static boolean mbPOIListUpdated = false;
    private static TreeSet<String> mTagSet = new TreeSet<>();

    private static final String POI_STRING = "POIString";
    public static int activeDatePicker = 0;

    @Override
    public void onCreate() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        mAppContext = getApplicationContext();
    }

    public static String getPOIString() {
        return mPreferences.getString(POI_STRING, "");
    }

    public static void setPOIString(String poiString) {
        mEditor.putString(POI_STRING, poiString);
        mEditor.commit();
    }

    public static List<POI> getPOIList(Activity callingActivity) {
        // If cache exists, use it right away and update it in the background.
        if (mPOIList.size() == 0) {
            String poiString = getPOIString();
            if (poiString.isEmpty() == false) {
                Gson gson = new Gson();
                mPOIList.addAll(Arrays.asList(gson.fromJson(poiString, POI[].class)));
                createTagSet();
            }
        }
        getPOIListFromServer(callingActivity);
        return mPOIList;
    }

    private static void createTagSet() {
        if (mTagSet.isEmpty()) {
            for (POI poi : mPOIList) {
                String allTagsString = poi.getTags();
                String[] tagStringArray = allTagsString.split(",");
                for (String tagString : tagStringArray) {
                    mTagSet.add(tagString.trim().toLowerCase());
                }
            }
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

    public static List<POI> getPOIListContaining(String queryString) {
        String lcQuesryString = queryString.toLowerCase();
        List<POI> poiList = new ArrayList<>();
        for (POI poi : mPOIList) {
            if (poi.getTags() != null) {
                if (poi.getTags().toLowerCase().contains(lcQuesryString)) {
                    poiList.add(poi);
                }
            }
        }
        return  poiList;
    }

    private static void getPOIListFromServer(final Activity callingActivity) {
        if (mbPOIListUpdated) {
            return;
        }
        CustomRequest citiesRequest = new CustomRequest(Request.Method.GET, ApplicationStore.POI_URL, "",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        mPOIList.addAll(Arrays.asList(gson.fromJson(response, POI[].class)));
                        // Also cache it away.
                        String poiString = gson.toJson(mPOIList);
                        setPOIString(poiString);
                        createTagSet();
                        mbPOIListUpdated = true;
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = callingActivity.getString(R.string.unable_to_get_poi_list);
                        Toast.makeText(callingActivity, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }){};

        RequestQueue requestQueue = VolleyProvider.getQueue(mAppContext);
        requestQueue.add(citiesRequest);
    }


}


