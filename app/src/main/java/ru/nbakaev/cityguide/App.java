package ru.nbakaev.cityguide;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import ru.nbakaev.cityguide.provider.PoiProviderConfiguration;
import ru.nbakaev.cityguide.provider.locaton.LocationProviderConfiguration;

/**
 * Created by Nikita on 10/11/2016.
 */

public class App extends MultiDexApplication {

    private static AppComponent appComponent;

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    public App() {
        MultiDex.install(this);
    }

    protected AppComponent buildComponent(){

        return  DaggerAppComponent.builder()
                .poiProviderConfiguration(new PoiProviderConfiguration())
                .appModule(new AppModule(this))
                .locationProviderConfiguration(new LocationProviderConfiguration())
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        appComponent = buildComponent();
    }
}
