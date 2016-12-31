package ru.nbakaev.cityguide;

import android.support.multidex.MultiDexApplication;

import org.greenrobot.greendao.database.Database;

import ru.nbakaev.cityguide.di.AppComponent;
import ru.nbakaev.cityguide.di.AppModule;
import ru.nbakaev.cityguide.di.DaggerAppComponent;
import ru.nbakaev.cityguide.location.LocationProviderConfiguration;
import ru.nbakaev.cityguide.poi.PoiProviderConfiguration;
import ru.nbakaev.cityguide.poi.db.DaoMaster;
import ru.nbakaev.cityguide.poi.db.DaoSession;

/**
 * Created by Nikita on 10/11/2016.
 */

public class App extends MultiDexApplication {

    private static AppComponent appComponent;
    private DaoSession daoSession;

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    // create with dagger DI
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

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "poi-db.db");

        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }


}
