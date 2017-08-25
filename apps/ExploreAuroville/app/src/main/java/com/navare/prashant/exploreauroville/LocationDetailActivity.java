package com.navare.prashant.exploreauroville;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.navare.prashant.shared.model.Location;

public class LocationDetailActivity extends AppCompatActivity {

    private TextView mNameTV;
    private TextView mDescriptionTV;
    private TextView mURLTV;
    private Location mLocation;
    private AdView mAdView;

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
        if (mLocation.getWebsite() != null)
            mURLTV.setText(mLocation.getWebsite());

        // Ads initialization
        MobileAds.initialize(this, "ca-app-pub-1181736027907915~9786968065");
        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setVisibility(View.VISIBLE);
        mAdView.setBackgroundColor(0xff330000);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
