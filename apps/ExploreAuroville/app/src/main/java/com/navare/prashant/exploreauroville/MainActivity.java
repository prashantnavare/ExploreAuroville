package com.navare.prashant.exploreauroville;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class MainActivity extends AppCompatActivity implements ApplicationStore.LocationListCallback {

    GridView    mGridView;
    String[]    mTileText = new String[2];
    int[]       mTileImage = {R.drawable.map_72px, R.drawable.current_events_72px};
    Activity    mMyActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMyActivity = this;

        // Check for a newer version
        ApplicationStore.doVersionCheck(mMyActivity);

        // Build the Location List for this location
        ApplicationStore.getLocationList(this);

        mTileText[0]=getString(R.string.places);
        mTileText[1]=getString(R.string.events);

        NavigationGridAdapter adapter = new NavigationGridAdapter(this, mTileText, mTileImage);
        mGridView =(GridView)findViewById(R.id.grid);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch(position){
                    case 0:
                        Intent routeIntent = new Intent(mMyActivity, MapActivity.class);
                        startActivity(routeIntent);
                        break;
                    case 1:
                        Intent eventIntent = new Intent(mMyActivity, CurrentEventsActivity.class);
                        startActivity(eventIntent);
                        break;
                }
                        /*
                switch(position){
                    case 0:
                        Intent routeIntent = new Intent(mMyActivity, MapActivity.class);
                        startActivity(routeIntent);
                        break;
                    case 1:
                        Intent activationIntent = new Intent(mMyActivity, CurrentEventsActivity.class);
                        startActivity(activationIntent);
                        break;
                    case 2:
                        Intent profileIntent = new Intent(mMyActivity, ProfileActivity.class);
                        startActivity(profileIntent);
                        break;
                    case 3:
                        Intent feedbackIntent = new Intent(mMyActivity, FeedbackActivity.class);
                        startActivity(feedbackIntent);
                        break;
                }
                        */

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_feedback:
                onFeedback();
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onFeedback() {
        Intent feedbackIntent = new Intent(mMyActivity, FeedbackActivity.class);
        startActivity(feedbackIntent);
    }

    @Override
    public void locationListUpdated() {
        // Do nothing
    }
}

