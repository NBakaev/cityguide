package ru.nbakaev.cityguide;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import ru.nbakaev.cityguide.poi.Poi;
import ru.nbakaev.cityguide.settings.AppSettings;
import ru.nbakaev.cityguide.settings.SettingsService;
import ru.nbakaev.cityguide.ui.navigationdrawer.NavigationDrawerFragment;

/**
 * Created by Nikita on 10/9/2016.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected String DEFAULT_TITLE = "City Guide";
    private List<Poi> searchDataResult = new ArrayList<>();

    @Inject
    SettingsService settingsService;

    protected void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("City Guide");
        setupSearchMenu(toolbar.getMenu());
    }

    public BaseActivity() {
    }

    public void setupSearchMenu(Menu menu) {
        if (settingsService == null) {
            App.getAppComponent().inject(this);
        }

        if (settingsService.getSettings().isEnableExperimentalFeature()) {
            toolbar.inflateMenu(R.menu.menu_main);

            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
            SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // newText is text entered by user to SearchView
                    Toast.makeText(getApplicationContext(), newText, Toast.LENGTH_LONG).show();
                    return false;
                }
            };
            searchView.setOnQueryTextListener(listener);
        }
    }

    protected String setupNameHeader(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String et_lugar;

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                et_lugar = returnedAddress.getThoroughfare();
//                StringBuilder strReturnedAddress = new StringBuilder();
//                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
//                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("");
//                }
//                et_lugar = strReturnedAddress.toString();
            } else {
                et_lugar = DEFAULT_TITLE;
            }
        } catch (IOException e) {
            et_lugar = DEFAULT_TITLE;
        }

        if (et_lugar == null) {
            et_lugar = DEFAULT_TITLE;
        }
        return et_lugar;
    }

    protected void setUpDrawer() {
        if (settingsService == null) {
            App.getAppComponent().inject(this);
        }

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drwr_fragment);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment.setUpDrawer(R.id.nav_drwr_fragment, drawerLayout, toolbar);

        SwitchCompat offlineModeSwitch = (SwitchCompat) findViewById(R.id.onlineSwitch);
        offlineModeSwitch.setChecked(settingsService.getSettings().isOffline());
        offlineModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppSettings settings = settingsService.getSettings();
                settings.setOffline(isChecked);
                settingsService.saveSettingsAndRestart(settings);
            }
        });
    }

}
