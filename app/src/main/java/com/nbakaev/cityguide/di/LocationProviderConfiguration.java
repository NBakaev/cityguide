package com.nbakaev.cityguide.di;

import android.content.Context;

import com.nbakaev.cityguide.background.AndroidBackgroundAware;
import com.nbakaev.cityguide.eventbus.EventBus;
import com.nbakaev.cityguide.location.AndroidLocationProvider;
import com.nbakaev.cityguide.location.LocationProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Nikita on 10/11/2016.
 */

@Module
public class LocationProviderConfiguration {

    @ApplicationScope
    @Provides
    public LocationProvider locationProvider(Context context, AndroidBackgroundAware androidBackgroundAware, EventBus eventBus) {
        return new AndroidLocationProvider(context, androidBackgroundAware, eventBus);
    }

}
