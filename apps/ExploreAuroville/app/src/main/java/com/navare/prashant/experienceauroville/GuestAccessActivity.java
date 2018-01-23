package com.navare.prashant.experienceauroville;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.navare.prashant.shared.model.Guest;
import com.navare.prashant.shared.util.CustomRequest;
import com.navare.prashant.shared.util.VolleyProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuestAccessActivity extends AppCompatActivity {

    private List<Guest> mGuestList = new ArrayList<>();
    private GuestListAdapter mAdapter;
    private ListView mListView;
    private GuestAccessActivity   mMyActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guest_access);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMyActivity = this;
        mMyActivity.setTitle("Your Current Guests");

        //get the listview
        mListView = (ListView) findViewById(R.id.listview_guests);

        // Get the guest list
        getGuestList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGuestList();
    }


    private void getGuestList() {
        final ProgressDialog progressDialog = new ProgressDialog(mMyActivity);
        progressDialog.setTitle("Retrieving Your Guests");
        progressDialog.setMessage("Please wait while we retrieve your guests...");
        progressDialog.show();

        String getGuestsURL = ApplicationStore.GUEST_URL;
        getGuestsURL += "?sponsor=" + ApplicationStore.getAurovilianProfile().getEmail();
        CustomRequest getGuestsRequest = new CustomRequest(Request.Method.GET, getGuestsURL, "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        List<Guest> guestListFromServer = Arrays.asList(gson.fromJson(response, Guest[].class));
                        mGuestList.clear();
                        mGuestList.addAll(guestListFromServer);
                        mAdapter = new GuestListAdapter(mMyActivity, mGuestList);
                        mListView.setAdapter(mAdapter);
                        // Show the number of guests in the title
                        String newTitle = "Your Current Guests" + " (" + String.valueOf(mGuestList.size()) + ")";
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
        requestQueue.add(getGuestsRequest);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void showGuestDetails(Guest guest) {
        ApplicationStore.setCurrentGuest(guest);
        Intent intent = new Intent(this, GuestDetailActivity.class);
        startActivity(intent);
    }
}
