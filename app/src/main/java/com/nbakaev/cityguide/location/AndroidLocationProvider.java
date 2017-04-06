package com.nbakaev.cityguide.location;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nbakaev.cityguide.background.AndroidBackgroundAware;
import com.nbakaev.cityguide.background.ApplicationBackgroundStatus;
import com.nbakaev.cityguide.eventbus.EventBus;
import com.nbakaev.cityguide.eventbus.events.ReloadLocationProvider;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * TODO: optional; unsubscribe location get if no subscribers; what do when app in background? reduce time for onLocationChanged() ?
 * Created by Nikita on 10/11/2016.
 */

public class AndroidLocationProvider implements LocationProvider, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "AndroidLocationProvider";

    private static final int ACTIVE_APP_LOCATION_INTERVAL = 10 * 1000;   // 10 seconds, in milliseconds
    private static final int ACTIVE_APP_LOCATION_INTERVAL_FASTEST = 1 * 1000;   // 1 seconds, in milliseconds

    private static final int BACKGROUND_APP_LOCATION_INTERVAL = 100 * 1000;
    private static final int BACKGROUND_APP_LOCATION_INTERVAL_FASTEST = 60 * 1000;   // 60 seconds, in milliseconds

    private int THRESHOLD_FOR_DISCONNECT_BACKGROUND = 30 * 1000;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private volatile Location prevLocation;

    private final Context context;
    private Observable<Location> locationObservable;
    private List<ObservableEmitter<Location>> observableEmitter = new CopyOnWriteArrayList<>();
    private Looper looper;
    private Observable<ApplicationBackgroundStatus> backgroundStatusObservable;

    private int foregroundInterval;
    private int foregroundFastestInterval;

    private int backgroundInterval;
    private int backgroundFastestInterval;

    private int locationPriority;

    private int sendedTimesAfterChangedMode = 0;

    private volatile ApplicationBackgroundStatus backgroundStatus = null;

    private Location lastKnownLocation;
    private EventBus eventBus;

    @Override
    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public AndroidLocationProvider(Context context, AndroidBackgroundAware androidBackgroundAware, EventBus eventBus) {
        this(context, androidBackgroundAware, ACTIVE_APP_LOCATION_INTERVAL, ACTIVE_APP_LOCATION_INTERVAL_FASTEST, BACKGROUND_APP_LOCATION_INTERVAL, BACKGROUND_APP_LOCATION_INTERVAL_FASTEST,
                LocationRequest.PRIORITY_HIGH_ACCURACY, eventBus);
    }

    /**
     * @param context
     * @param androidBackgroundAware
     * @param foregroundInterval        milliseconds
     * @param foregroundFastestInterval milliseconds
     * @param backgroundInterval        milliseconds
     * @param backgroundFastestInterval milliseconds
     * @param locationPriority
     */
    public AndroidLocationProvider(Context context, AndroidBackgroundAware androidBackgroundAware, int foregroundInterval, int foregroundFastestInterval,
                                   int backgroundInterval, int backgroundFastestInterval, int locationPriority, EventBus eventBus) {

        // check that user is pass applciation context, not eg activity
        if (!(context instanceof Application)) {
            throw new IllegalArgumentException("Context must be applciation context to avoid memory leaks");
        }

        this.context = context;
        this.foregroundInterval = foregroundInterval;
        this.foregroundFastestInterval = foregroundFastestInterval;
        this.backgroundInterval = backgroundInterval;
        this.backgroundFastestInterval = backgroundFastestInterval;
        this.locationPriority = locationPriority;
        this.eventBus = eventBus;

        HandlerThread handlerThread = new HandlerThread("AndroidLocationProviderLooper");
        handlerThread.start();
        looper = handlerThread.getLooper();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationObservable = Observable.create(e -> {
            observableEmitter.add(e);
            if (prevLocation != null) {
                e.onNext(prevLocation);
            }
        });

        backgroundStatusObservable = androidBackgroundAware.getStatusObservable();
        subscribeToBackgroundStatus();
        subscribeToReloadLocation();
    }

    private void subscribeToReloadLocation() {
        eventBus.observable(ReloadLocationProvider.class).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<ReloadLocationProvider>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ReloadLocationProvider value) {
                Log.w(TAG, "Reload android location");
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.w(TAG, "failed to reload android location - no permission");
                    return;
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, AndroidLocationProvider.this, looper);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void subscribeToBackgroundStatus() {
        backgroundStatusObservable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<ApplicationBackgroundStatus>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ApplicationBackgroundStatus value) {
                        if (value.equals(ApplicationBackgroundStatus.BACKGROUND)) {
                            onBackground();
                        } else if (value.equals(ApplicationBackgroundStatus.FOREGROUND)) {
                            onForeground();
                        }
                        backgroundStatus = value;
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.getErrorMessage() != null) {
            Log.w(TAG, connectionResult.getErrorMessage());
        }
