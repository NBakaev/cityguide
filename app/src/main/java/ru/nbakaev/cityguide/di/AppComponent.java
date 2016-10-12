package ru.nbakaev.cityguide.di;

import dagger.Component;
import ru.nbakaev.cityguide.AppModule;
import ru.nbakaev.cityguide.MainActivity;
import ru.nbakaev.cityguide.MapsActivity;
import ru.nbakaev.cityguide.locaton.LocationProviderConfiguration;
import ru.nbakaev.cityguide.poi.PoiProviderConfiguration;

/**
 * Created by Nikita on 10/11/2016.
 */

@ApplicationScope
@Component(modules = {PoiProviderConfiguration.class, AppModule.class, LocationProviderConfiguration.class})
public interface AppComponent {

    void inject(MainActivity mainActivity);

    void inject(MapsActivity mapsActivity);

}
