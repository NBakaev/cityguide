package ru.nbakaev.cityguide.di;

import dagger.Component;
import ru.nbakaev.cityguide.AboutFragment;
import ru.nbakaev.cityguide.NearbyFragment;
import ru.nbakaev.cityguide.QrScanFragment;
import ru.nbakaev.cityguide.push.BackgrounNotificationService;
import ru.nbakaev.cityguide.CitiesFragment;
import ru.nbakaev.cityguide.BaseActivity;
import ru.nbakaev.cityguide.IntroActivity;
import ru.nbakaev.cityguide.MapsFragment;
import ru.nbakaev.cityguide.locaton.LocationProviderConfiguration;
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

    void inject(CitiesFragment citiesFragment);

    void inject(AboutFragment aboutFragment);

    void inject(IntroActivity introActivity);

    void inject(QrScanFragment qrScanFragment);

    void inject(BackgrounNotificationService service);

}
