package com.nbakaev.cityguide;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.logentries.logger.AndroidLogger;
import com.nbakaev.cityguide.city.DaoMaster;
import com.nbakaev.cityguide.city.DaoSession;
import com.nbakaev.cityguide.di.AppComponent;
import com.nbakaev.cityguide.di.AppModule;
import com.nbakaev.cityguide.di.DaggerAppComponent;
import com.nbakaev.cityguide.location.LocationProviderConfiguration;
import com.nbakaev.cityguide.poi.PoiProviderConfiguration;
import com.nbakaev.cityguide.util.logger.LogentriesLogger;

import org.greenrobot.greendao.database.Database;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Created by Nikita on 10/11/2016.
 */

public class App extends MultiDexApplication {

    private static AppComponent appComponent;
    private DaoSession daoSession;
    private AndroidLogger instance;

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

        if (BuildConfig.ENABLE_CRASHLYTICS){
            Fabric.with(this, new Crashlytics());
        }

        if (BuildConfig.DEBUG) {
//        if (false) {
            Timber.plant(new Timber.DebugTree());
        } else {
            instance = LogentriesLogger.instance(this);
            Timber.plant(new CrashReportingTree());
        }

        appComponent = buildComponent();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "poi-db.db");

        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }


    /**
     * A tree which logs important information for crash reporting.
     */
    private class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            if (instance == null){
                return;
            }

            instance.log(priority + " " + "" + tag + " " + message);

            if (t != null) {
                if (priority == Log.ERROR) {
                    instance.log(priority + " ERROR " + t + " " + "" + tag + " " + message);
                } else if (priority == Log.WARN) {
                    instance.log(priority + " WARNING " + t + " " + "" + tag + " " + message);
                }
            }
        }
    }

}
