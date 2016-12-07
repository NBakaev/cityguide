package ru.nbakaev.cityguide;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.bignerdranch.android.multiselector.MultiSelector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ru.nbakaev.cityguide.city.City;
import ru.nbakaev.cityguide.ui.CityRecyclerAdapter;



public class CitiesActivity extends BaseActivity {

    RecyclerView reciclerView;
    CityRecyclerAdapter adapter;

    MultiSelector multiSelector;

    Random random = new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //App.getAppComponent().inject(this);

        setContentView(R.layout.activity_cities_list);

        setUpToolbar();
        setUpDrawer();
        setupMultiselector();
        setUpRecyclerView();
    }

    void setupMultiselector()
    {
        multiSelector = new MultiSelector();
        multiSelector.setSelectable(true);
    }

    private void setUpRecyclerView()
    {
        reciclerView = (RecyclerView) findViewById(R.id.citiesRecyclerView);
        List<City> cities = new ArrayList<>();
        String citiesArray[] = {"Moscow", "SntPetersburg", "Kazan", "Nizniy Novgorod", "Perm"};
        for (int i=0; i<citiesArray.length; i++)
        {
            City city = new City();
            city.id = ""+i;
            city.name = citiesArray[i];
            city.POINumber = random.nextInt(100);
            if (random.nextBoolean())
                city.lastUpdated = new Date();
            else
                city.lastUpdated = null;
            cities.add(city);
        }
        adapter = new CityRecyclerAdapter(this, cities, multiSelector);
        reciclerView.setAdapter(adapter);

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(this);
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        reciclerView.setLayoutManager(mLinearLayoutManagerVertical);
    }
}
