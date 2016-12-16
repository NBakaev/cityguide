package ru.nbakaev.cityguide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.nbakaev.cityguide.city.City;
import ru.nbakaev.cityguide.fragments.CityFragment;
import ru.nbakaev.cityguide.ui.CityRecyclerAdapter;
import ru.nbakaev.cityguide.ui.cityselector.MultiSelector;
import ru.nbakaev.cityguide.ui.cityselector.OnItemSelectedListener;


public class CitiesActivity extends BaseActivity {

    RecyclerView reciclerView;
    CityRecyclerAdapter adapter;
    Button load;
    ru.nbakaev.cityguide.ui.cityselector.MultiSelector<City> selector;
    Button pages[];

    Random random = new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //App.getAppComponent().inject(this);

        setContentView(R.layout.activity_cities_list);

        setUpToolbar();
        setUpDrawer();
        setUpPager();
        //toolbar.setMenu(null, null);
    }

    void setUpPager()
    {
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        CityPagerAdapter adapter = new CityPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pages = new Button[]{(Button) findViewById(R.id.loadedCities), (Button) findViewById(R.id.citiesToLoad), (Button) findViewById(R.id.lcitiesAll)};
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pages[position].setBackgroundResource(R.color.colorPrimary);
            }

            @Override
            public void onPageSelected(int position) {
                pages[position].setBackgroundResource(R.drawable.selected_page);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    class CityPagerAdapter extends FragmentPagerAdapter {
        public CityPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new CityFragment();
        }

        @Override
        public int getCount() {
            return 3;
        }
    }


}
