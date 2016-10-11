package ru.nbakaev.cityguide;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by Nikita on 10/9/2016.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar toolbar;

    protected void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("City Guide");
        toolbar.inflateMenu(R.menu.menu_main);
    }

    protected void setUpDrawer() {
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drwr_fragment);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment.setUpDrawer(R.id.nav_drwr_fragment, drawerLayout, toolbar);
    }

}
