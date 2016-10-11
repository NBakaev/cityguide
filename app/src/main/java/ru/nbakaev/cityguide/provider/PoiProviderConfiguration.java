package ru.nbakaev.cityguide.provider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Nikita on 10/11/2016.
 */

@Module
public class PoiProviderConfiguration {

    @Singleton
    @Provides
    public PoiProvider poiProvider(){
        return new MockedPoiProvider();
    }

}
