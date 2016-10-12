package ru.nbakaev.cityguide.poi;

import dagger.Module;
import dagger.Provides;
import ru.nbakaev.cityguide.di.ApplicationScope;

/**
 * Created by Nikita on 10/11/2016.
 */

@Module
public class PoiProviderConfiguration {

    @ApplicationScope
    @Provides
    public PoiProvider poiProvider() {
        return new MockedPoiProvider();
    }

}
