package ru.nbakaev.cityguide;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ru.nbakaev.cityguide.settings.AppSettings;
import ru.nbakaev.cityguide.settings.SettingsService;
import ru.nbakaev.cityguide.ui.navigationdrawer.NavigationDrawerFragment;
import ru.nbakaev.cityguide.utils.AppUtils;

/**
 * Created by Nikita on 10/9/2016.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected String DEFAULT_TITLE = "City Guide";

    protected void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("City Guide");
//        toolbar.inflateMenu(R.menu.menu_main);
    }

    protected String setupNameHeader(double latitude, double longitude){
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
            }
            else {
                et_lugar = DEFAULT_TITLE;
            }
        } catch (IOException e) {
            et_lugar = DEFAULT_TITLE;
        }

        if (et_lugar == null){
            et_lugar = DEFAULT_TITLE;
        }
        return et_lugar;
    }

    protected void setUpDrawer() {
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drwr_fragment);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment.setUpDrawer(R.id.nav_drwr_fragment, drawerLayout, toolbar);

        SwitchCompat offlineModeSwitch = (SwitchCompat) findViewById(R.id.onlineSwitch);
        offlineModeSwitch.setChecked(SettingsService.getSettings().isOffline());
        offlineModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppSettings settings = SettingsService.getSettings();
                settings.setOffline(isChecked);
                SettingsService.saveSettings(settings);
                AppUtils.doRestart(getApplicationContext()); // restart app to reload dagger
            }
        });
    }

}
