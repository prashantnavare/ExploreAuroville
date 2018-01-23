package com.navare.prashant.experienceauroville;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.navare.prashant.shared.model.Aurovilian;
import com.navare.prashant.shared.model.Guest;

public class SettingsActivity extends AppCompatActivity {

    private LinearLayout mAurovilianLL;
    private TextView mAurovilianStatusTV;
    private TextView mAurovilianNameTV;
    private TextView mAurovilianEmailTV;

    private LinearLayout mGuestLL;
    private TextView mGuestStatusTV;
    private TextView mGuestNameTV;
    private TextView mGuestPhoneTV;
    private TextView mGuestSponsorTV;
    private TextView mGuestLocationTV;
    private TextView mGuestValidityTV;

    private LinearLayout mVisitorLL;
    private TextView mVisitorStatusTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle("My Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAurovilianLL = (LinearLayout) findViewById(R.id.aurovilianLL);
        mAurovilianStatusTV = (TextView) findViewById(R.id.textAurovilianStatus);
        mAurovilianNameTV = (TextView) findViewById(R.id.textAurovilianName);

        mGuestLL = (LinearLayout) findViewById(R.id.guestLL);
        mGuestStatusTV = (TextView) findViewById(R.id.textGuestStatus);
        mGuestNameTV = (TextView) findViewById(R.id.textGuestName);
        mGuestPhoneTV = (TextView) findViewById(R.id.textGuestPhone);
        mGuestSponsorTV = (TextView) findViewById(R.id.textGuestSponsor);
        mGuestLocationTV = (TextView) findViewById(R.id.textGuestLocation);
        mGuestValidityTV = (TextView) findViewById(R.id.textGuestValidity);

        mVisitorLL = (LinearLayout) findViewById(R.id.visitorLL);
        mVisitorStatusTV = (TextView) findViewById(R.id.textVisitorStatus);

        if (ApplicationStore.getUserLevel() == ApplicationStore.AUROVILIAN) {
            Aurovilian aurovilian = ApplicationStore.getAurovilianProfile();

            mAurovilianLL.setVisibility(View.VISIBLE);
            mGuestLL.setVisibility(View.GONE);
            mVisitorLL.setVisibility(View.GONE);

            mAurovilianStatusTV.setText("Aurovilian");
            mAurovilianNameTV.setText(aurovilian.getName());
        }
        else if (ApplicationStore.getUserLevel() == ApplicationStore.GUEST) {
            Guest guest = ApplicationStore.getGuestProfile();

            mGuestLL.setVisibility(View.VISIBLE);
            mAurovilianLL.setVisibility(View.GONE);
            mVisitorLL.setVisibility(View.GONE);

            mGuestStatusTV.setText("Auroville Guest");

            mGuestNameTV.setText(guest.getName());
            mGuestPhoneTV.setText(guest.getPhone());
            mGuestSponsorTV.setText(guest.getSponsor());
            mGuestLocationTV.setText(guest.getLocation());
            mGuestValidityTV.setText(guest.getValidTill());
        }
        else if (ApplicationStore.getUserLevel() == ApplicationStore.VISITOR) {
            mVisitorLL.setVisibility(View.VISIBLE);
            mAurovilianLL.setVisibility(View.GONE);
            mGuestLL.setVisibility(View.GONE);

            mVisitorStatusTV.setText("Visitor");
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
