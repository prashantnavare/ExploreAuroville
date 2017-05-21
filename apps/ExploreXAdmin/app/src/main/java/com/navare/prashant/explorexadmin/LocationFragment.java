package com.navare.prashant.explorexadmin;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends Fragment {

    private FloatingActionButton mNewLocationButton;
    private static RecyclerView mRecyclerView;
    protected static LocationAdapter mAdapter;
    private static List<Location> mLocationList = new ArrayList<>();
    private static Activity mMyActivity;

    public LocationFragment() {
        // Required empty public constructor
    }

    public static LocationFragment newInstance() {
        LocationFragment fragment = new LocationFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location,container,false);
        mMyActivity = getActivity();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mLocationList = ApplicationStore.getLocationList(mMyActivity);
        mAdapter = new LocationAdapter(this, mLocationList);
        mRecyclerView.setAdapter(mAdapter);

        mNewLocationButton = (FloatingActionButton) rootView.findViewById(R.id.location_button);
        mNewLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editLocation(null);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLocations();
    }

    private void updateLocations() {
        final ProgressDialog progressDialog = new ProgressDialog(mMyActivity);
        progressDialog.setTitle("Retrieving Locations");
        progressDialog.setMessage("Please wait while we retrieve locations...");
        progressDialog.show();

        CustomRequest getLocationsRequest = new CustomRequest(Request.Method.GET, ApplicationStore.LOCATION_URL, "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        mLocationList.clear();
                        mLocationList.addAll(Arrays.asList(gson.fromJson(response, Location[].class)));
                        mAdapter.notifyDataSetChanged();
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

        RequestQueue requestQueue = VolleyProvider.getQueue(getActivity());
        requestQueue.add(getLocationsRequest);
    }

    public void editLocation(Location currentLocation) {
        boolean bExistingLocation = false;
        if (currentLocation != null) {
            bExistingLocation = true;
            ApplicationStore.setCurrentLocation(currentLocation);
        }
        Intent locationDetailsIntent = new Intent(getActivity(), LocationDetailsActivity.class);
        locationDetailsIntent.putExtra(ApplicationStore.EXISTING_LOCATION_STRING, bExistingLocation);
        startActivity(locationDetailsIntent);
    }

    public void deleteLocation(final Location currentLocation) {
        new AlertDialog.Builder(mMyActivity)
                .setTitle(mMyActivity.getString(R.string.delete_location))
                .setMessage(mMyActivity.getString(R.string.delete_location_confirmation)+ " " + currentLocation.getName() + "?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        sendDeleteLocationToServer(currentLocation);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.attention_24px)
                .show();
    }

    private void sendDeleteLocationToServer(final Location locationToDelete) {
        String deleteLocationURL = ApplicationStore.LOCATION_URL + "?locationid=" + locationToDelete.getId();

        CustomRequest deleteLocationRequest = new CustomRequest(Request.Method.DELETE, deleteLocationURL, "", ApplicationStore.getAuthToken(),
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(mMyActivity, mMyActivity.getString(R.string.location_deleted), Toast.LENGTH_LONG).show();
                        mLocationList.remove(locationToDelete);
                        mAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = mMyActivity.getString(R.string.location_delete_failed);
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            if (error.networkResponse.data != null)
                                errorMsg = new String(error.networkResponse.data);
                        }
                        Toast.makeText(mMyActivity, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }){};

        RequestQueue requestQueue = VolleyProvider.getQueue(mMyActivity);
        requestQueue.add(deleteLocationRequest);
    }
}
