package com.navare.prashant.exploreauroville;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        mAurovilianEmailTV = (TextView) findViewById(R.id.textAurovilianEmail);

        mGuestLL = (LinearLayout) findViewById(R.id.guestLL);
        mGuestStatusTV = (TextView) findViewById(R.id.textGuestStatus);
        mGuestNameTV = (TextView) findViewById(R.id.textGuestName);
        mGuestPhoneTV = (TextView) findViewById(R.id.textGuestPhone);
        mGuestSponsorTV = (TextView) findViewById(R.id.textGuestSponsor);
        mGuestLocationTV = (TextView) findViewById(R.id.textGuestLocation);
        mGuestValidityTV = (TextView) findViewById(R.id.textGuestValidity);

        mVisitorLL = (LinearLayout) findViewById(R.id.visitorLL);
        mVisitorStatusTV = (TextView) findViewById(R.id.textVisitorStatus);

        mAurovilianStatusTV.setText("Aurovilian");
        mAurovilianNameTV.setText("Prashant Navare");
        mAurovilianEmailTV.setText("prashant.navare@futureschool.org.in");
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
