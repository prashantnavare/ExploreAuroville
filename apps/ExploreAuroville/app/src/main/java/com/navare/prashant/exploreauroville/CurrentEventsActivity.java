package com.navare.prashant.exploreauroville;

import android.app.Activity;
import android.app.DialogFragment;
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
import com.google.gson.Gson;
import com.navare.prashant.exploreauroville.model.CurrentEvent;
import com.navare.prashant.exploreauroville.util.CustomRequest;
import com.navare.prashant.exploreauroville.util.VolleyProvider;

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

    private Activity                mMyActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_events);

        setTitle(getString(R.string.current_events));
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
                }
                if (mAdapter != null)
                    mAdapter.filterDate(mFromCalendar, mToCalendar);
            }
        });

        getCurrentEvents();
    }

    private void getCurrentEvents() {
        // TODO: Add the fromDate and toDate logic
        String getEventsURL = ApplicationStore.GET_CURRENT_EVENTS_URL + "?from=xyz&to=abc";
        CustomRequest getEventsRequest = new CustomRequest(Request.Method.GET, getEventsURL, "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        mEventList = Arrays.asList(gson.fromJson(response, CurrentEvent[].class));
                        mAdapter = new CurrentEventListAdapter(mMyActivity, mEventList);
                        mListView.setAdapter(mAdapter);
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
}
