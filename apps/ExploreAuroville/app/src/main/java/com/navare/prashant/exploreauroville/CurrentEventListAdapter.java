package com.navare.prashant.exploreauroville;

import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.navare.prashant.shared.model.CurrentEvent;
import com.navare.prashant.shared.model.Location;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by prashant on 23-Apr-17.
 */

public class CurrentEventListAdapter extends BaseAdapter {
    CurrentEventsActivity mActivity;
    LayoutInflater inflater;
    private List<CurrentEvent> mEventListToShow = new ArrayList<>();
    private List<CurrentEvent> mEventListAll = new ArrayList<>();

    public CurrentEventListAdapter(CurrentEventsActivity activity, List<CurrentEvent> eventList){
        this.mActivity = activity;
        this.mEventListToShow.addAll(eventList);
        inflater=LayoutInflater.from(mActivity);
        this.mEventListAll.addAll(eventList);
    }

    public class ViewHolder{

        RelativeLayout rl;
        TextView name;
        TextView timing;
        TextView location;
        TextView description;
    }

    @Override
    public int getCount() {
        return mEventListToShow.size();
    }

    @Override
    public CurrentEvent getItem(int position) {
        return mEventListToShow.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.current_event_item, null);
            // Locate the TextViews in listview_item.xml
            viewHolder.rl = (RelativeLayout) view.findViewById(R.id.rl);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.timing = (TextView) view.findViewById(R.id.timing);
            viewHolder.location = (TextView) view.findViewById(R.id.location);
            viewHolder.description = (TextView) view.findViewById(R.id.description);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        CurrentEvent currentEvent = mEventListToShow.get(position);

        viewHolder.name.setText(currentEvent.getName());

        Calendar fromCal = Calendar.getInstance();
        fromCal.setTimeInMillis(currentEvent.getFrom_date());
        Calendar toCal = Calendar.getInstance();
        toCal.setTimeInMillis(currentEvent.getTo_date());
        SimpleDateFormat sdfDay = new SimpleDateFormat("EEE, dd/MM/yyyy");
        String timingString = sdfDay.format(fromCal.getTime());
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
        timingString += " (" + sdfTime.format(fromCal.getTime());
        if (currentEvent.getTo_date() == 0) {
            timingString +=  " onwards)";
        }
        else {
            timingString += " -- " + sdfTime.format(toCal.getTime()) + ")";
        }
        viewHolder.timing.setText(timingString);

        viewHolder.location.setText(currentEvent.getLocation());
        viewHolder.description.setText(currentEvent.getDescription());

        // If the event is in the past, gray it out
        if (currentEvent.getFrom_date() < Calendar.getInstance().getTimeInMillis()) {
            viewHolder.rl.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorExpired));
        }
        else {
            viewHolder.rl.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.windowBackground));
        }

        // Listen for ListView Item Click
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Send single item click data to SingleItemView Class
                Log.i("CurrentEventListAdapter", "Selected event "+ mEventListToShow.get(position).getName());
                mActivity.showEventDetails(mEventListToShow.get(position));
            }
        });

        return view;
    }

    // Filter Text
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mEventListToShow.clear();
        if (charText.length() == 0) {
            mEventListToShow.addAll(mEventListAll);
        }
        else
        {
            for (CurrentEvent event : mEventListAll)
            {
                if (event.getName().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    mEventListToShow.add(event);
                }
                else if (event.getTags() != null && event.getTags().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    mEventListToShow.add(event);
                }
                else if (event.getDescription() != null && event.getDescription().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    mEventListToShow.add(event);
                }
                else if (event.getLocation() != null && event.getLocation().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    mEventListToShow.add(event);
                }
            }
        }
        notifyDataSetChanged();
        mActivity.showNewCount(mEventListToShow.size());
    }
}
