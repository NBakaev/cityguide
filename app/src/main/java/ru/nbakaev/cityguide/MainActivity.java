package ru.nbakaev.cityguide;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.nbakaev.cityguide.locaton.LocationProvider;
import ru.nbakaev.cityguide.poi.Poi;
import ru.nbakaev.cityguide.poi.PoiProvider;
import ru.nbakaev.cityguide.poi.db.DBService;
import ru.nbakaev.cityguide.ui.RecyclerAdapter;

import static ru.nbakaev.cityguide.poi.PoiProvider.DISTANCE_POI_DOWNLOAD;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    PoiProvider poiProvider;

    @Inject
    LocationProvider locationProvider;

    @Inject
    DBService dbService;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getAppComponent().inject(this);
        setContentView(R.layout.activity_main);

        setUpToolbar();
        setUpDrawer();
        setUpRecyclerView();
        toolbar.setTitle(getString(R.string.title_activity_main));
    }

    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        Observer<Location> locationObserver = new Observer<Location>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, d.toString());
            }

            @Override
            public void onNext(Location value) {
                handleNewLocation(value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, e.toString());
            }

            @Override
            public void onComplete() {
            }
        };

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(MainActivity.this);
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        locationProvider.getCurrentUserLocation().subscribe(locationObserver);
    }

    private void handleNewLocation(@NonNull Location prevLocation) {
        final double x;
        final double y;

        x = prevLocation.getLatitude();
        y = prevLocation.getLongitude();

        poiProvider.getData(x, y, DISTANCE_POI_DOWNLOAD).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Poi>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Poi> value) {
                        dbService.cachePoiToDB(value);
                        // pass MainActivity context instead of applicationContext to have right borders in recycler view
                        RecyclerAdapter adapter = new RecyclerAdapter(MainActivity.this, value, locationProvider, poiProvider);
                        recyclerView.setAdapter(adapter);
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
