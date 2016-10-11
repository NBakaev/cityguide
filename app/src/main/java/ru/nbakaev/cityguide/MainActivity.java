package ru.nbakaev.cityguide;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import ru.nbakaev.cityguide.adapter.RecyclerAdapter;
import ru.nbakaev.cityguide.provider.PoiProvider;
import ru.nbakaev.cityguide.provider.locaton.LocationProvider;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    @Inject
    PoiProvider poiProvider;

    @Inject
    LocationProvider locationProvider;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((App) getApplication()).buildComponent().inject(this);

        setContentView(R.layout.activity_main);

        setUpToolbar();
        setUpDrawer();
        App.getAppComponent().inject(this);
        setUpRecyclerView();
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

        locationProvider.getCurrentUserLocation().subscribe(locationObserver);
        handleNewLocation(null);
    }

    private void handleNewLocation(Location prevLocation) {
        double x;
        double y;

        if (prevLocation == null) {
            x = 0;
            y = 0;
        } else {
            x = prevLocation.getLatitude();
            y = prevLocation.getLongitude();
        }

        RecyclerAdapter adapter = new RecyclerAdapter(this, poiProvider.getData(x, y));
        recyclerView.setAdapter(adapter);

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(this);
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

}
