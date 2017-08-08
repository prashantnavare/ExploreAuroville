package com.navare.prashant.exploreauroville;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.navare.prashant.shared.model.Feedback;
import com.navare.prashant.shared.util.CustomRequest;
import com.navare.prashant.shared.util.VolleyProvider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FeedbackActivity extends AppCompatActivity {

    private EditText mFeedbackET;
    private String magicPhrase = "iamaurovillian";

    private CustomRequest mFeedbackRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        mFeedbackET = (EditText) findViewById(R.id.feedback_text);

        setTitle(getString(R.string.feedback));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageButton feedbackBtn = (ImageButton) findViewById(R.id.feedback_btn);
        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFeedback();
            }
        });
    }

    private void handleFeedback() {
        if (isUIValidated() == false) {
            return;
        }
        String feedBackString = mFeedbackET.getText().toString();
        String feedBackStringTrimmed = feedBackString.replaceAll("\\s","");
        if (feedBackStringTrimmed.equalsIgnoreCase(magicPhrase)) {
            // Turn off the ads and return.
            ApplicationStore.setAurovillian(true);
            Toast.makeText(FeedbackActivity.this, getString(R.string.ads_turned_off), Toast.LENGTH_LONG).show();
            return;
        }
        Feedback feedback = new Feedback();
        feedback.setFeedback(feedBackString);
        Gson gson = new Gson();
        String requestBody = gson.toJson(feedback);
        mFeedbackRequest = new CustomRequest(Request.Method.POST, ApplicationStore.FEEDBACK_URL, requestBody,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mFeedbackET.getText().clear();
                        Toast.makeText(FeedbackActivity.this, getString(R.string.feedback_successful), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(FeedbackActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }){};

        RequestQueue requestQueue = VolleyProvider.getQueue(getApplicationContext());
        requestQueue.add(mFeedbackRequest);
    }

    private boolean isUIValidated() {
        if (mFeedbackET.getText().toString().isEmpty()) {
            Toast.makeText(FeedbackActivity.this, getString(R.string.enter_feedback), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
