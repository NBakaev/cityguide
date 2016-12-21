package ru.nbakaev.cityguide;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.multidex.MultiDexApplication;

import org.greenrobot.greendao.database.Database;

import ru.nbakaev.cityguide.di.AppComponent;
import ru.nbakaev.cityguide.di.DaggerAppComponent;
import ru.nbakaev.cityguide.locaton.LocationProviderConfiguration;
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


//        Intent intent = new Intent(BackgrounNotificationService.class.getName());
//        intent.setPackage(this.getPackageName());

//        bindService(intent, new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName componentName) {
//
//            }
//        }, BIND_AUTO_CREATE);
        startService(new Intent(getApplicationContext(), BackgrounNotificationService.class));
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }


}
