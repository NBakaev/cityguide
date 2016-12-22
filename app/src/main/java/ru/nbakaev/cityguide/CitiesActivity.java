package ru.nbakaev.cityguide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    Button pages[];
    boolean MENU_ACTIVE = false;
    MultiSelector<City> selector;

    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //App.getAppComponent().inject(this);

        setContentView(R.layout.activity_cities_list);

        setUpToolbar();
        setupMultiselector();
        setUpDrawer();
        setUpPager();
        //toolbar.setMenu(null, null);
    }

    void setupMultiselector() {
        selector = new MultiSelector<>();
        selector.setListener(new OnItemSelectedListener<City>() {
            @Override
            public void onSelect(City item, boolean selected) {

            }

            @Override
            public void onSelectorActivated(boolean activated) {
                MENU_ACTIVE = activated;
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    protected void setUpToolbar() {
        super.setUpToolbar();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_activity_cities);
    }

    void setUpPager() {
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        CityPagerAdapter adapter = new CityPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pages = new Button[]{(Button) findViewById(R.id.loadedCities), (Button) findViewById(R.id.citiesToLoad), (Button) findViewById(R.id.lcitiesAll)};
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < 3; i++) {
                    pages[i].setBackgroundResource(R.color.colorPrimary);
                }
                pages[position].setBackgroundResource(R.drawable.selected_page);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.city_menu, menu);
        MenuItem delete = menu.findItem(R.id.delete);
        MenuItem load = menu.findItem(R.id.load);

        delete.setVisible(MENU_ACTIVE);
        load.setVisible(MENU_ACTIVE);
        getSupportActionBar().setDisplayShowTitleEnabled(!MENU_ACTIVE);
        if (MENU_ACTIVE) {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        else
        {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        return true;
    }

    class CityPagerAdapter extends FragmentPagerAdapter {
        public CityPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            CityFragment fragment = new CityFragment();
            fragment.setSelector(selector);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }


}
