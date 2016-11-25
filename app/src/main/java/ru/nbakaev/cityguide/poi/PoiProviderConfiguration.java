package ru.nbakaev.cityguide.poi;

import android.content.Context;
import android.util.Log;

import dagger.Module;
import dagger.Provides;
import ru.nbakaev.cityguide.di.ApplicationScope;
import ru.nbakaev.cityguide.poi.server.ServerPoiProvider;
import ru.nbakaev.cityguide.settings.AppSettings;
import ru.nbakaev.cityguide.settings.SettingsService;

/**
 * Created by Nikita on 10/11/2016.
 */

@Module
public class PoiProviderConfiguration {

    private final static String TAG = PoiProviderConfiguration.class.getSimpleName();

    @ApplicationScope
    @Provides
    public PoiProvider poiProvider(Context context, SettingsService settingsService) {
        AppSettings settings = settingsService.getSettings();
        boolean offlineMode = settings.isOffline();

        Log.d(TAG, Boolean.toString(offlineMode));

        // TODO: optional; THIS IS NOT DI !!!. move to separate module ???
        if (offlineMode) {
            return new OfflinePoiProvider(context);
        } else {
//            return new MockedPoiProvider(context);
            return new ServerPoiProvider(context);
        }
    }

}
