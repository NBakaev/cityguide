package com.nbakaev.cityguide.nearby;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nbakaev.cityguide.App;
import com.nbakaev.cityguide.BaseActivity;
import com.nbakaev.cityguide.BaseFragment;
import com.nbakaev.cityguide.R;
import com.nbakaev.cityguide.eventbus.events.ReInjectPoiProvider;
import com.nbakaev.cityguide.location.LocationProvider;
import com.nbakaev.cityguide.poi.PoiProvider;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.nbakaev.cityguide.util.FragmentsWalker.OPEN_FRAGMENT;

public class NearbyFragment extends BaseFragment {

    private static final String TAG = "NearbyFragment";

    @Inject
    PoiProvider poiProvider;

    @Inject
    LocationProvider locationProvider;

    private RecyclerView recyclerView;
    private BaseActivity baseActivity;
    private View view;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(OPEN_FRAGMENT, TAG);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar.setTitle(R.string.drawer_near_me);
    }

    @Override
    protected void inject() {
        App.getAppComponent().inject(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        baseActivity = (BaseActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
        view = inflater.inflate(R.layout.fragment_nearby, container, false);
        setUpRecyclerView();
        subscribeToReInjectPoiProvider();
        return view;
    }

    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        RecyclerAdapter adapter = new RecyclerAdapter(baseActivity, locationProvider, poiProvider, baseActivity.getSupportFragmentManager());
        recyclerView.setAdapter(adapter);

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(baseActivity);
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void subscribeToReInjectPoiProvider() {
        eventBus.observable(ReInjectPoiProvider.class).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<ReInjectPoiProvider>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ReInjectPoiProvider value) {
                inject();
                setUpRecyclerView();
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
