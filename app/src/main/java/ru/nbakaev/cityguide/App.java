package ru.nbakaev.cityguide;

import android.support.multidex.MultiDexApplication;

import com.orm.SugarContext;

import ru.nbakaev.cityguide.di.AppComponent;
import ru.nbakaev.cityguide.di.DaggerAppComponent;
import ru.nbakaev.cityguide.locaton.LocationProviderConfiguration;
import ru.nbakaev.cityguide.poi.PoiProviderConfiguration;

/**
 * Created by Nikita on 10/11/2016.
 */

public class App extends MultiDexApplication {

    private static AppComponent appComponent;

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    protected AppComponent buildComponent() {

        return DaggerAppComponent.builder()
                .poiProviderConfiguration(new PoiProviderConfiguration())
                .appModule(new AppModule(this))
                .locationProviderConfiguration(new LocationProviderConfiguration())
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = buildComponent();
        SugarContext.init(this);
    }
}
