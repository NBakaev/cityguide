package ru.nbakaev.cityguide.provider.locaton;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Nikita on 10/11/2016.
 */

@Module
public class LocationProviderConfiguration {

    @Singleton
    @Provides
    public LocationProvider locationProvider(Context context){
        return new AndroidLocationProvider(context);
    }

}
