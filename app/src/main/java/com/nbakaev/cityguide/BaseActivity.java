package com.nbakaev.cityguide;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;

import com.nbakaev.cityguide.eventbus.EventBus;
import com.nbakaev.cityguide.eventbus.events.ReInjectPoiProvider;
import com.nbakaev.cityguide.settings.AppSettings;
import com.nbakaev.cityguide.settings.SettingsService;
import com.nbakaev.cityguide.ui.navigationdrawer.NavigationDrawerAdapter;
import com.nbakaev.cityguide.ui.navigationdrawer.NavigationDrawerFragment;

import javax.inject.Inject;

/**
 * Created by Nikita on 10/9/2016.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Inject
    SettingsService settingsService;

    private DrawerLayout drawer;
    private NavigationDrawerAdapter navigationDrawerAdapter;

    @Inject
    EventBus eventBus;

    public NavigationDrawerAdapter getNavigationDrawerAdapter() {
        return navigationDrawerAdapter;
    }

    public void setNavigationDrawerAdapter(NavigationDrawerAdapter navigationDrawerAdapter) {
        this.navigationDrawerAdapter = navigationDrawerAdapter;
    }

    public DrawerLayout getDrawer() {
        return drawer;
    }

    public BaseActivity() {
    }

    protected void setupDrawer() {
        if (settingsService == null) {
            App.getAppComponent().inject(this);
        }

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drwr_fragment);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment.setUpRecyclerView(drawerLayout);
        drawer = drawerLayout;

        setupDrawerOfflineMode();
    }

    private void setupDrawerOfflineMode() {
        SwitchCompat offlineModeSwitch = (SwitchCompat) findViewById(R.id.onlineSwitch);
        offlineModeSwitch.setChecked(settingsService.isOffline());

        if (settingsService.isOfflineForced()) {
            offlineModeSwitch.setClickable(false);
        }

        offlineModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppSettings settings = settingsService.getSettings();
            settings.setOffline(isChecked);
            settingsService.saveSettings(settings);
            eventBus.post(new ReInjectPoiProvider());
        });
    }

    /**
     * we use toolbar per activity, so replace toolbar if we have once in new fragment
     *
     * @param toolbar from fragment to add to main activity
     */
    public void setToolbar(Toolbar toolbar) {
        if (toolbar != null) {
            DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drwr_fragment);
            drawerFragment.setUpDrawer(R.id.nav_drwr_fragment, drawerLayout, toolbar);
        } else {
            drawer.setDrawerListener(null);
        }
    }

//    private List<Poi> searchDataResult = new ArrayList<>();

//    public void setupSearchMenu(Menu menu) {
//        if (settingsService == null) {
//            App.getAppComponent().inject(this);
//        }
//
//        if (settingsService.getSettings().isEnableExperimentalFeature()) {
//            toolbar.inflateMenu(R.menu.menu_main);
//
//            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
//            SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
//                @Override
//                public boolean onQueryTextSubmit(String query) {
//                    return false;
//                }
//
//                @Override
//                public boolean onQueryTextChange(String newText) {
//                    // newText is text entered by user to SearchView
//                    Toast.makeText(getApplicationContext(), newText, Toast.LENGTH_LONG).show();
//                    return false;
//                }
//            };
//            searchView.setOnQueryTextListener(listener);
//        }
//    }

}
