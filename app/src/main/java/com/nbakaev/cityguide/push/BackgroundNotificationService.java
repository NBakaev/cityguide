package com.nbakaev.cityguide.push;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nbakaev.cityguide.App;
import com.nbakaev.cityguide.location.LocationProvider;
import com.nbakaev.cityguide.poi.Poi;
import com.nbakaev.cityguide.poi.PoiProvider;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.nbakaev.cityguide.poi.PoiProvider.DISTANCE_POI_DOWNLOAD;
import static com.nbakaev.cityguide.poi.PoiProvider.DISTANCE_POI_DOWNLOAD_MOVE_CAMERA_REFRESH;

/**
 * Created by Наташа on 20.12.2016.
 */

public class BackgroundNotificationService extends Service {

    @Inject
    LocationProvider locationProvider;

    @Inject
    PoiProvider poiProvider;

    NotificationService notificationService;

    private static final String TAG = "BackgrounNotificationSe";

    private Location locationForPoi;

    @Override
    public void onCreate() {
        super.onCreate();
        App.getAppComponent().inject(this);
        notificationService = new NotificationService(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w("Service", "StartCommand");
        subscribeToLocationChange();
        return super.onStartCommand(intent, flags, startId);
    }

    private void subscribeToLocationChange() {

        Observer<Location> locationObserver = new Observer<Location>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, d.toString());
            }

            @Override
            public void onNext(Location value) {
                Log.d("Service", "Next Location");
                processNewLocation(value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, e.toString());
            }

            @Override
            public void onComplete() {
            }
        };

        locationProvider.getCurrentUserLocation().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(locationObserver);
    }

    private void processNewLocation(final Location location) {
        if (location == null) {
            return;
        }

        // if distance between downloaded POI and current location > DISTANCE_POI_DOWNLOAD_MOVE_CAMERA_REFRESH metres - download new POIs
        if (locationForPoi == null || location.distanceTo(locationForPoi) >= DISTANCE_POI_DOWNLOAD_MOVE_CAMERA_REFRESH) {
            locationForPoi = location;
        } else {
            return;
        }
        // get POIs from server/offline with some distance from current location
        poiProvider.getData(location.getLatitude(), location.getLongitude(), DISTANCE_POI_DOWNLOAD).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Poi>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<Poi> value) {
                        notificationService.showNotification(value, location);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
