package com.nbakaev.cityguide.nearby;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import com.nbakaev.cityguide.App;
import com.nbakaev.cityguide.BaseActivity;
import com.nbakaev.cityguide.BaseFragment;
import com.nbakaev.cityguide.R;
import com.nbakaev.cityguide.location.LocationProvider;
import com.nbakaev.cityguide.poi.Poi;
import com.nbakaev.cityguide.poi.PoiProvider;
import com.nbakaev.cityguide.poi.db.DBService;

import static com.nbakaev.cityguide.poi.PoiProvider.DISTANCE_POI_DOWNLOAD;

public class NearbyFragment extends BaseFragment {

    private static final String TAG = NearbyFragment.class.getSimpleName();

    @Inject
    PoiProvider poiProvider;

    @Inject
    LocationProvider locationProvider;

    @Inject
    DBService dbService;

    private RecyclerView recyclerView;

    private BaseActivity baseActivity;

    private View view;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        baseActivity = (BaseActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        view = inflater.inflate(R.layout.fragment_nearby, container, false);
        setUpRecyclerView();

        baseActivity.toolbar.setTitle(getString(R.string.title_activity_main));
        return view;
    }

    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

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

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(baseActivity);
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
                        // pass NearbyFragment context instead of applicationContext to have right borders in recycler view
                        RecyclerAdapter adapter = new RecyclerAdapter(baseActivity, value, locationProvider, poiProvider, baseActivity.getSupportFragmentManager());
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
