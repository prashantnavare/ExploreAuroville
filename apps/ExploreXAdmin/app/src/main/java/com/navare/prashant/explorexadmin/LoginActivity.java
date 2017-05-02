package com.navare.prashant.explorexadmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.navare.prashant.shared.util.CustomRequest;
import com.navare.prashant.shared.util.VolleyProvider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {
    private CustomRequest mLoginRequest;
    private Activity mMyActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyActivity = this;
        setContentView(R.layout.activity_login);

        TextView forgotPasswordTV = (TextView) findViewById(R.id.forgot_password);
        forgotPasswordTV.setPaintFlags(forgotPasswordTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgotPasswordTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                doForgotPassword();
            }
        });

        Button loginButton = (Button) findViewById(R.id.login_btn);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        Button signupButton = (Button) findViewById(R.id.signup_btn);
        signupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                signup();
            }
        });

        doVersionCheck();
    }

    private void doVersionCheck() {
        /*
        CustomRequest versionRequest = new CustomRequest(this, Request.Method.GET, ApplicationStore.VERSION_URL, "", "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        double serverVersion = Double.valueOf(response);
                        if (serverVersion > ApplicationStore.getFleetAppVersion()) {

                            new AlertDialog.Builder(mMyActivity)
                                    .setTitle(mMyActivity.getString(R.string.new_version))
                                    .setMessage(mMyActivity.getString(R.string.new_version_available_message))
                                    .setCancelable(false)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .setIcon(R.drawable.attention_24px)
                                    .show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = getString(R.string.version_check_failed);
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            if (error.networkResponse.data != null)
                                errorMsg = new String(error.networkResponse.data);
                        }
                        Toast.makeText(LaunchActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }){};

        RequestQueue requestQueue = VolleyProvider.getQueue(this);
        requestQueue.add(versionRequest);
        */
    }

    private void doForgotPassword() {
        /*
        Intent forgorPasswordIntent = new Intent(LaunchActivity.this, ForgotPasswordActivity.class);
        startActivity(forgorPasswordIntent);
        */
    }

    private void login(){

        String userName = ((EditText) findViewById(R.id.username)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();
        if (userName.isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_username), Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_password), Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("LoginActivity",userName+ ":" + password);

        String requestBody = "";
        try {
            requestBody = "username=" + URLEncoder.encode(userName, "UTF-8");
            requestBody += "&password=" + URLEncoder.encode(password, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mLoginRequest = new CustomRequest(Request.Method.POST, ApplicationStore.LOGIN_URL, requestBody,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent tabIntent = new Intent(LoginActivity.this, TabActivity.class);
                        startActivity(tabIntent);
                        finish();
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
        requestQueue.add(mLoginRequest);
    }

    private void signup(){

        /*
        Intent signupIntent = new Intent(this, SignupActivity.class);
        startActivity(signupIntent);
        */
    }
}
