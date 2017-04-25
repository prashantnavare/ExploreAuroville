package com.navare.prashant.exploreauroville;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.navare.prashant.exploreauroville.model.CurrentEvent;
import com.navare.prashant.exploreauroville.model.POI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by prashant on 23-Apr-17.
 */

public class CurrentEventListAdapter extends BaseAdapter {
    Activity mActivity;
    LayoutInflater inflater;
    private List<CurrentEvent> mEventListToShow = new ArrayList<>();
    private List<CurrentEvent> mEventListAll = new ArrayList<>();

    public CurrentEventListAdapter(Activity activity, List<CurrentEvent> eventList){
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
        timingString += " -- " + sdfTime.format(toCal.getTime()) + ")";
        viewHolder.timing.setText(timingString);

        POI poi = ApplicationStore.getPOI(currentEvent.getPOI_id());
        if (poi != null) {
            viewHolder.location.setText(poi.getName());
        }

        viewHolder.description.setText(currentEvent.getDescription());

        // If the event is in the past, gray it out
        if (currentEvent.getFrom_date() < Calendar.getInstance().getTimeInMillis()) {
            viewHolder.rl.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorExpired));
        }

        // Listen for ListView Item Click
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Send single item click data to SingleItemView Class
                Log.i("CurrentEventListAdapter", "Selected event "+ mEventListToShow.get(position).getName());
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
                if (event.getName().toLowerCase(Locale.getDefault()).contains(charText) || event.getTags().toLowerCase(Locale.getDefault()).contains(charText) || event.getDescription().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    mEventListToShow.add(event);
                }
                else {
                    POI poi = ApplicationStore.getPOI(event.getId());
                    if (poi != null) {
                        if (poi.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                            mEventListToShow.add(event);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
}
