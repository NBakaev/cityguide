package com.nbakaev.cityguide.location;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import com.nbakaev.cityguide.background.AndroidBackgroundAware;
import com.nbakaev.cityguide.background.ApplicationBackgroundStatus;


/**
 * TODO: optional; unsubscribe location get if no subscribers; what do when app in background? reduce time for onLocationChanged() ?
 * Created by Nikita on 10/11/2016.
 */

public class AndroidLocationProvider implements LocationProvider, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "AndroidLocationProvider";

    private static final int ACTIVE_APP_LOCATION_INTERVAL = 10 * 1000;   // 10 seconds, in milliseconds
    private static final int ACTIVE_APP_LOCATION_INTERVAL_FASTEST = 1 * 1000;   // 1 seconds, in milliseconds

    private static final int BACKGROUND_APP_LOCATION_INTERVAL_FASTEST = 60 * 1000;   // 60 seconds, in milliseconds
    private static final int BACKGROUND_APP_LOCATION_INTERVAL = 100 * 1000;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private volatile Location prevLocation;

    private final Context context;
    private Observable<Location> locationObservable;
    private List<ObservableEmitter<Location>> observableEmitter = new CopyOnWriteArrayList<>();
    private Looper looper;
    private Observable<ApplicationBackgroundStatus> backgroundStatusObservable;

    public AndroidLocationProvider(Context context, AndroidBackgroundAware androidBackgroundAware) {
        this.context = context;

        HandlerThread handlerThread = new HandlerThread("AndroidLocationProviderLooper");
        handlerThread.start();
        looper = handlerThread.getLooper();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        onForeground();

        locationObservable = Observable.create(new ObservableOnSubscribe<Location>() {
            @Override
            public void subscribe(ObservableEmitter<Location> e) throws Exception {
                observableEmitter.add(e);
                if (prevLocation != null) {
                    e.onNext(prevLocation);
                }
            }
        });

        backgroundStatusObservable = androidBackgroundAware.getStatusObservable();
        subscribeToBackgroundStatus();
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
        Log.w(TAG, connectionResult.getErrorMessage());
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

        for (ObservableEmitter<Location> emitter : observableEmitter) {
            try {
                emitter.onNext(location);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    @Override
    public Observable<Location> getCurrentUserLocation() {
        return locationObservable;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Need location permission");
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this, looper);
    }

    private void changeLocationUpdateIntervalMode(int interval, int fastestInterval){
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(interval)
                .setFastestInterval(fastestInterval);

        mGoogleApiClient.connect();
    }

    private void onBackground() {
        changeLocationUpdateIntervalMode(BACKGROUND_APP_LOCATION_INTERVAL, BACKGROUND_APP_LOCATION_INTERVAL_FASTEST);
    }

    // called on main thread
    private void onForeground() {
        changeLocationUpdateIntervalMode(ACTIVE_APP_LOCATION_INTERVAL, ACTIVE_APP_LOCATION_INTERVAL_FASTEST);
    }

}
