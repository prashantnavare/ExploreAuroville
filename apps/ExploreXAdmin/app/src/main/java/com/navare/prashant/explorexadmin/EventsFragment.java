package com.navare.prashant.explorexadmin;

import android.app.Activity;
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

import com.google.gson.Gson;
import com.navare.prashant.shared.model.CurrentEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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

    private static List<CurrentEvent> mEventList = new ArrayList<>();
    private static Activity mMyActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events,container,false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mAdapter = new EventsAdapter(this, mEventList);
        mMyActivity = getActivity();

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

        loadEvents();

        return rootView;
    }

    public static void loadEvents() {
        // TODO
        /*
        mEventList.clear();
        String allActivatedRoutesString = ApplicationStore.getActivatedRoutes();
        if (allActivatedRoutesString == null || allActivatedRoutesString.isEmpty()) {
            return;
        }
        else {
            Gson gson = new Gson();
            List<RouteInfo> cachedRouteInfoList = Arrays.asList(gson.fromJson(allActivatedRoutesString, RouteInfo[].class));
            mEventList.addAll(cachedRouteInfoList);
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
        */
    }

    public void editEvent(CurrentEvent currentEvent) {
        // TODO
        /*
        RouteInfo selectedRouteInfo = mEventList.get(pos);
        Gson gson = new Gson();
        String routeString = gson.toJson(selectedRouteInfo);
        Intent mapsActivity = new Intent(getActivity(), MapsActivity.class);
        mapsActivity.putExtra(ApplicationStore.ROUTE_STRING, routeString);
        startActivity(mapsActivity);
        */
    }

    public void deleteEvent(CurrentEvent currentEvent) {
        // TODO
        /*
        RouteInfo selectedRouteInfo = mEventList.get(pos);
        Gson gson = new Gson();
        String routeString = gson.toJson(selectedRouteInfo);
        Intent mapsActivity = new Intent(getActivity(), MapsActivity.class);
        mapsActivity.putExtra(ApplicationStore.ROUTE_STRING, routeString);
        startActivity(mapsActivity);
        */
    }


}
