package ru.nbakaev.cityguide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.nbakaev.cityguide.R;
import ru.nbakaev.cityguide.city.City;
import ru.nbakaev.cityguide.ui.CityRecyclerAdapter;
import ru.nbakaev.cityguide.ui.cityselector.MultiSelector;
import ru.nbakaev.cityguide.ui.cityselector.OnItemSelectedListener;

/**
 * Created by Наташа on 16.12.2016.
 */

public class CityFragment extends Fragment {
    private MultiSelector<City> selector;
    private RecyclerView reciclerView;
    private Random random = new Random();
    private CityRecyclerAdapter adapter;
    private List<City> cities;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setSelector(MultiSelector<City> selector)
    {
        this.selector = selector;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cities, container, false);
        findAllViews(view);
        //setupMultiselector();
        setUpRecyclerView();
        return  view;
    }

    void findAllViews(View view)
    {

        reciclerView = (RecyclerView) view.findViewById(R.id.citiesRecyclerView);
    }



    private void setUpRecyclerView()
    {
        List<City> cities = new ArrayList<>();
        String citiesArray[] = {"Moscow", "SntPetersburg", "Kazan", "Nizniy Novgorod", "Perm"};
        for (int i=0; i<citiesArray.length; i++)
        {
            City city = new City();
            city.id = ""+i;
            city.name = citiesArray[i];
            city.POINumber = random.nextInt(100);
            city.lastUpdated = null;
            cities.add(city);
        }
        for (int i=0; i<citiesArray.length; i++)
        {
            City city = new City();
            city.id = ""+i;
            city.name = citiesArray[i];
            city.POINumber = random.nextInt(100);
            city.lastUpdated = null;
            cities.add(city);
        }
        for (int i=0; i<citiesArray.length; i++)
        {
            City city = new City();
            city.id = ""+i;
            city.name = citiesArray[i];
            city.POINumber = random.nextInt(100);
            city.lastUpdated = null;
            cities.add(city);
        }
        for (int i=0; i<citiesArray.length; i++)
        {
            City city = new City();
            city.id = ""+i;
            city.name = citiesArray[i];
            city.POINumber = random.nextInt(100);
            city.lastUpdated = null;
            cities.add(city);
        }
        adapter = new CityRecyclerAdapter(getActivity(), cities, selector);
        reciclerView.setAdapter(adapter);

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(getActivity());
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        reciclerView.setLayoutManager(mLinearLayoutManagerVertical);
    }
}
