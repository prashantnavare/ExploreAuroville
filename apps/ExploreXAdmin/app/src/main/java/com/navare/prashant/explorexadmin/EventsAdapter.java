package com.navare.prashant.explorexadmin;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.navare.prashant.shared.model.CurrentEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by prashant on 02-May-17.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.RowViewHolder> {
    private List<CurrentEvent> mEventList;
    private RelativeLayout mRelativeLayout;
    private EventsFragment mEventsFragment;


    public class RowViewHolder extends RecyclerView.ViewHolder {
        public TextView mEventNameTextView, mEventTimeTextView;
        public ImageButton mDeleteButton;


        public RowViewHolder(View view) {
            super(view);
            mEventNameTextView = (TextView) view.findViewById(R.id.eventName);
            mEventTimeTextView = (TextView) view.findViewById(R.id.eventTime);
            mDeleteButton = (ImageButton) view.findViewById(R.id.delete_btn);
            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.ll_layout);

            mDeleteButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.i("HomeAdapter.java", "route settings button position "+getAdapterPosition());
                    mEventsFragment.deleteEvent(mEventList.get(getAdapterPosition()));
                }
            });

            mRelativeLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mEventsFragment.editEvent(mEventList.get(getAdapterPosition()));
                }
            });
        }
    }

    public EventsAdapter(EventsFragment eventsFragment, List<CurrentEvent> eventList){
        this.mEventList = eventList;
        this.mEventsFragment = eventsFragment;
    }

    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_row, parent, false);

        return new RowViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RowViewHolder holder, int position) {
        CurrentEvent currentEvent = mEventList.get(position);
        holder.mEventNameTextView.setText(currentEvent.getName());

        Calendar fromCal = Calendar.getInstance();
        fromCal.setTimeInMillis(currentEvent.getFrom_date());
        Calendar toCal = Calendar.getInstance();
        toCal.setTimeInMillis(currentEvent.getTo_date());
        SimpleDateFormat sdfDay = new SimpleDateFormat("EEE, dd/MM/yyyy");
        String timingString = sdfDay.format(fromCal.getTime());
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
        timingString += " (" + sdfTime.format(fromCal.getTime());
        timingString += " -- " + sdfTime.format(toCal.getTime()) + ")";
        holder.mEventTimeTextView.setText(timingString);
    }

    @Override
    public int getItemCount() {

        return mEventList.size();
    }
}
