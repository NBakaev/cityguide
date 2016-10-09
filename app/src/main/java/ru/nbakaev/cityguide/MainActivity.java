package ru.nbakaev.cityguide;

import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import ru.nbakaev.cityguide.adapter.RecyclerAdapter;
import ru.nbakaev.cityguide.model.Landscape;


/**
 Full Height Navigation Drawer Demo
 */
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

}
