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
import com.navare.prashant.exploreauroville.model.POI;
import com.navare.prashant.exploreauroville.util.CustomRequest;
import com.navare.prashant.exploreauroville.util.VolleyProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by prashant on 16-Apr-17.
 */

public class ApplicationStore extends Application {
    public static final String BASE_URL = "http://192.168.1.104:5678";
    // API URLs
    public static final String POI_URL = ApplicationStore.BASE_URL + "/api/explorex/v1/poi";

    private static SharedPreferences mPreferences;
    private static SharedPreferences.Editor mEditor;
    private static Context mAppContext;

    private static List<POI> mPOIList = new ArrayList<>();

    private static final String POI_STRING = "POIString";

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
            }
        }
        getPOIListFromServer(callingActivity);
        return mPOIList;
    }

    private static void getPOIListFromServer(final Activity callingActivity) {
        CustomRequest citiesRequest = new CustomRequest(Request.Method.GET, ApplicationStore.POI_URL, "",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        mPOIList.addAll(Arrays.asList(gson.fromJson(response, POI[].class)));
                        // Also cache it away.
                        String poiString = gson.toJson(mPOIList);
                        setPOIString(poiString);
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


