package ru.nbakaev.cityguide;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ru.nbakaev.cityguide.city.City;
import ru.nbakaev.cityguide.ui.CityRecyclerAdapter;
import ru.nbakaev.cityguide.ui.CitySelector.MultiSelector;
import ru.nbakaev.cityguide.ui.CitySelector.OnItemSelectedListener;


public class CitiesActivity extends BaseActivity {

    RecyclerView reciclerView;
    CityRecyclerAdapter adapter;
    Button load;
    ru.nbakaev.cityguide.ui.CitySelector.MultiSelector<City> selector;

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

    void  setupMultiselector()
    {
        load = (Button) findViewById(R.id.loadCities);
        load.setVisibility(View.GONE);
        selector = new MultiSelector<>();
        selector.setListener(new OnItemSelectedListener<City>() {
            @Override
            public void onSelect(City item, boolean selected) {

            }

            @Override
            public void onSelectorActivated(boolean activated) {
                if (activated)
                    load.setVisibility(View.VISIBLE);
                else
                    load.setVisibility(View.GONE);
            }
        });
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
        adapter = new CityRecyclerAdapter(this, cities, selector);
        reciclerView.setAdapter(adapter);

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(this);
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        reciclerView.setLayoutManager(mLinearLayoutManagerVertical);
    }
}
