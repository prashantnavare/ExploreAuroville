package com.navare.prashant.exploreauroville;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.navare.prashant.shared.model.Location;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLngBounds mRouteBound;
    private static List<Location> mLocationListToShow = new ArrayList<>();
    private static List<Location> mLocationListAll = new ArrayList<>();
    private DelayAutoCompleteTextView   mAutocompleteTV;
    private ImageView                   mAutocompleteClearIV;
    private Integer THRESHOLD = 1;
    private String mTagSearchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Build the Location List for this location
        mLocationListAll = ApplicationStore.getLocationList(this);

        mAutocompleteClearIV = (ImageView) findViewById(R.id.autocomplete_clear);

        mAutocompleteTV = (DelayAutoCompleteTextView) findViewById(R.id.autocomplete);
        mAutocompleteTV.setThreshold(THRESHOLD);
        mAutocompleteTV.setAdapter(new TagAutoCompleteAdapter(this)); // 'this' is Activity instance

        mAutocompleteTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mTagSearchString = (String) adapterView.getItemAtPosition(position);
                mAutocompleteTV.setText(mTagSearchString);
                mLocationListToShow = ApplicationStore.getLocationListContaining(mTagSearchString);
                showMarkersOnMap();
            }
        });

        mAutocompleteTV.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mAutocompleteClearIV.setVisibility(View.VISIBLE);
                }
                else {
                    mAutocompleteClearIV.setVisibility(View.GONE);
                }
            }
        });

        mAutocompleteClearIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteTV.setText("");
                mMap.clear();
                showAuroville();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        showAuroville();
    }

    public void showAuroville() {
        // Add a marker in Auroville and move the camera
        LatLng auroville = new LatLng(12.0053, 79.8129);
        mMap.addMarker(new MarkerOptions().position(auroville).title(getString(R.string.auroville)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(auroville, 14));
    }

    public void showMarkersOnMap() {
        LatLngBounds.Builder mapBoundsBuilder = new LatLngBounds.Builder();

        for (Location currentLocation : mLocationListToShow) {
            LatLng locationLatLng = new LatLng(Double.valueOf(currentLocation.getLatitude()), Double.valueOf(currentLocation.getLongitude()));
            Marker locationMarker = mMap.addMarker(new MarkerOptions()
                    .position(locationLatLng)
                    .draggable(false)
                    .title(currentLocation.getName())
                    .snippet(currentLocation.getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mapBoundsBuilder.include(locationLatLng);
        }
        if (mLocationListToShow.size() > 0) {
            mRouteBound = mapBoundsBuilder.build();
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mRouteBound, 40));
                }
            });
        }
        else {
            showAuroville();
        }
    }
}
