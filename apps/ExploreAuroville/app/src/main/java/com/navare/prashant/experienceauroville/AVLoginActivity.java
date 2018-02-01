package com.navare.prashant.experienceauroville;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.navare.prashant.experienceauroville.oauth.APIClient;
import com.navare.prashant.experienceauroville.oauth.AccessToken;
import com.navare.prashant.experienceauroville.oauth.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AVLoginActivity extends AppCompatActivity {

    // TODO Replace this with your own data
    public static final String API_LOGIN_URL = "https://example.com/oauthloginpage";
    public static final String API_OAUTH_CLIENTID = "replace-me";
    public static final String API_OAUTH_CLIENTSECRET = "replace-me";
    public static final String API_OAUTH_REDIRECT = "nl.jpelgrm.retrofit2oauthrefresh://oauth";

    private Activity mMyActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avlogin);
        mMyActivity = this;
        Intent intent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(API_LOGIN_URL));
        // This flag is set to prevent the browser with the login form from showing in the history stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri uri = getIntent().getData();
        if(uri != null && uri.toString().startsWith(API_OAUTH_REDIRECT)) {
            String code = uri.getQueryParameter("code");
            if(code != null) {
                // TODO We can probably do something with this code! Show the user that we are logging them in

                APIClient client = ServiceGenerator.createService(APIClient.class);
                Call<AccessToken> call = client.getNewAccessToken(code, API_OAUTH_CLIENTID,
                        API_OAUTH_CLIENTSECRET, API_OAUTH_REDIRECT,
                        "authorization_code");
                call.enqueue(new Callback<AccessToken>() {
                    @Override
                    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                        int statusCode = response.code();
                        if(statusCode == 200) {
                            AccessToken token = response.body();

                            // TODO Show the user they are logged in

                            ApplicationStore.setAurovilianProfile("foo", "foo");
                            Intent mainIntent = new Intent(mMyActivity, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }
                        else {
                            // TODO Handle errors on a failed response
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mMyActivity);
                            alertDialog.setTitle("Sign In Failed");
                            alertDialog.setIcon(R.drawable.ic_error);
                            alertDialog.setMessage("Please use your auroville.org.in email ID to sign in.");
                            alertDialog.setNeutralButton("OK", null);
                            alertDialog.create().show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AccessToken> call, Throwable t) {
                        // TODO Handle failure
                    }
                });
            } else {
                // TODO Handle a missing code in the redirect URI
            }
        }
    }
}
