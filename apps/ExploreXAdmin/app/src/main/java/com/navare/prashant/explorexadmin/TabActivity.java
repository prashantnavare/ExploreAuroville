package com.navare.prashant.explorexadmin;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
    static final int LOCATION_TAB = 1;
    static final int CITY_TAB = 2;

    static final String EVENTS_TAB_TITLE = "EVENTS";
    static final String LOCATION_TAB_TITLE = "LOCATIONS";
    static final String CITY_TAB_TITLE = "CITIES";

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
                case LOCATION_TAB: return LocationFragment.newInstance();
                case CITY_TAB: return CityFragment.newInstance();
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
                case LOCATION_TAB:
                    return LOCATION_TAB_TITLE;
                case CITY_TAB:
                    return CITY_TAB_TITLE;
            }
            return null;
        }
    }
}
