package ru.nbakaev.cityguide;

import android.location.Location;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ru.nbakaev.cityguide.adapter.RecyclerAdapter;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);

        setContentView(R.layout.activity_main);

        setUpToolbar();
        setUpDrawer();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        Location prevLocation = MapsActivity.prevLocation;

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
