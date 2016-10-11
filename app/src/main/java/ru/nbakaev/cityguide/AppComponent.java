package ru.nbakaev.cityguide;

import javax.inject.Singleton;

import dagger.Component;
import ru.nbakaev.cityguide.provider.PoiProviderConfiguration;
import ru.nbakaev.cityguide.provider.locaton.LocationProviderConfiguration;

/**
 * Created by Nikita on 10/11/2016.
 */

@Singleton
@Component(modules = {PoiProviderConfiguration.class, AppModule.class, LocationProviderConfiguration.class})
public interface AppComponent {

    void inject(MainActivity mainActivity);
    void inject(MapsActivity mapsActivity);

}
