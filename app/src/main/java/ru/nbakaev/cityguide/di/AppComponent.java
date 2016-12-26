package ru.nbakaev.cityguide.di;

import dagger.Component;
import ru.nbakaev.cityguide.NearbyActivity;
import ru.nbakaev.cityguide.push.BackgrounNotificationService;
import ru.nbakaev.cityguide.CitiesActivity;
import ru.nbakaev.cityguide.BaseActivity;
import ru.nbakaev.cityguide.IntroActivity;
import ru.nbakaev.cityguide.MapsActivity;
import ru.nbakaev.cityguide.QrReadActivity;
import ru.nbakaev.cityguide.about.AboutActivity;
import ru.nbakaev.cityguide.locaton.LocationProviderConfiguration;
import ru.nbakaev.cityguide.poi.PoiProviderConfiguration;

/**
 * Created by Nikita on 10/11/2016.
 */

@ApplicationScope
@Component(modules = {PoiProviderConfiguration.class, AppModule.class, LocationProviderConfiguration.class, AppProviders.class})
public interface AppComponent {

    void inject(NearbyActivity nearbyActivity);

    void inject(MapsActivity mapsActivity);

    void inject(BaseActivity baseActivity);

    void inject(CitiesActivity citiesActivity);

    void inject(AboutActivity aboutActivity);

    void inject(IntroActivity introActivity);

    void inject(QrReadActivity qrReadActivity);

    void inject(BackgrounNotificationService service);

}
