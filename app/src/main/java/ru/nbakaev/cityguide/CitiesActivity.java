package ru.nbakaev.cityguide;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.nbakaev.cityguide.fragments.EmptyFragment;
import ru.nbakaev.cityguide.poi.City;
import ru.nbakaev.cityguide.fragments.CityFragment;
import ru.nbakaev.cityguide.poi.Poi;
import ru.nbakaev.cityguide.poi.PoiProvider;
import ru.nbakaev.cityguide.poi.db.DBService;
import ru.nbakaev.cityguide.poi.server.ServerPoiProvider;
import ru.nbakaev.cityguide.settings.SettingsService;
import ru.nbakaev.cityguide.ui.cityselector.MultiSelector;
import ru.nbakaev.cityguide.ui.cityselector.OnItemSelectedListener;
import ru.nbakaev.cityguide.utils.CacheUtils;


public class CitiesActivity extends BaseActivity {

    Button pages[];
    boolean MENU_ACTIVE = false;
    ArrayList<MultiSelector<City>> selectors;
    CityFragment fragments[];
    ViewPager pager;
    @Inject
    PoiProvider poiProvider;
    boolean offline = true;

    Random random = new Random();

    @Inject
    DBService dbService;

    @Inject
    SettingsService settingsService;

    @Inject
    CacheUtils cacheUtils;

    public CitiesActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getAppComponent().inject(this);
        offline = settingsService.getSettings().isOffline();
        //poiProvider = new ServerPoiProvider(this);

        setContentView(R.layout.activity_cities_list);

        setUpToolbar();
        setupMultiselector();
        setUpDrawer();
        fragments = new CityFragment[3];

        for (int i = 0; i < 3; i++) {
            CityFragment fragment = new CityFragment();
            fragment.setSelector(selectors.get(i));
            fragment.setCacheUtils(cacheUtils);
            fragment.setPoiProvider(poiProvider);
            fragments[i] = fragment;
        }

        if (offline)
        {
            fragments[1] = new EmptyFragment();
        }
        setUpPager();
        setCitiesLists();

    }

    List<City> loaded = new ArrayList<>();
    List<City> fromServer = new ArrayList<>();
    List<City> toLoad = new ArrayList<>();

    private void setCitiesLists() {
//        setLoadedCities();
//        setToLoadCities();
//        setAllCities();
//        getAllCities();
        if (dbService != null) {
            loaded = dbService.getCitiesFromDB();
        }
        setLoadedCities(loaded);
        if (offline) {
            setAllCities(loaded);
            setToLoadCities(new ArrayList<City>());
        }else {
            poiProvider.getCities().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<City>>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(List<City> value) {
                    fromServer = value;
                    setAllCities(fromServer);
                    toLoad = new ArrayList<City>(fromServer);
                    toLoad.removeAll(loaded);
                    setToLoadCities(toLoad);
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });
        }
    }


    private void setToLoadCities(List<City> cities) {
        fragments[1].setCities(cities);
    }

    private void setLoadedCities(List<City> cities) {
        fragments[0].setCities(cities);
    }

    private void setAllCities(List<City> cities) {
        fragments[2].setCities(cities);
    }

    void setupMultiselector() {
        selectors = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
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
                    if (fragments[finalI].getAdapter() != null) {
                        fragments[finalI].getAdapter().notifyDataSetChanged();
                    }
                }
            });
            selectors.add(selector);
        }
    }

    private void setMenuActtivated(boolean activated) {
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
                    pages[i].setTextColor(ContextCompat.getColor(CitiesActivity.this, R.color.grey_200));
                    pages[i].setTypeface(null, Typeface.NORMAL);
                    selectors.get(i).clear();
                }
                pages[position].setBackgroundResource(R.drawable.selected_page);
                pages[position].setTextColor(ContextCompat.getColor(CitiesActivity.this, R.color.white));
                pages[position].setTypeface(null, Typeface.BOLD);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        for (int i = 0; i < 3; i++) {
            final int finalI = i;
            pages[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pager.setCurrentItem(finalI, true);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int position = -1;
        if (pager != null) {
            position = pager.getCurrentItem();
        }
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.city_menu, menu);
        MenuItem delete = menu.findItem(R.id.delete);
        MenuItem load = menu.findItem(R.id.load);
        MenuItem selectAll = menu.findItem(R.id.selectAll);
        MenuItem deselect = menu.findItem(R.id.deselectAll);

        delete.setVisible(MENU_ACTIVE);
        load.setVisible(MENU_ACTIVE && !offline);
        selectAll.setVisible(MENU_ACTIVE);
        deselect.setVisible(MENU_ACTIVE);
        getSupportActionBar().setDisplayShowTitleEnabled(!MENU_ACTIVE);
        if (MENU_ACTIVE) {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        } else {
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
        switch (item.getItemId()) {
            case R.id.selectAll:
                fragment.selectAll();
                break;
            case R.id.load:
                saveCities(selector.getSelected(), selector);
                break;
            case R.id.delete:
                removeCities(selector.getSelected(), selector);
                break;
            default:
                selector.clear();

        }
        return false;
    }

    private void saveCities(final List<City> cities, final MultiSelector<City> selector) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading the cities...");
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        dialog.show();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Future<Boolean> result = dbService.cacheCityToDB(cities);
                Observer<List<Poi>> observer = new Observer<List<Poi>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Poi> value) {
                        dbService.cachePoiToDB(value);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                };
                for (City city : cities) {
                    poiProvider.getPoiFromCity(city.getId()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
                }
                while (!result.isDone()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                dialog.dismiss();
                selector.clear();
                setCitiesLists();

            }
        };
        new Handler().post(runnable);
    }

    private void removeCities(final List<City> cities, final MultiSelector<City> selector) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Removing the cities...");
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        dialog.show();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Future<Boolean> result = dbService.deleteCityFromDB(cities);
                Observer<List<Poi>> observer = new Observer<List<Poi>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Poi> value) {
                        dbService.deletePoiFromDB(value);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                };
                for (City city : cities) {
                    poiProvider.getPoiFromCity(city.getId()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
                }
                while (!result.isDone()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                dialog.dismiss();
                selector.clear();
                setCitiesLists();

            }
        };
        new Handler().post(runnable);
    }
}
