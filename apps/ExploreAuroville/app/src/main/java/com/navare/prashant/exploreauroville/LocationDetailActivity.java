package com.navare.prashant.exploreauroville;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.navare.prashant.shared.model.Location;

public class LocationDetailActivity extends AppCompatActivity {

    private TextView mNameTV;
    private TextView mDescriptionTV;
    private TextView mURLTV;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        mNameTV = (TextView) findViewById(R.id.nameTV);
        mDescriptionTV = (TextView) findViewById(R.id.descriptionTV);
        mURLTV = (TextView) findViewById(R.id.urlTV);

        mLocation = ApplicationStore.getCurrentLocation();
        mNameTV.setText(mLocation.getName());
        mDescriptionTV.setText(mLocation.getDescription());
        mURLTV.setText(mLocation.getWebsite());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
