package com.nbakaev.cityguide.di;

import com.nbakaev.cityguide.eventbus.EventBus;
import com.nbakaev.cityguide.eventbus.RxEventBus;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Nikita on 10/11/2016.
 */

@Module
public class EventBusProviders {

    @ApplicationScope
    @Provides
    public EventBus eventBus() {
        return new RxEventBus();
    }

}
