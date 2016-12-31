package ru.nbakaev.cityguide.di;

import android.content.Context;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ru.nbakaev.cityguide.background.AndroidBackgroundAware;
import ru.nbakaev.cityguide.poi.db.DBService;
import ru.nbakaev.cityguide.scan.QrCodeParser;
import ru.nbakaev.cityguide.settings.SettingsService;
import ru.nbakaev.cityguide.util.CacheUtils;

import static ru.nbakaev.cityguide.settings.SettingsService.getServerUrl;

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

    @ApplicationScope
    @Provides
    public QrCodeParser qrCodeParser() {
        return new QrCodeParser();
    }


}
