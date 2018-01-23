package com.navare.prashant.experienceauroville;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.navare.prashant.shared.model.Guest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by prashant on 1/19/2018.
 */

public class GuestListAdapter extends BaseAdapter {
    GuestAccessActivity mActivity;
    LayoutInflater inflater;
    private List<Guest> mGuestList = new ArrayList<>();

    public GuestListAdapter(GuestAccessActivity activity, List<Guest> guestList){
        this.mActivity = activity;
        this.mGuestList.addAll(guestList);
        inflater = LayoutInflater.from(mActivity);
    }

    public class ViewHolder{

        RelativeLayout rl;
        TextView name;
        TextView phone;
        TextView validFrom;
        TextView validTill;
    }

    @Override
    public int getCount() {
        return mGuestList.size();
    }

    @Override
    public Guest getItem(int position) {
        return mGuestList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final GuestListAdapter.ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new GuestListAdapter.ViewHolder();
            view = inflater.inflate(R.layout.guest_item, null);
            // Locate the TextViews in guest_item.xml
            viewHolder.rl = (RelativeLayout) view.findViewById(R.id.rl);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.phone = (TextView) view.findViewById(R.id.phone);
            viewHolder.validFrom = (TextView) view.findViewById(R.id.validFrom);
            viewHolder.validTill = (TextView) view.findViewById(R.id.validTill);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (GuestListAdapter.ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        Guest guest = mGuestList.get(position);

        viewHolder.name.setText(guest.getName());
        viewHolder.phone.setText("Phone Number: " + guest.getPhone());
        viewHolder.validFrom.setText("Valid From: " + guest.getValidFrom());
        viewHolder.validTill.setText("Valid Till: " + guest.getValidTill());

        // If the guest to_date is in the past, gray it out
        if (guest.getTo_date() < Calendar.getInstance().getTimeInMillis()) {
            viewHolder.rl.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorExpired));
        }
        else {
            viewHolder.rl.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.windowBackground));
        }

        // Listen for ListView Item Click
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mActivity.showGuestDetails(mGuestList.get(position));
            }
        });

        return view;
    }
}
