package com.nbakaev.cityguide.city;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import com.nbakaev.cityguide.R;
import com.nbakaev.cityguide.city.cityselector.MultiSelector;
import com.nbakaev.cityguide.poi.PoiProvider;
import com.nbakaev.cityguide.util.CacheUtils;

/**
 * Created by Наташа on 16.12.2016.
 */

public class CityFragment extends Fragment {
    private MultiSelector<City> selector;
    private RecyclerView reciclerView;
    private LinearLayout empty;
    private CityRecyclerAdapter adapter;
    private List<City> cities = new ArrayList<>();
    CacheUtils cacheUtils;
    PoiProvider poiProvider;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setSelector(MultiSelector<City> selector) {
        this.selector = selector;
    }

    public void setCacheUtils(CacheUtils cacheUtils) {
        this.cacheUtils = cacheUtils;
    }

    public void setPoiProvider(PoiProvider poiProvider) {
        this.poiProvider = poiProvider;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cities, container, false);
        findAllViews(view);
        //setupMultiselector();
        setUpRecyclerView();
        return view;
    }

    void findAllViews(View view) {

        reciclerView = (RecyclerView) view.findViewById(R.id.citiesRecyclerView);
        empty = (LinearLayout) view.findViewById(R.id.empty);
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
        if (reciclerView != null)
            setUpRecyclerView();
    }

    public CityRecyclerAdapter getAdapter() {
        return adapter;
    }

    public void selectAll() {
        for (City city : cities) {
            if (!selector.isSelected(city)) {
                selector.select(city);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void setUpRecyclerView() {

        adapter = new CityRecyclerAdapter(getActivity(), cities, selector, poiProvider, cacheUtils);
        reciclerView.setAdapter(adapter);

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(getActivity());
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        reciclerView.setLayoutManager(mLinearLayoutManagerVertical);
        reciclerView.setVisibility(cities.isEmpty() ? View.GONE : View.VISIBLE);
        empty.setVisibility(cities.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
