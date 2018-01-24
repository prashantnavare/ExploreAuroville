package com.navare.prashant.experienceauroville;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.navare.prashant.shared.model.Guest;
import com.navare.prashant.shared.util.CustomRequest;
import com.navare.prashant.shared.util.VolleyProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GuestDetailActivity extends AppCompatActivity {

    private EditText        mGuestNameET;
    private EditText        mGuestPhoneET;
    private EditText        mGuestLocationET;
    private static EditText mFromDateET;
    private static EditText mToDateET;
    private DialogFragment  mDateFragment;
    private ImageButton     mSaveButton;
    private MenuItem        deleteMenuItem = null;

    private Activity    mMyActivity;
    private boolean     mbExistingGuest = false;
    private Guest       mGuest;
    private Date mFromEventDate = new Date();
    private Date mToEventDate = new Date();
    private boolean mbGuestChanged = false;

    public final static String fromDatePickerTag = "fromDatePicker";
    public final static String toDatePickerTag = "fromDatePicker";
    public static Calendar mFromCalendar = Calendar.getInstance();
    public static Calendar mToCalendar = Calendar.getInstance();

    public static SimpleDateFormat mSDF = new SimpleDateFormat("EEE, dd MMM yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_detail);

        mMyActivity = this;
        mbGuestChanged = false;
        Guest guest = ApplicationStore.getCurrentGuest();
        if (guest != null) {
            mGuest = guest;
            mbExistingGuest = true;
        }
        else {
            mGuest = new Guest();
            mbExistingGuest = false;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGuestNameET = (EditText) findViewById(R.id.guest_name_et);
        mGuestPhoneET = (EditText) findViewById(R.id.guest_phone_et);
        mGuestLocationET = (EditText) findViewById(R.id.guest_location_et);
        mFromDateET = (EditText) findViewById(R.id.guest_from_date_et);
        mToDateET = (EditText) findViewById(R.id.guest_to_date_et);
        mDateFragment = new DatePickerFragment();

        mFromDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationStore.activeDatePicker = v.getId();
                mDateFragment.show(getFragmentManager(), fromDatePickerTag);
            }
        });

        mToDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationStore.activeDatePicker = v.getId();
                mDateFragment.show(getFragmentManager(), toDatePickerTag);
            }
        });

        mSaveButton = (ImageButton) findViewById(R.id.save_btn);
        mSaveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveGuestInfo();
            }
        });

        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.guest_detail_actions, menu);
        deleteMenuItem = menu.getItem(0);

        if (mbExistingGuest == false) {
            deleteMenuItem.setEnabled(false);
            deleteMenuItem.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                onBackPressed();
                return true;
            case R.id.menu_delete:
                deleteGuest();
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void setDateFieldText(){
        if (ApplicationStore.activeDatePicker == R.id.guest_from_date_et){
            mFromDateET.setText(mSDF.format(mFromCalendar.getTime()));
        }
        else if (ApplicationStore.activeDatePicker == R.id.guest_to_date_et){
            mToDateET.setText(mSDF.format(mToCalendar.getTime()));
        }
    }

    private void updateUI() {
        setTitle(getString(R.string.guest_details));
        if (mbExistingGuest) {
            mGuestNameET.setText(mGuest.getName());
            mGuestPhoneET.setText(mGuest.getPhone());
            mGuestLocationET.setText(mGuest.getLocation());

            Calendar dateCalendar = Calendar.getInstance();

            dateCalendar.setTimeInMillis(mGuest.getFrom_date());
            String fromDateString = mSDF.format(dateCalendar.getTime());
            mFromDateET.setText(fromDateString);

            dateCalendar.setTimeInMillis(mGuest.getTo_date());
            String toDateString = mSDF.format(dateCalendar.getTime());
            mToDateET.setText(toDateString);
        }
    }

    private boolean isUIValidated() {
        if (mGuestNameET.getText().toString().isEmpty()) {
            Toast.makeText(mMyActivity, getString(R.string.guest_name_missing), Toast.LENGTH_LONG).show();
            return false;
        }
        if (mGuestPhoneET.getText().toString().isEmpty()) {
            Toast.makeText(mMyActivity, getString(R.string.guest_phone_missing), Toast.LENGTH_LONG).show();
            mGuestPhoneET.requestFocus();
            return false;
        }
        if (mGuestLocationET.getText().toString().isEmpty()) {
            Toast.makeText(mMyActivity, getString(R.string.guest_location_missing), Toast.LENGTH_LONG).show();
            mGuestLocationET.requestFocus();
            return false;
        }
        if (mFromDateET.getText().toString().isEmpty()) {
            Toast.makeText(mMyActivity, getString(R.string.from_date_missing), Toast.LENGTH_LONG).show();
            return false;
        }
        if (mToDateET.getText().toString().isEmpty()) {
            Toast.makeText(mMyActivity, getString(R.string.to_date_missing), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void updateGuestFromUI() {
        mGuest.setName(mGuestNameET.getText().toString());
        mGuest.setPhone(mGuestPhoneET.getText().toString());
        mGuest.setLocation(mGuestLocationET.getText().toString());

        Date fromDate = new Date();
        try {
            fromDate = mSDF.parse(mFromDateET.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(fromDate);
        mGuest.setFrom_date(fromCalendar.getTimeInMillis());

        Date toDate = new Date();
        try {
            toDate = mSDF.parse(mToDateET.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(toDate);
        mGuest.setTo_date(toCalendar.getTimeInMillis());
    }

    private void saveGuestInfo() {
        if (isUIValidated() == false) {
            return;
        }
        updateGuestFromUI();

        int requestMethod = Request.Method.POST;
        if (mbExistingGuest) {
            requestMethod = Request.Method.PUT;
        }
        Gson gson = new Gson();
        String requestBody = gson.toJson(mGuest);

        CustomRequest guestRequest = new CustomRequest(requestMethod, ApplicationStore.GUEST_URL, requestBody, "",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(mMyActivity, getString(R.string.guest_info_saved), Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = getString(R.string.unable_to_save_guest_info);
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            if (error.networkResponse.data != null)
                                errorMsg = new String(error.networkResponse.data);
                        }
                        Toast.makeText(mMyActivity, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }){};

        RequestQueue requestQueue = VolleyProvider.getQueue(mMyActivity);
        requestQueue.add(guestRequest);
    }

    private void deleteGuest() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Delete Guest");
        alertDialog.setMessage("Are you sure you want to delete this guest?");
        alertDialog.setIcon(R.drawable.ic_menu_delete_white);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                deleteGuestInternal();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void deleteGuestInternal() {
        String deleteGuestsURL = ApplicationStore.GUEST_URL;
        deleteGuestsURL += "?guestid=" + mGuest.getId();
        CustomRequest deleteGuestsRequest = new CustomRequest(Request.Method.DELETE, deleteGuestsURL, "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(mMyActivity, "Guest deleted successfully.", Toast.LENGTH_LONG).show();
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
                    }
                }){};

        RequestQueue requestQueue = VolleyProvider.getQueue(getApplicationContext());
        requestQueue.add(deleteGuestsRequest);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
