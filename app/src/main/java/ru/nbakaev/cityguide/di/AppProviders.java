package ru.nbakaev.cityguide.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import ru.nbakaev.cityguide.background.AndroidBackgroundAware;
import ru.nbakaev.cityguide.poi.db.DBService;
import ru.nbakaev.cityguide.settings.SettingsService;
import ru.nbakaev.cityguide.utils.CacheUtils;

/**
 * Created by Nikita on 10/11/2016.
 */

@Module
public class AppProviders {

    @ApplicationScope
    @Provides
    public DBService dbService(Context context, SettingsService settingsService) {
        return new DBService(context, settingsService);
    }

    @ApplicationScope
    @Provides
    public SettingsService settingsService(Context context) {
        return new SettingsService(context);
    }

    @ApplicationScope
    @Provides
    public CacheUtils cacheUtils(SettingsService settingsService) {
        return new CacheUtils(settingsService);
    }

    @ApplicationScope
    @Provides
    public AndroidBackgroundAware androidBackgroundAware(Context context) {
        return new AndroidBackgroundAware(context);
    }

}
