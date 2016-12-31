package ru.nbakaev.cityguide.location;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import ru.nbakaev.cityguide.background.AndroidBackgroundAware;
import ru.nbakaev.cityguide.di.ApplicationScope;

/**
 * Created by Nikita on 10/11/2016.
 */

@Module
public class LocationProviderConfiguration {

    @ApplicationScope
    @Provides
    public LocationProvider locationProvider(Context context, AndroidBackgroundAware androidBackgroundAware) {
        return new AndroidLocationProvider(context, androidBackgroundAware);
    }

}
