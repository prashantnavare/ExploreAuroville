package com.navare.prashant.explorexadmin;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.navare.prashant.shared.model.CurrentEvent;
import com.navare.prashant.shared.model.POI;
import com.navare.prashant.shared.util.CustomRequest;
import com.navare.prashant.shared.util.VolleyProvider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class EventDetailsActivity extends AppCompatActivity {

    private EditText        mEventNameET;
    private Spinner         mPOISpinner;
    private static EditText        mDateET;
    private DialogFragment  mDateFragment;
    private EditText        mStartTimeET;
    private EditText        mEndTimeET;
    private EditText        mEventDescriptionET;
    private EditText        mEventTagsET;
    private ImageButton     mSaveButton;

    private List<POI>       mPOIList;

    private Activity mMyActivity;
    private boolean  mbExistingEvent = false;
    private CurrentEvent mEvent;
    private Date mEventDate = new Date();
    private int  mStartHour, mStartMinute;
    private int  mEndHour, mEndMinute;
    private boolean mbEventChanged = false;

    public final static String eventDatePickerTag = "eventDatePicker";
    public static Calendar mEventCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        mMyActivity = this;
        mbEventChanged = false;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mbExistingEvent = bundle.getBoolean(ApplicationStore.EXISTING_EVENT_STRING);
            if (mbExistingEvent) {
                mEvent = ApplicationStore.getCurrentEvent();
            }
            else {
                mEvent = new CurrentEvent();
            }
        }
        else {
            mEvent = new CurrentEvent();
            mbExistingEvent = false;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEventNameET = (EditText) findViewById(R.id.event_name_et);
        initPOISpinner();
        mDateET = (EditText) findViewById(R.id.date_et);
        mDateFragment = new DatePickerFragment();

        mDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateFragment.show(getFragmentManager(), eventDatePickerTag);
            }
        });

        mStartTimeET = (EditText) findViewById(R.id.start_time_et);
        mEndTimeET = (EditText) findViewById(R.id.end_time_et);
        mStartTimeET.setInputType(InputType.TYPE_NULL);
        mEndTimeET.setInputType(InputType.TYPE_NULL);

        if (mbExistingEvent) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mEvent.getFrom_date());
            mStartHour = calendar.get(Calendar.HOUR_OF_DAY);
            mStartMinute = calendar.get(Calendar.MINUTE);

            calendar.setTimeInMillis(mEvent.getTo_date());
            mEndHour = calendar.get(Calendar.HOUR_OF_DAY);
            mEndMinute = calendar.get(Calendar.MINUTE);
        }
        else {
            Calendar currentTime = Calendar.getInstance();
            mStartHour = mEndHour = currentTime.get(Calendar.HOUR_OF_DAY);
            mStartMinute = mEndMinute = currentTime.get(Calendar.MINUTE);
        }

        mStartTimeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(mMyActivity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mStartTimeET.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, mStartHour, mStartMinute,false);//Yes 24 hour time
                mTimePicker.setTitle(getResources().getString(R.string.timePickerTitle));
                mTimePicker.show();
            }
        });

        mEndTimeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(mMyActivity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mEndTimeET.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, mEndHour, mEndMinute,false);//Yes 24 hour time
                mTimePicker.setTitle(getResources().getString(R.string.timePickerTitle));
                mTimePicker.show();
            }
        });

        mEventDescriptionET = (EditText) findViewById(R.id.description_et);
        mEventTagsET = (EditText) findViewById(R.id.tags_et);

        mSaveButton = (ImageButton) findViewById(R.id.save_btn);
        mSaveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveEventInfo();
            }
        });

        updateUI();
    }

    public static void setDateFieldText(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy");
        mDateET.setText(dateFormat.format(mEventCalendar.getTime()));
    }

    private void updateUI() {
        if (mbExistingEvent) {
            setTitle(getString(R.string.modify_event));
            mEventNameET.setText(mEvent.getName());
            setPOISpinnerSelection();

            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTimeInMillis(mEvent.getFrom_date());
            SimpleDateFormat sdfDay = new SimpleDateFormat("EEE, dd/MM/yyyy");
            String dateString = sdfDay.format(dateCalendar.getTime());
            mDateET.setText(dateString);

            mStartTimeET.setText(String.format("%02d:%02d", mStartHour, mStartMinute));
            mEndTimeET.setText(String.format("%02d:%02d", mEndHour, mEndMinute));

            mEventDescriptionET.setText(mEvent.getDescription());
            mEventTagsET.setText(mEvent.getTags());
        }
        else {
            setTitle(getString(R.string.new_event));
        }
    }

    private void setPOISpinnerSelection() {
        // Set the POI spinner selection to the event's poi
        boolean bPOIExists = false;
        int currentPos;
        for (currentPos = 0; currentPos < mPOISpinner.getAdapter().getCount(); currentPos++) {
            POI currentPOI = (POI) mPOISpinner.getItemAtPosition(currentPos);
            if (currentPOI.getId() == mEvent.getPOI_id()) {
                bPOIExists = true;
                break;
            }
        }
        if (bPOIExists) {
            mPOISpinner.setSelection(currentPos);
        }
    }

    private void initPOISpinner(){
        mPOISpinner = (Spinner) findViewById(R.id.poi_spinner);
        List<POI> poiList = ApplicationStore.getPOIList(mMyActivity);
        final ArrayAdapter<POI> poiAdapter = new ArrayAdapter<>(mMyActivity, android.R.layout.simple_spinner_item, poiList);
        poiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPOISpinner.setAdapter(poiAdapter);
    }

    private boolean isUIValidated() {
        if (mEventNameET.getText().toString().isEmpty()) {
            Toast.makeText(mMyActivity, getString(R.string.event_name_missing), Toast.LENGTH_LONG).show();
            return false;
        }
        if (mDateET.getText().toString().isEmpty()) {
            Toast.makeText(mMyActivity, getString(R.string.event_date_missing), Toast.LENGTH_LONG).show();
            return false;
        }
        if (mStartTimeET.getText().toString().isEmpty()) {
            Toast.makeText(mMyActivity, getString(R.string.event_start_time_missing), Toast.LENGTH_LONG).show();
            return false;
        }
        if (mEndTimeET.getText().toString().isEmpty()) {
            Toast.makeText(mMyActivity, getString(R.string.event_end_time_missing), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void updateEventFromUI() {
        mEvent.setName(mEventNameET.getText().toString());
        mEvent.setPoi_id(((POI)mPOISpinner.getSelectedItem()).getId());

        String dateString = mDateET.getText().toString();
        SimpleDateFormat sdfDay = new SimpleDateFormat("EEE, dd/MM/yyyy");
        Date eventDate = new Date();
        try {
            eventDate = sdfDay.parse(mDateET.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar eventFromCalendar = Calendar.getInstance();
        eventFromCalendar.setTime(eventDate);
        eventFromCalendar.set(Calendar.HOUR_OF_DAY, mStartHour);
        eventFromCalendar.set(Calendar.MINUTE, mStartMinute);
        mEvent.setFrom_date(eventFromCalendar.getTimeInMillis());

        Calendar eventToCalendar = Calendar.getInstance();
        eventToCalendar.setTime(eventDate);
        eventToCalendar.set(Calendar.HOUR_OF_DAY, mEndHour);
        eventToCalendar.set(Calendar.MINUTE, mEndMinute);
        mEvent.setTo_date(eventToCalendar.getTimeInMillis());

        mEvent.setDescription(mEventDescriptionET.getText().toString());
        mEvent.setTags(mEventTagsET.getText().toString());
    }

    private void saveEventInfo() {
        if (isUIValidated() == false) {
            return;
        }
        updateEventFromUI();

        int requestMethod = Request.Method.POST;
        if (mbExistingEvent) {
            requestMethod = Request.Method.PUT;
        }
        Gson gson = new Gson();
        String requestBody = gson.toJson(mEvent);

        CustomRequest eventRequest = new CustomRequest(requestMethod, ApplicationStore.EVENT_URL, requestBody, ApplicationStore.getAuthToken(),
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(mMyActivity, getString(R.string.event_info_saved), Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = getString(R.string.unable_to_save_event_info);
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            if (error.networkResponse.data != null)
                                errorMsg = new String(error.networkResponse.data);
                        }
                        Toast.makeText(mMyActivity, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }){};

        RequestQueue requestQueue = VolleyProvider.getQueue(mMyActivity);
        requestQueue.add(eventRequest);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
