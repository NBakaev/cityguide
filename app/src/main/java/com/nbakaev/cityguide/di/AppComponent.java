package com.nbakaev.cityguide.di;

import com.nbakaev.cityguide.BaseActivity;
import com.nbakaev.cityguide.IntroActivity;
import com.nbakaev.cityguide.MainActivity;
import com.nbakaev.cityguide.city.CitiesFragment;
import com.nbakaev.cityguide.map.MapsFragment;
import com.nbakaev.cityguide.nearby.NearbyFragment;
import com.nbakaev.cityguide.push.BackgroundNotificationService;
import com.nbakaev.cityguide.push.BroadcastReceiverOnBootComplete;
import com.nbakaev.cityguide.scan.QrScanFragment;
import com.nbakaev.cityguide.settings.SettingsFragment;

import dagger.Component;

/**
 * Created by Nikita on 10/11/2016.
 */

@ApplicationScope
@Component(modules = {AppModule.class, LocationProviderConfiguration.class, AppProviders.class, EventBusProviders.class, PoiProviderConfiguration.class})
public interface AppComponent {

    void inject(NearbyFragment nearbyFragment);

    void inject(MapsFragment mapsFragment);

    void inject(BaseActivity baseActivity);

    void inject(MainActivity mainActivity);

    void inject(CitiesFragment citiesFragment);

    void inject(SettingsFragment settingsFragment);

    void inject(IntroActivity introActivity);

    void inject(QrScanFragment qrScanFragment);

    void inject(BackgroundNotificationService service);

    void inject(BroadcastReceiverOnBootComplete broadcastReceiverOnBootComplete);

}
