package com.navare.prashant.explorexadmin;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.navare.prashant.shared.model.CurrentEvent;
import com.navare.prashant.shared.model.Location;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by prashant on 07-May-17.
 */

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.RowViewHolder> {

    private List<Location> mLocationList;
    private RelativeLayout mRelativeLayout;
    private LocationFragment mLocationFragment;


    public class RowViewHolder extends RecyclerView.ViewHolder {
        public TextView mLocationNameTextView, mLocationDescriptionTextView;
        public Button mDeleteButton;

        public RowViewHolder(View view) {
            super(view);
            mLocationNameTextView = (TextView) view.findViewById(R.id.locationName);
            mLocationDescriptionTextView = (TextView) view.findViewById(R.id.locationDescription);
            mDeleteButton = (Button) view.findViewById(R.id.delete_btn);
            mRelativeLayout = (RelativeLayout) view.findViewById(R.id.ll_layout);

            mDeleteButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.i("EventsAdapter.java", "event position "+getAdapterPosition());
                    mLocationFragment.deleteLocation(mLocationList.get(getAdapterPosition()));
                }
            });

            mRelativeLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mLocationFragment.editLocation(mLocationList.get(getAdapterPosition()));
                }
            });
        }
    }

    public LocationAdapter(LocationFragment locationFragment, List<Location> locationList){
        this.mLocationList = locationList;
        this.mLocationFragment = locationFragment;
    }

    @Override
    public LocationAdapter.RowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_row, parent, false);

        return new LocationAdapter.RowViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LocationAdapter.RowViewHolder holder, int position) {
        Location currentLocation = mLocationList.get(position);
        holder.mLocationNameTextView.setText(currentLocation.getName());
        holder.mLocationDescriptionTextView.setText(currentLocation.getDescription());
    }

    @Override
    public int getItemCount() {

        return mLocationList.size();
    }
}

