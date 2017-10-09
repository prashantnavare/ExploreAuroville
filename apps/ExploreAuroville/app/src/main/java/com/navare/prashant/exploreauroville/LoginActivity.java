package com.navare.prashant.exploreauroville;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

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
    private String mDomainName = "futureschool.org.in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                .addApi(Auth.CREDENTIALS_API)
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
            if (emailID.toLowerCase().contains(mDomainName)) {
                // Aurovilian fully authenticated. Set up the Aurovilian profile
                ApplicationStore.createAurovilianProfile(emailID);
                launchMainActivity();
            }
            else {
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
    }

    private void visitorSignIn() {
        ApplicationStore.setUserLevel(ApplicationStore.VISITOR);
        launchMainActivity();
    }


    /* Code for guest login - to be used later
    Calendar todayCalendar = Calendar.getInstance();
            if (todayCalendar.getTimeInMillis() > ApplicationStore.getGuestValidity()) {
        ApplicationStore.setUserLevel(ApplicationStore.VISITOR);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Guest Access");
        alertDialog.setIcon(R.drawable.ic_error);
        alertDialog.setMessage("Your Explore Auroville guest access has expired.");
        alertDialog.setNeutralButton("OK", null);
        alertDialog.create().show();
    }
            else {
        // Go straight to MainActivity
        launchMainActivity();
    }
    */
}
