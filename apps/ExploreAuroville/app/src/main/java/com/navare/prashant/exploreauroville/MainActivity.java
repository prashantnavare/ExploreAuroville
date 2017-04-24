package com.navare.prashant.exploreauroville;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    GridView    mGridView;
    String[]    mTileText = new String[4];
    int[]       mTileImage = {R.drawable.map_72px, R.drawable.current_events_72px, R.drawable.user_72px, R.drawable.feedback_yellow_96px};
    Activity    mMyActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMyActivity = this;

        mTileText[0]=getString(R.string.map);
        mTileText[1]=getString(R.string.current_events);
        mTileText[2]=getString(R.string.profile);
        mTileText[3]=getString(R.string.feedback);

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
}

