package com.nbakaev.cityguide.di;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Nikita on 10/11/2016.
 */

@Module
public class AppModule {

    private Context context;

    @Inject
    public AppModule(@NonNull Context context) {
        this.context = context;
    }

    @Provides
    @ApplicationScope
    public Context getContext() {
        return context;
    }
}
