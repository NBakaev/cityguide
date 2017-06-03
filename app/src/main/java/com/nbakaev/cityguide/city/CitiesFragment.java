package com.nbakaev.cityguide.city;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nbakaev.cityguide.App;
import com.nbakaev.cityguide.BaseActivity;
import com.nbakaev.cityguide.BaseFragment;
import com.nbakaev.cityguide.R;
import com.nbakaev.cityguide.poi.PoiProvider;
import com.nbakaev.cityguide.poi.db.DBService;
import com.nbakaev.cityguide.settings.SettingsService;
import com.nbakaev.cityguide.util.CacheUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CitiesFragment extends BaseFragment {

    @Inject
    PoiProvider poiProvider;

    @Inject
    DBService dbService;

    @Inject
    SettingsService settingsService;

    @Inject
    CacheUtils cacheUtils;

    private BaseActivity baseActivity;
    private GridLayoutManager lLayout;
    private View view;
    private static final int CITIES_PER_COLUMN = 2;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar.setTitle(getString(R.string.title_activity_cities));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        baseActivity = (BaseActivity) context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
        view = inflater.inflate(R.layout.fragment_cities_main, container, false);
        loadCities();
        return view;
    }

    private void loadCities() {
        poiProvider.getCities().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<City>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<City> value) {
                lLayout = new GridLayoutManager(CitiesFragment.this.baseActivity, CITIES_PER_COLUMN);
                RecyclerView rView = (RecyclerView) view.findViewById(R.id.cities_recycler_view);
                rView.setHasFixedSize(true);
                rView.setLayoutManager(lLayout);

                CityRecyclerViewAdapter rcAdapter = new CityRecyclerViewAdapter(CitiesFragment.this.baseActivity, value, poiProvider, settingsService);
                rView.setAdapter(rcAdapter);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    protected void inject() {
        App.getAppComponent().inject(this);
    }

}
