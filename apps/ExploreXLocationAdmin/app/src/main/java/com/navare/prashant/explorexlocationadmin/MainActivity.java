package com.navare.prashant.explorexlocationadmin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner mLocationSpinner;
    private Button mNextButton;
    private Activity mMyActivity;
    private List<Location> mLocationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMyActivity = this;
        mLocationSpinner = (Spinner) findViewById(R.id.locationSpinner);
        mLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Location location = (Location) mLocationSpinner.getSelectedItem();
                if (location != null)
                    ApplicationStore.setLastSelectedLocationString(location.getName());
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        mLocationSpinner.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);

        mNextButton = (Button) findViewById(R.id.nextButton) ;
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location location = (Location) mLocationSpinner.getSelectedItem();
                ApplicationStore.setCurrentLocation(location);
                Intent eventListIntent = new Intent(mMyActivity, EventListActivity.class);
                startActivity(eventListIntent);
            }
        });
        getLocationListFromServer();
    }

    private void getLocationListFromServer() {
        CustomRequest locationRequest = new CustomRequest(Request.Method.GET, ApplicationStore.LOCATION_URL, "",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        List<Location> locationListFromServer = Arrays.asList(gson.fromJson(response, Location[].class));
                        if (locationListFromServer.size() > 0) {
                            mLocationList.clear();
                            mLocationList.addAll(locationListFromServer);
                            ArrayAdapter<Location> locationDataAdapter = new ArrayAdapter<>(mMyActivity, android.R.layout.simple_spinner_item, mLocationList);
                            locationDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mLocationSpinner.setAdapter(locationDataAdapter);

                            String locationName = ApplicationStore.getLastSelectedLocationString();
                            if (locationName.isEmpty() == false) {
                                Location chosenLocation = null;
                                for (Location currentLocation : mLocationList) {
                                    if (currentLocation.getName().equalsIgnoreCase(locationName)) {
                                        chosenLocation = currentLocation;
                                    }
                                }
                                if (chosenLocation != null) {
                                    mLocationSpinner.setSelection(locationDataAdapter.getPosition(chosenLocation));
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = mMyActivity.getString(R.string.unable_to_get_location_list);
                        Toast.makeText(mMyActivity, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }){};

        RequestQueue requestQueue = VolleyProvider.getQueue(mMyActivity);
        requestQueue.add(locationRequest);
    }

}
