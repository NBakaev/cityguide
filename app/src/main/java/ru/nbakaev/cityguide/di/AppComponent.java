package ru.nbakaev.cityguide.di;

import dagger.Component;
import ru.nbakaev.cityguide.SettingsFragment;
import ru.nbakaev.cityguide.MainActivity;
import ru.nbakaev.cityguide.nearby.NearbyFragment;
import ru.nbakaev.cityguide.push.BackgroundNotificationService;
import ru.nbakaev.cityguide.push.BroadcastReceiverOnBootComplete;
import ru.nbakaev.cityguide.scan.QrScanFragment;
import ru.nbakaev.cityguide.city.CitiesFragment;
import ru.nbakaev.cityguide.BaseActivity;
import ru.nbakaev.cityguide.IntroActivity;
import ru.nbakaev.cityguide.map.MapsFragment;
import ru.nbakaev.cityguide.location.LocationProviderConfiguration;
import ru.nbakaev.cityguide.poi.PoiProviderConfiguration;

/**
 * Created by Nikita on 10/11/2016.
 */

@ApplicationScope
@Component(modules = {PoiProviderConfiguration.class, AppModule.class, LocationProviderConfiguration.class, AppProviders.class})
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
