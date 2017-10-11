package com.navare.prashant.exploreauroville;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.gson.Gson;
import com.navare.prashant.shared.model.Guest;
import com.navare.prashant.shared.util.CustomRequest;
import com.navare.prashant.shared.util.VolleyProvider;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, Button.OnClickListener {

    private LinearLayout mRadioButtonsLL;
    private RadioButton mAurovilianRadioButton;
    private RadioButton mGuestRadioButton;
    private RadioButton mVisitorRadioButton;
    private LinearLayout mAurovilianLL;
    private SignInButton mAurovilianSignInButton;
    private LinearLayout mGuestLL;
    private EditText mGuestPhoneNumberET;
    private Button mGuestSignInButton;
    private LinearLayout mGuestExpiredLL;
    private Button mGuestExpiredButton;
    private LinearLayout mVisitorLL;
    private Button mVisitorNextButton;

    private GoogleSignInOptions mGSO;
    private GoogleApiClient mGAC;
    private String mDomainName = "auroville.org.in";

    private Activity mMyActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mMyActivity = this;

        // Show EULA
        new SimpleEula(this).show();

        mRadioButtonsLL = (LinearLayout) findViewById(R.id.radioButtonsLL);
        mAurovilianRadioButton = (RadioButton) findViewById(R.id.aurovilianRadioButton);
        mGuestRadioButton = (RadioButton) findViewById(R.id.guestRadioButton);
        mVisitorRadioButton = (RadioButton) findViewById(R.id.visitorRadioButton);
        mAurovilianLL = (LinearLayout) findViewById(R.id.aurovilianLL);
        mAurovilianSignInButton = (SignInButton) findViewById(R.id.aurovilianSignInbutton);
        mGuestLL = (LinearLayout) findViewById(R.id.guestLL);
        mGuestPhoneNumberET = (EditText) findViewById(R.id.guestPhoneNumber);
        mGuestSignInButton = (Button) findViewById(R.id.guestSignInbutton);
        mGuestExpiredLL = (LinearLayout) findViewById(R.id.guestExpiredLL);
        mGuestExpiredButton = (Button) findViewById(R.id.guestExpiredbutton);
        mVisitorLL = (LinearLayout) findViewById(R.id.visitorLL);
        mVisitorNextButton = (Button) findViewById(R.id.visitorNextbutton);

        mAurovilianRadioButton.setOnClickListener(new RadioGroup.OnClickListener() {
            public void onClick(View v){
                mAurovilianLL.setVisibility(View.VISIBLE);
                mGuestLL.setVisibility(View.GONE);
                mVisitorLL.setVisibility(View.GONE);
                mGuestExpiredLL.setVisibility(View.GONE);
            }
        });

        mGuestRadioButton.setOnClickListener(new RadioGroup.OnClickListener() {
            public void onClick(View v){
                mGuestLL.setVisibility(View.VISIBLE);
                mGuestPhoneNumberET.requestFocus();
                mAurovilianLL.setVisibility(View.GONE);
                mVisitorLL.setVisibility(View.GONE);
                mGuestExpiredLL.setVisibility(View.GONE);
            }
        });

        mVisitorRadioButton.setOnClickListener(new RadioGroup.OnClickListener() {
            public void onClick(View v){
                mVisitorLL.setVisibility(View.VISIBLE);
                mGuestLL.setVisibility(View.GONE);
                mAurovilianLL.setVisibility(View.GONE);
                mGuestExpiredLL.setVisibility(View.GONE);
            }
        });

        // Google Sign-in related
        mGSO = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(Scopes.EMAIL))
                .setHostedDomain(mDomainName)
                .build();

        mGAC = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGSO)
                .build();

        mAurovilianSignInButton.setOnClickListener(this);

        // Guest Sign-in related
        mGuestSignInButton.setOnClickListener(this);

        // Visitor Sign-in related
        mVisitorNextButton.setOnClickListener(this);

        mRadioButtonsLL.setVisibility(View.VISIBLE);

        if (ApplicationStore.getUserLevel() == ApplicationStore.AUROVILIAN) {
            mAurovilianRadioButton.performClick();
        }
        else if (ApplicationStore.getUserLevel() == ApplicationStore.GUEST) {
            mGuestRadioButton.performClick();
        }
        else {
            mVisitorRadioButton.performClick();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aurovilianSignInbutton:
                aurovilianSignIn();
                break;
            case R.id.guestSignInbutton:
                guestSignIn();
                break;
            case R.id.visitorNextbutton:
                visitorSignIn();
                break;
        }
    }

    private int RC_AUROVILIAN_SIGN_IN = 101;

    private void aurovilianSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGAC);
        startActivityForResult(signInIntent, RC_AUROVILIAN_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_AUROVILIAN_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleAurovilianSignInResult(result);
        }
    }

    private void handleAurovilianSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String emailID = acct.getEmail();
            String userName = acct.getDisplayName();
            if (emailID.toLowerCase().contains(mDomainName)) {
                // Aurovilian fully authenticated. Set up the Aurovilian profile
                ApplicationStore.setAurovilianProfile(userName, emailID);
                launchMainActivity();
            }
            else {
                Auth.GoogleSignInApi.signOut(mGAC);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Wrong email ID");
                alertDialog.setIcon(R.drawable.ic_error);
                alertDialog.setMessage("Please use your auroville.org.in email ID.");
                alertDialog.setNeutralButton("OK", null);
                alertDialog.create().show();
            }
        }
        else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Sign In Failed");
            alertDialog.setIcon(R.drawable.ic_error);
            alertDialog.setMessage("Please use your auroville.org.in email ID to sign in.");
            alertDialog.setNeutralButton("OK", null);
            alertDialog.create().show();
        }
    }

    private void launchMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void guestSignIn() {
        if (mGuestPhoneNumberET.getText().toString().isEmpty()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Phone Number Required");
            alertDialog.setIcon(R.drawable.ic_error);
            alertDialog.setMessage("Please enter your phone number to sign in.");
            alertDialog.setNeutralButton("OK", null);
            alertDialog.create().show();
        }
        else {
            handleGuestSignIn();
        }
    }

    private void handleGuestSignIn() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Retrieving Your Guest Credentials");
        progressDialog.setMessage("Please wait while we retrieve your credentials...");
        progressDialog.show();

        String getGuestInfoURL = ApplicationStore.GET_GUEST_INFO_URL;
        getGuestInfoURL += "?phone=" + mGuestPhoneNumberET.getText();
        CustomRequest getGuestInfoRequest = new CustomRequest(Request.Method.GET, getGuestInfoURL, "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.cancel();
                        Gson gson = new Gson();
                        Guest guest = gson.fromJson(response, Guest.class);
                        Calendar todayCalendar = Calendar.getInstance();
                        if (todayCalendar.getTimeInMillis() > guest.getTo_date()) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mMyActivity);
                            alertDialog.setTitle("Guest Access");
                            alertDialog.setIcon(R.drawable.ic_error);
                            alertDialog.setMessage("Your Explore Auroville guest access has expired.");
                            alertDialog.setNeutralButton("OK", null);
                            alertDialog.create().show();
                        }
                        else {
                            ApplicationStore.setGuestProfile(guest);
                            launchMainActivity();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.cancel();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(mMyActivity, getString(R.string.network_error),
                                    Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof AuthFailureError){
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mMyActivity);
                            alertDialog.setTitle("Guest Access");
                            alertDialog.setIcon(R.drawable.ic_error);
                            alertDialog.setMessage("You do not have Auroville guest access.");
                            alertDialog.setNeutralButton("OK", null);
                            alertDialog.create().show();
                        }
                        else {
                            Toast.makeText(mMyActivity, getString(R.string.network_error),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }){};

        RequestQueue requestQueue = VolleyProvider.getQueue(getApplicationContext());
        requestQueue.add(getGuestInfoRequest);
    }

    private void visitorSignIn() {
        ApplicationStore.setUserLevel(ApplicationStore.VISITOR);
        launchMainActivity();
    }
}
