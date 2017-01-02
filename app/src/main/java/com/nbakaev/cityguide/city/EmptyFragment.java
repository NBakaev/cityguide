package com.nbakaev.cityguide.city;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nbakaev.cityguide.R;

/**
 * Created by Наташа on 23.12.2016.
 */

public class EmptyFragment extends CityFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cities_no_offline, container, false);

        return view;
    }
}
