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


public class EventsFragment extends Fragment {

    public static EventsFragment newInstance() {
        EventsFragment eventsFragment = new EventsFragment();
        return eventsFragment;
    }

    public EventsFragment() {

    }

    private FloatingActionButton mNewEventButton;
    private static RecyclerView mRecyclerView;
    protected static EventsAdapter mAdapter;
    private Spinner mLocationSpinner;

    private static List<CurrentEvent> mEventList = new ArrayList<>();
    private static Activity mMyActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events,container,false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mAdapter = new EventsAdapter(this, mEventList);
        mMyActivity = getActivity();

        mLocationSpinner = (Spinner) rootView.findViewById(R.id.location_spinner);
        mLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Location location = (Location) mLocationSpinner.getSelectedItem();
                updateEvents(location);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        List<Location> locationList = ApplicationStore.getLocationList(mMyActivity);
        ArrayAdapter<Location> locationDataAdapter = new ArrayAdapter<>(mMyActivity, android.R.layout.simple_spinner_item, locationList);
        locationDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLocationSpinner.setAdapter(locationDataAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        mNewEventButton = (FloatingActionButton) rootView.findViewById(R.id.feedback_button);
        mNewEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEvent(null);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Location location = (Location) mLocationSpinner.getSelectedItem();
        updateEvents(location);
    }

    private void updateEvents(Location location) {
        final ProgressDialog progressDialog = new ProgressDialog(mMyActivity);
        progressDialog.setTitle("Retrieving Events");
        progressDialog.setMessage("Please wait while we retrieve events for " + location.getName()+ "...");
        progressDialog.show();

        String getEventsURL = ApplicationStore.GET_CURRENT_EVENTS_URL;
        getEventsURL += "&locationid=" + location.getId();

        // get next 10 days of events
        Calendar nowCalendar = Calendar.getInstance();
        int year = nowCalendar.get(Calendar.YEAR);
        int month = nowCalendar.get(Calendar.MONTH);
        int day = nowCalendar.get(Calendar.DAY_OF_MONTH);
        Calendar todayCalendar = Calendar.getInstance();
        Calendar tenDayCalendar = Calendar.getInstance();
        todayCalendar.clear();
        todayCalendar.set(year, month, day);
        tenDayCalendar.clear();
        tenDayCalendar.set(year, month, day+10);
        getEventsURL += "&from=" + String.valueOf(todayCalendar.getTimeInMillis());
        getEventsURL += "&to=" + String.valueOf(tenDayCalendar.getTimeInMillis());

        CustomRequest getEventsRequest = new CustomRequest(Request.Method.GET, getEventsURL, ApplicationStore.getAuthToken(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        mEventList.clear();
                        mEventList.addAll(Arrays.asList(gson.fromJson(response, CurrentEvent[].class)));
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
        requestQueue.add(getEventsRequest);
    }

    public void editEvent(CurrentEvent currentEvent) {
        boolean bExistingEvent = false;
        if (currentEvent != null) {
            bExistingEvent = true;
            ApplicationStore.setCurrentEvent(currentEvent);
        }
        Intent eventDetailsIntent = new Intent(getActivity(), EventDetailsActivity.class);
        eventDetailsIntent.putExtra(ApplicationStore.EXISTING_EVENT_STRING, bExistingEvent);
        startActivity(eventDetailsIntent);
    }

    public void deleteEvent(final CurrentEvent currentEvent) {
        new AlertDialog.Builder(mMyActivity)
                .setTitle(mMyActivity.getString(R.string.delete_event))
                .setMessage(mMyActivity.getString(R.string.delete_event_confirmation)+" "+ currentEvent.getName()+"?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        sendDeleteEventToServer(currentEvent);
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

    private void sendDeleteEventToServer(final CurrentEvent eventToDelete) {
        String deleteEventURL = ApplicationStore.EVENT_URL + "?eventid=" + eventToDelete.getId();

        CustomRequest deleteEventRequest = new CustomRequest(Request.Method.DELETE, deleteEventURL, "", ApplicationStore.getAuthToken(),
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(mMyActivity, mMyActivity.getString(R.string.event_deleted), Toast.LENGTH_LONG).show();
                        mEventList.remove(eventToDelete);
                        mAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = mMyActivity.getString(R.string.event_delete_failed);
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            if (error.networkResponse.data != null)
                                errorMsg = new String(error.networkResponse.data);
                        }
                        Toast.makeText(mMyActivity, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }){};

        RequestQueue requestQueue = VolleyProvider.getQueue(mMyActivity);
        requestQueue.add(deleteEventRequest);
    }


}
