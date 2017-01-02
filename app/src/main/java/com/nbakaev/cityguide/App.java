package com.nbakaev.cityguide;

import android.support.multidex.MultiDexApplication;

import com.nbakaev.cityguide.city.DaoMaster;
import com.nbakaev.cityguide.city.DaoSession;
import com.nbakaev.cityguide.di.AppComponent;
import com.nbakaev.cityguide.di.AppModule;
import com.nbakaev.cityguide.di.DaggerAppComponent;
import com.nbakaev.cityguide.location.LocationProviderConfiguration;
import com.nbakaev.cityguide.poi.PoiProviderConfiguration;

import org.greenrobot.greendao.database.Database;

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