//        if (connectionResult.hasResolution()) {
//            try {
//                // Start an Activity that tries to resolve the error
//                connectionResult.startResolutionForResult(context, CONNECTION_FAILURE_RESOLUTION_REQUEST);
//            } catch (IntentSender.SendIntentException e) {
//                e.printStackTrace();
//            }
//        } else {
//            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
//        }
    }

    /**
     * called ONLY looper thread. no need any synchronized
     *
     * @param location new android device location
     */
    @Override
    public void onLocationChanged(final Location location) {

        if (prevLocation != null && location.getLatitude() == prevLocation.getLatitude() && location.getLongitude() == prevLocation.getLongitude()) {
            return;
        }

        if (location == null) {
            return;
        }

        if (prevLocation != null) {
            if (prevLocation.getTime() > location.getTime()) {
                return;
            }

            if (location.getLatitude() == prevLocation.getLatitude() && location.getLongitude() == prevLocation.getLongitude()) {
                return;
            } else {
                prevLocation = location;
            }
        } else {
            prevLocation = location;
        }

        Log.d(TAG, location.toString());

        lastKnownLocation = location;

        for (ObservableEmitter<Location> emitter : observableEmitter) {
            try {
                emitter.onNext(location);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        disconnectOnFirstBackgroundHandledLocation();
    }

    private void disconnectOnFirstBackgroundHandledLocation() {
        sendedTimesAfterChangedMode++;
        if (sendedTimesAfterChangedMode == 1 && backgroundStatus.equals(ApplicationBackgroundStatus.BACKGROUND)) {
            disconnect();
        }
    }

    @Override
    public Observable<Location> getCurrentUserLocation() {
        return locationObservable;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Need location permission");
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this, looper);
    }

    public synchronized void disconnect() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private synchronized void watchInInterval(final int interval, final int fastestInterval) {
        disconnect();

        Handler handler = new Handler(looper);
        handler.postDelayed(() -> {
            if (backgroundStatus.equals(ApplicationBackgroundStatus.BACKGROUND)) {
                watchInInterval(interval, fastestInterval);
                changeLocationUpdateIntervalMode(interval, fastestInterval);
            }
        }, fastestInterval);
    }

    public synchronized void changeLocationUpdateIntervalMode(int interval, int fastestInterval) {
        disconnect();

        mLocationRequest = LocationRequest.create()
                .setPriority(locationPriority)
                .setInterval(interval)
                .setFastestInterval(fastestInterval);

        sendedTimesAfterChangedMode = 0;
        mGoogleApiClient.connect();
    }

    private void onBackground() {
        if (backgroundInterval > THRESHOLD_FOR_DISCONNECT_BACKGROUND) {
            watchInInterval(backgroundInterval, backgroundFastestInterval);
        } else {
            changeLocationUpdateIntervalMode(backgroundInterval, backgroundFastestInterval);
        }
    }

    /**
     * called when app(ui) is open
     * called on main thread
     */
    private void onForeground() {
        changeLocationUpdateIntervalMode(foregroundInterval, foregroundFastestInterval);
    }

}
