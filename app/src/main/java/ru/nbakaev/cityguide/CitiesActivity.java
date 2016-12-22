package ru.nbakaev.cityguide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.nbakaev.cityguide.poi.City;
import ru.nbakaev.cityguide.fragments.CityFragment;
import ru.nbakaev.cityguide.poi.server.ServerCityProvider;
import ru.nbakaev.cityguide.poi.server.ServerPoiProvider;
import ru.nbakaev.cityguide.ui.CityRecyclerAdapter;
import ru.nbakaev.cityguide.ui.cityselector.MultiSelector;
import ru.nbakaev.cityguide.ui.cityselector.OnItemSelectedListener;


public class CitiesActivity extends BaseActivity {

    RecyclerView reciclerView;
    CityRecyclerAdapter adapter;
    Button load;
    Button pages[];
    boolean MENU_ACTIVE = false;
    ArrayList<MultiSelector<City>> selectors;
    CityFragment fragments[];
    ViewPager pager;

    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //App.getAppComponent().inject(this);

        setContentView(R.layout.activity_cities_list);

        setUpToolbar();
        setupMultiselector();
        setUpDrawer();
        fragments = new CityFragment[3];

        for (int i = 0; i<3; i++) {
            CityFragment fragment = new CityFragment();
            fragment.setSelector(selectors.get(i));
            //fragment.setCities(cities);
            fragments[i] = fragment;
        }
        setUpPager();
        setCitiesLists();
//        new ServerPoiProvider(this).getCities().observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<City>>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(List<City> value) {
//                fragments[2].setCities(value);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
        //toolbar.setMenu(null, null);
//        List<City> res = new ServerCityProvider(this).getCities();
//        fragments[2].setCities(res);
    }

    private void setCitiesLists()
    {
        setLoadedCities();
        setToLoadCities();
        setAllCities();
    }

    private  void setAllCities()
    {
        new ServerPoiProvider(this).getCities().observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<City>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<City> value) {
                fragments[2].setCities(value);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void setToLoadCities()
    {
        List<City> cities = new ArrayList<>();
        String citiesArray[] = {"Moscow", "SntPetersburg", "Kazan", "Nizniy Novgorod", "Perm"};
        for (int i=0; i<citiesArray.length; i++)
        {
            City city = new City();
            city.setId(""+i);
            city.setName(citiesArray[i]);
            city.setPois(random.nextInt(100));
            cities.add(city);
        }
        fragments[1].setCities(cities);
    }

    private void setLoadedCities()
    {
        List<City> cities = new ArrayList<>();
        String citiesArray[] = {"Moscow", "SntPetersburg", "Kazan", "Nizniy Novgorod", "Perm"};
        for (int i=0; i<citiesArray.length; i++)
        {
            City city = new City();
            city.setId(""+i);
            city.setName(citiesArray[i]);
            city.setPois(random.nextInt(100));
            cities.add(city);
        }
        fragments[0].setCities(cities);
    }

    void setupMultiselector() {
        selectors = new ArrayList<>();
        for (int i=0; i<3; i++) {
            MultiSelector<City> selector = new MultiSelector<>();
            final int finalI = i;
            selector.setListener(new OnItemSelectedListener<City>() {
                @Override
                public void onSelect(City item, boolean selected) {

                }

                @Override
                public void onSelectorActivated(boolean activated) {
                    setMenuActtivated(activated);
                }

                @Override
                public void onClear() {
                    if (fragments[finalI].getAdapter()!=null) {
                        fragments[finalI].getAdapter().notifyDataSetChanged();
                    }
                }
            });
            selectors.add(selector);
        }
    }

    private void setMenuActtivated(boolean activated)
    {
        MENU_ACTIVE = activated;
        invalidateOptionsMenu();
    }

    @Override
    protected void setUpToolbar() {
        super.setUpToolbar();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_activity_cities);
    }

    void setUpPager() {
        pager = (ViewPager) findViewById(R.id.pager);
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
                    selectors.get(i).clear();
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
        MenuItem selectAll = menu.findItem(R.id.selectAll);
        MenuItem deselect = menu.findItem(R.id.deselectAll);

        delete.setVisible(MENU_ACTIVE);
        load.setVisible(MENU_ACTIVE);
        selectAll.setVisible(MENU_ACTIVE);
        deselect.setVisible(MENU_ACTIVE);
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
//            CityFragment fragment = new CityFragment();
//            fragment.setSelector(selector);
//            return fragment;
            return fragments[position];
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO:
        //handlers

        MultiSelector<City> selector = selectors.get(pager.getCurrentItem());
        CityFragment fragment = fragments[pager.getCurrentItem()];
        switch (item.getItemId())
        {
            case R.id.selectAll:
                fragment.selectAll();
                break;
            default:
                selector.clear();

        }
        return false;
    }
}
