package com.navare.prashant.experienceauroville;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import com.navare.prashant.shared.model.CurrentEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EventDetailActivity extends AppCompatActivity {

    private TextView mNameTV;
    private TextView mLocationTV;
    private TextView mTimeTV;
    private TextView mDescriptionTV;
    private CurrentEvent mEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_event_detail);
        mNameTV = (TextView) findViewById(R.id.nameTV);
        mLocationTV = (TextView) findViewById(R.id.locationTV);
        mTimeTV = (TextView) findViewById(R.id.timeTV);
        mDescriptionTV = (TextView) findViewById(R.id.descriptionTV);

        mEvent = ApplicationStore.getCurrentEvent();
        mNameTV.setText(mEvent.getName());

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(mEvent.getLocation());
        ssb.setSpan(new URLSpan("#"), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mLocationTV.setText(ssb, TextView.BufferType.SPANNABLE);
        mLocationTV.setClickable(true);
        mLocationTV.setLinkTextColor(Color.BLUE);

        mLocationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra(ApplicationStore.SPECIFIC_LOCATION_STRING, mEvent.getLocation());
                startActivity(intent);
            }
        });

        Calendar fromCal = Calendar.getInstance();
        fromCal.setTimeInMillis(mEvent.getFrom_date());
        Calendar toCal = Calendar.getInstance();
        toCal.setTimeInMillis(mEvent.getTo_date());
        SimpleDateFormat sdfDay = new SimpleDateFormat("EEE, dd/MM/yyyy");
        String timingString = sdfDay.format(fromCal.getTime());
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
        timingString += " (" + sdfTime.format(fromCal.getTime());
        if (mEvent.getTo_date() == 0) {
            timingString +=  " onwards)";
        }
        else {
            timingString += " -- " + sdfTime.format(toCal.getTime()) + ")";
        }
        mTimeTV.setText(timingString);

        mDescriptionTV.setText(mEvent.getDescription());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
