package com.navare.prashant.exploreauroville;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.navare.prashant.shared.model.CurrentEvent;
import com.navare.prashant.shared.util.CustomRequest;
import com.navare.prashant.shared.util.VolleyProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CurrentEventsActivity extends AppCompatActivity {

    private List<CurrentEvent>      mEventList = new ArrayList<>();
    private CurrentEventListAdapter mAdapter;
    private ListView                mListView;
    private EditText                mSearchET;
    private static EditText         mFromDateET;
    private static EditText         mToDateET;
    private DialogFragment          mDateFragment;
    public static Calendar          mFromCalendar = Calendar.getInstance();
    public static Calendar          mToCalendar = Calendar.getInstance();
    public final static String      fromPickerTag = "fromDatePicker";
    public final static String      toPickerTag = "toDatePicker";
    private ImageButton             mDateFilterButton;

    private boolean                 mbOneWeek = true;
    private CurrentEventsActivity   mMyActivity;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_current_events);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMyActivity = this;

        //get the listview
        mListView = (ListView) findViewById(R.id.listview_events);

        // Locate the EditText search in listview_main.xml
        mSearchET = (EditText) findViewById(R.id.search_events);

        // Capture Text in EditText
        mSearchET.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                String text = mSearchET.getText().toString().toLowerCase(Locale.getDefault());
                if (mAdapter != null)
                    mAdapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }
        });

        // Find the from and the to fields to setup the datepicker
        mFromDateET = (EditText) findViewById(R.id.event_date_from);
        mToDateET = (EditText) findViewById(R.id.event_date_to);
        mDateFragment = new DatePickerFragment();

        mFromDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationStore.activeDatePicker = v.getId();
                mDateFragment.show(getFragmentManager(), fromPickerTag);
            }
        });

        mToDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationStore.activeDatePicker = v.getId();
                mDateFragment.show(getFragmentManager(), toPickerTag);
            }
        });

        // Respond to the date filter button
        mDateFilterButton = (ImageButton) findViewById(R.id.dateFilterBtn);
        mDateFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFromDateET.getText().toString().isEmpty() || mToDateET.getText().toString().isEmpty()){
                    Toast.makeText(mMyActivity, getResources().getString(R.string.invalid_calendar_filter),Toast.LENGTH_SHORT).show();
                    return;
                }
                Calendar endOfToDateCalendar = (Calendar) mToCalendar.clone();
                endOfToDateCalendar.set(Calendar.HOUR, 23);
                endOfToDateCalendar.set(Calendar.MINUTE, 59);

                if (endOfToDateCalendar.getTimeInMillis() <= mFromCalendar.getTimeInMillis()) {
                    Toast.makeText(mMyActivity, getResources().getString(R.string.to_date_before_from_date),Toast.LENGTH_SHORT).show();
                    return;
                }
                mEventList.clear();
                mAdapter = new CurrentEventListAdapter(mMyActivity, mEventList);
                mListView.setAdapter(mAdapter);
                getCurrentEvents(mFromCalendar.getTimeInMillis(), endOfToDateCalendar.getTimeInMillis(), false);
            }
        });

        // Ads initialization
        MobileAds.initialize(this, "ca-app-pub-1181736027907915~9786968065");
        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setVisibility(View.VISIBLE);
        mAdView.setBackgroundColor(0xff330000);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Start with 1 week's events
        Calendar nowCalendar = Calendar.getInstance();
        int year = nowCalendar.get(Calendar.YEAR);
        int month = nowCalendar.get(Calendar.MONTH);
        int day = nowCalendar.get(Calendar.DAY_OF_MONTH);

        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.clear();
        todayCalendar.set(year, month, day);

        Calendar oneWeekCalendar = Calendar.getInstance();
        oneWeekCalendar.clear();
        oneWeekCalendar.set(year, month, day+7);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        mFromDateET.setText(dateFormat.format(todayCalendar.getTime()));
        mToDateET.setText(dateFormat.format(oneWeekCalendar.getTime()));

        // Initialize the member calendars
        mFromCalendar = (Calendar) todayCalendar.clone();
        mToCalendar = (Calendar) oneWeekCalendar.clone();

        // Get 1 week's events
        getCurrentEvents(todayCalendar.getTimeInMillis(), oneWeekCalendar.getTimeInMillis(), true);
    }

    private void getCurrentEvents(long fromDate, long toDate, boolean bOneWeek) {
        mbOneWeek = bOneWeek;
        final ProgressDialog progressDialog = new ProgressDialog(mMyActivity);
        progressDialog.setTitle("Retrieving Events");
        if (bOneWeek) {
            setTitle(getString(R.string.events_one_week));
            progressDialog.setMessage("Please wait while we retrieve this week's events...");
        }
        else {
            mMyActivity.setTitle(getString(R.string.events));
            progressDialog.setMessage("Please wait while we retrieve your requested events...");
        }
        progressDialog.show();

        String getEventsURL = ApplicationStore.GET_CURRENT_EVENTS_URL;
        getEventsURL += "?from=" + String.valueOf(fromDate);
        getEventsURL += "&to=" + String.valueOf(toDate);
        CustomRequest getEventsRequest = new CustomRequest(Request.Method.GET, getEventsURL, "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        List<CurrentEvent> eventListFromServer = Arrays.asList(gson.fromJson(response, CurrentEvent[].class));
                        mEventList.clear();
                        for (CurrentEvent event : eventListFromServer) {
                            if (ApplicationStore.getUserLevel() >= event.getAccessLevel()) {
                                mEventList.add(event);
                            }
                        }
                        mAdapter = new CurrentEventListAdapter(mMyActivity, mEventList);
                        mListView.setAdapter(mAdapter);
                        if (mEventList.size() <= 3) {
                            mSearchET.setVisibility(View.GONE);
                            if (mEventList.isEmpty()) {
                                Toast.makeText(mMyActivity, getResources().getString(R.string.no_events_scheduled),Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(mMyActivity, getResources().getString(R.string.click_on_event_for_details),Toast.LENGTH_LONG).show();
                            mSearchET.setVisibility(View.VISIBLE);
                        }
                        // Show the number of events in the title
                        String newTitle = mMyActivity.getTitle() + " (" + String.valueOf(mEventList.size()) + ")";
                        mMyActivity.setTitle(newTitle);
                        progressDialog.cancel();
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
                        progressDialog.cancel();
                    }
                }){};

        RequestQueue requestQueue = VolleyProvider.getQueue(getApplicationContext());
        requestQueue.add(getEventsRequest);
    }

    public static void setDateFieldText(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (ApplicationStore.activeDatePicker == R.id.event_date_from){
            mFromDateET.setText(dateFormat.format(mFromCalendar.getTime()));
        }
        else if (ApplicationStore.activeDatePicker == R.id.event_date_to){
            mToDateET.setText(dateFormat.format(mToCalendar.getTime()));
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void showNewCount(int newCount) {
        if (mbOneWeek) {
            String newTitle = getString(R.string.events_one_week) + " (" + String.valueOf(newCount) + ")";
            mMyActivity.setTitle(newTitle);
        }
        else {
            String newTitle = getString(R.string.events) + " (" + String.valueOf(newCount) + ")";
            mMyActivity.setTitle(newTitle);
        }
    }

    public void showEventDetails(CurrentEvent event) {
        ApplicationStore.setCurrentEvent(event);
        Intent intent = new Intent(CurrentEventsActivity.this, EventDetailActivity.class);
        startActivity(intent);

    }
}
