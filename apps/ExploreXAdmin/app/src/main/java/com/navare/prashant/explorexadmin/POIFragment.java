package com.navare.prashant.explorexadmin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link POIFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class POIFragment extends Fragment {

    public POIFragment() {
        // Required empty public constructor
    }

    public static POIFragment newInstance() {
        POIFragment fragment = new POIFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_poi, container, false);
    }

}
