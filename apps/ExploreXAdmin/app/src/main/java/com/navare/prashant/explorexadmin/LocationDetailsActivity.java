package com.navare.prashant.explorexadmin;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.navare.prashant.shared.model.Location;
import com.navare.prashant.shared.util.CustomRequest;
import com.navare.prashant.shared.util.VolleyProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LocationDetailsActivity extends AppCompatActivity {
    private EditText    mLocationNameET;
    private EditText    mLocationDescriptionET;
    private EditText    mLocationTagsET;
    private ImageButton mSaveButton;

    private Activity mMyActivity;
    private boolean mbExistingLocation = false;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);

        mMyActivity = this;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mbExistingLocation = bundle.getBoolean(ApplicationStore.EXISTING_LOCATION_STRING);
            if (mbExistingLocation) {
                mLocation = ApplicationStore.getCurrentLocation();
            }
            else {
                mLocation = new Location();
            }
        }
        else {
            mLocation = new Location();
            mbExistingLocation = false;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLocationNameET = (EditText) findViewById(R.id.location_name_et);
        mLocationDescriptionET = (EditText) findViewById(R.id.description_et);
        mLocationTagsET = (EditText) findViewById(R.id.tags_et);

        mSaveButton = (ImageButton) findViewById(R.id.save_btn);
        mSaveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveLocationInfo();
            }
        });

        updateUI();
    }

    private void updateUI() {
        if (mbExistingLocation) {
            setTitle(getString(R.string.modify_location));
            mLocationNameET.setText(mLocation.getName());
            mLocationDescriptionET.setText(mLocation.getDescription());
            mLocationTagsET.setText(mLocation.getTags());
        }
        else {
            setTitle(getString(R.string.new_location));
        }
    }

    private boolean isUIValidated() {
        if (mLocationNameET.getText().toString().isEmpty()) {
            Toast.makeText(mMyActivity, getString(R.string.location_name_missing), Toast.LENGTH_LONG).show();
            return false;
        }
        if (mLocationDescriptionET.getText().toString().isEmpty()) {
            Toast.makeText(mMyActivity, getString(R.string.location_description_missing), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void updateLocationFromUI() {
        mLocation.setName(mLocationNameET.getText().toString());
        mLocation.setDescription(mLocationDescriptionET.getText().toString());
        mLocation.setTags(mLocationTagsET.getText().toString());
    }

    private void saveLocationInfo() {
        if (isUIValidated() == false) {
            return;
        }
        updateLocationFromUI();

        int requestMethod = Request.Method.POST;
        if (mbExistingLocation) {
            requestMethod = Request.Method.PUT;
        }
        Gson gson = new Gson();
        String requestBody = gson.toJson(mLocation);

        CustomRequest locationRequest = new CustomRequest(requestMethod, ApplicationStore.LOCATION_URL, requestBody, ApplicationStore.getAuthToken(),
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(mMyActivity, getString(R.string.location_info_saved), Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = getString(R.string.unable_to_save_event_info);
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            if (error.networkResponse.data != null)
                                errorMsg = new String(error.networkResponse.data);
                        }
                        Toast.makeText(mMyActivity, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }){};

        RequestQueue requestQueue = VolleyProvider.getQueue(mMyActivity);
        requestQueue.add(locationRequest);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
