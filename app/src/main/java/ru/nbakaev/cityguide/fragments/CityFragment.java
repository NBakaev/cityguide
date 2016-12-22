package ru.nbakaev.cityguide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.nbakaev.cityguide.R;
import ru.nbakaev.cityguide.poi.City;
import ru.nbakaev.cityguide.ui.CityRecyclerAdapter;
import ru.nbakaev.cityguide.ui.cityselector.MultiSelector;

/**
 * Created by Наташа on 16.12.2016.
 */

public class CityFragment extends Fragment {
    private MultiSelector<City> selector;
    private RecyclerView reciclerView;
    private Random random = new Random();
    private CityRecyclerAdapter adapter;
    private List<City> cities = new ArrayList<>();


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

    public void setCities(List<City> cities)
    {
        this.cities = cities;
        if (reciclerView!=null)
            setUpRecyclerView();
    }

    public CityRecyclerAdapter getAdapter()
    {
        return adapter;
    }

    public void selectAll()
    {
        for (City city : cities)
        {
            if (!selector.isSelected(city)) {
                selector.select(city);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void setUpRecyclerView()
    {
//        List<City> cities = new ArrayList<>();
//        String citiesArray[] = {"Moscow", "SntPetersburg", "Kazan", "Nizniy Novgorod", "Perm"};
//        for (int i=0; i<citiesArray.length; i++)
//        {
//            City city = new City();
//            city.setId(""+i);
//            city.setName(citiesArray[i]);
//            city.setPois(random.nextInt(100));
//            cities.add(city);
//        }

        adapter = new CityRecyclerAdapter(getActivity(), cities, selector);
        reciclerView.setAdapter(adapter);

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(getActivity());
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        reciclerView.setLayoutManager(mLinearLayoutManagerVertical);
    }
}