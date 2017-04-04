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
import com.nbakaev.cityguide.location.LocationProvider;
import com.nbakaev.cityguide.poi.PoiProvider;

import javax.inject.Inject;

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

        if (toolbar != null){
            toolbar.setTitle(getString(R.string.title_activity_main));
        }
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

}
