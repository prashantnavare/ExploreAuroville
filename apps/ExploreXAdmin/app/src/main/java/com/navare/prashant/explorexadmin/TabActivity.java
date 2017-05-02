package com.navare.prashant.explorexadmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.navare.prashant.shared.util.CustomRequest;

public class TabActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    static final int NUM_TABS       = 3;
    static final int EVENTS_TAB = 0;
    static final int POI_TAB = 1;
    static final int LOCATIONS_TAB = 2;

    static final String EVENTS_TAB_TITLE = "EVENTS";
    static final String POI_TAB_TITLE = "POINTS OF INTEREST";
    static final String LOCATIONS_TAB_TITLE = "LOCATIONS";

    private Activity mMyActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        mMyActivity = this;

        initTabActivity();
    }

    private void initTabActivity() {

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(EVENTS_TAB);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public void selectTab(int tabID) {
        mViewPager.setCurrentItem(tabID);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case EVENTS_TAB: return EventsFragment.newInstance();
                case POI_TAB: return POIFragment.newInstance();
                case LOCATIONS_TAB: return LocationsFragment.newInstance();
                default: return EventsFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            //total pages to show
            return NUM_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case EVENTS_TAB:
                    return EVENTS_TAB_TITLE;
                case POI_TAB:
                    return POI_TAB_TITLE;
                case LOCATIONS_TAB:
                    return LOCATIONS_TAB_TITLE;
            }
            return null;
        }
    }
}
