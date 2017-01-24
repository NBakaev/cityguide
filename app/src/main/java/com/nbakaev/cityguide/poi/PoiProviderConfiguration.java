package com.nbakaev.cityguide.poi;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.nbakaev.cityguide.di.ApplicationScope;
import com.nbakaev.cityguide.poi.server.ServerPoiProvider;
import com.nbakaev.cityguide.settings.SettingsService;
import com.nbakaev.cityguide.util.CacheUtils;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static com.nbakaev.cityguide.settings.SettingsService.getServerUrl;

/**
 * Created by Nikita on 10/11/2016.
 */

@Module
public class PoiProviderConfiguration {

    private static final String TAG = "PoiProviderConfiguratio";

    @ApplicationScope
    @Provides
    public PoiProvider poiProvider(Context context, SettingsService settingsService, CacheUtils cacheUtils) {
        boolean offlineMode = settingsService.isOffline();

        Log.d(TAG, Boolean.toString(offlineMode));

        // TODO: optional; THIS IS NOT DI !!!. move to separate module ???
        if (offlineMode) {
            return new OfflinePoiProvider(context, cacheUtils);
        } else {
            return new ServerPoiProvider(context, defaultRetrofit());
        }
    }

    //    @ApplicationScope
//    @Provides
    public Retrofit defaultRetrofit() {

        String baseUrl = getServerUrl();
        RxJava2CallAdapterFactory rxAdapter = RxJava2CallAdapterFactory.create();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addCallAdapterFactory(rxAdapter)
                .build();

        return retrofit;
    }

}
