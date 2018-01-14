package com.navare.prashant.experienceauroville;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.navare.prashant.shared.util.CustomRequest;
import com.navare.prashant.shared.util.VolleyProvider;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements ApplicationStore.LocationListCallback {

    private GridView    mGridView;

    String[]    mTileText = new String[2];
    int[]       mTileImage = {R.drawable.map_72px, R.drawable.current_events_72px};
    Activity    mMyActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: For now, evebrybody is treated as Aurovilian. This needs to change once the Login scheme is implemented.
        ApplicationStore.setUserLevel(ApplicationStore.AUROVILIAN);

        // Show EULA
        new SimpleEula(this).show();

        mMyActivity = this;

        // Check for a newer version
        ApplicationStore.doVersionCheck(mMyActivity);

        // Build the Location List for this location
        ApplicationStore.getLocationList(this);

        mTileText[0]=getString(R.string.places);
        mTileText[1]=getString(R.string.events);

        NavigationGridAdapter adapter = new NavigationGridAdapter(this, mTileText, mTileImage);
        mGridView =(GridView)findViewById(R.id.grid);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch(position){
                    case 0:
                        Intent mapIntent = new Intent(mMyActivity, MapActivity.class);
                        startActivity(mapIntent);
                        break;
                    case 1:
                        Intent eventIntent = new Intent(mMyActivity, CurrentEventsActivity.class);
                        startActivity(eventIntent);
                        break;
                }
            }
        });

        // purge older events in the background
        purgeOldEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_feedback:
                onFeedback();
                return super.onOptionsItemSelected(item);
            /*
            case R.id.menu_settings:
                onSettings();
                return super.onOptionsItemSelected(item);
            */
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onFeedback() {
        Intent feedbackIntent = new Intent(mMyActivity, FeedbackActivity.class);
        startActivity(feedbackIntent);
    }

    private void onSettings() {
        Intent settingsIntent = new Intent(mMyActivity, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    public void locationListUpdated() {
        // Do nothing
    }

    private void purgeOldEvents() {
        // Purge all events scheduled before today
        Calendar nowCalendar = Calendar.getInstance();
        int year = nowCalendar.get(Calendar.YEAR);
        int month = nowCalendar.get(Calendar.MONTH);
        int day = nowCalendar.get(Calendar.DAY_OF_MONTH);
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.clear();
        todayCalendar.set(year, month, day);

        String purgeEventsURL = ApplicationStore.PURGE_EVENTS_URL;
        purgeEventsURL += "?cutofftime=" + String.valueOf(todayCalendar.getTimeInMillis());
        CustomRequest purgeEventsRequest = new CustomRequest(Request.Method.DELETE, purgeEventsURL, "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do nothing
                        int foo = 1;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = getString(R.string.network_error);
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            if (error.networkResponse.data != null)
                                errorMsg = new String(error.networkResponse.data);
                        }
                        Toast.makeText(mMyActivity, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }){};

        RequestQueue requestQueue = VolleyProvider.getQueue(getApplicationContext());
        requestQueue.add(purgeEventsRequest);
    }
}

