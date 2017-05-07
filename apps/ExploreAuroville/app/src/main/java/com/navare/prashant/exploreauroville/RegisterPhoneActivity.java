package com.navare.prashant.exploreauroville;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.navare.prashant.shared.util.CustomRequest;
import com.navare.prashant.shared.util.VolleyProvider;

public class RegisterPhoneActivity extends AppCompatActivity {

    private EditText mPhoneNumberET;
    private Activity mMyActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);

        setTitle(getString(R.string.register_for_events));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMyActivity = this;

        mPhoneNumberET   = (EditText)findViewById(R.id.phonenumber);

        Button registerButton = (Button) findViewById(R.id.register_btn);
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isUIValidated() == false) {
                    return;
                }

                String requestBody = "phone=" + mPhoneNumberET.getText().toString();
                CustomRequest registerRequest = new CustomRequest(Request.Method.POST, ApplicationStore.PHONE_REGISTER_URL, requestBody,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                ApplicationStore.setPhoneNumber(mPhoneNumberET.getText().toString());
                                Intent currentEventsActivityIntent = new Intent(mMyActivity, CurrentEventsActivity.class);
                                startActivity(currentEventsActivityIntent);
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String errorMsg = "Network error";
                                if (error.networkResponse != null) {
                                    int statusCode = error.networkResponse.statusCode;
                                    if (error.networkResponse.data != null)
                                        errorMsg = new String(error.networkResponse.data);
                                }
                                Toast.makeText(mMyActivity, errorMsg, Toast.LENGTH_LONG).show();
                            }
                        }){};

                RequestQueue requestQueue = VolleyProvider.getQueue(getApplicationContext());
                requestQueue.add(registerRequest);
            }
        });
    }

    private boolean isUIValidated() {
        if (mPhoneNumberET.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_phone_no), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mPhoneNumberET.getText().toString().length() < 10){
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.less_digits_phone_no), Toast.LENGTH_SHORT).show();
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
