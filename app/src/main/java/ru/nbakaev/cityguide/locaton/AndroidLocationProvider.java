package ru.nbakaev.cityguide.locaton;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

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
import ru.nbakaev.cityguide.App;


/**
 * TODO: optional; unsubscribe location get if no subscribers; what do when app in background? reduce time for onLocationChanged() ?
 * Created by Nikita on 10/11/2016.
 */

public class AndroidLocationProvider implements LocationProvider, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ComponentCallbacks2 {

    private static final String TAG = "AndroidLocationProvider";

    private static final int ACTIVE_APP_LOCATION_INTERVAL = 10 * 1000;   // 10 seconds, in milliseconds
    private static final int ACTIVE_APP_LOCATION_INTERVAL_FASTEST = 1 * 1000;   // 1 seconds, in milliseconds

    private static final int BACKGROUND_APP_LOCATION_INTERVAL_FASTEST = 60 * 1000;   // 60 seconds, in milliseconds
    private static final int BACKGROUND_APP_LOCATION_INTERVAL = 100 * 1000;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private volatile Location prevLocation;

    // do not need atomic/volatile; changed only from main thread
    private boolean background = false;

    private final Context context;
    private Observable<Location> locationObservable;
    private List<ObservableEmitter<Location>> observableEmitter = new CopyOnWriteArrayList<>();
    private Looper looper;

    public AndroidLocationProvider(Context context) {
        this.context = context;

        HandlerThread handlerThread = new HandlerThread("AndroidLocationProviderLooper");
        handlerThread.start();
        looper = handlerThread.getLooper();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(ACTIVE_APP_LOCATION_INTERVAL)
                .setFastestInterval(ACTIVE_APP_LOCATION_INTERVAL_FASTEST); // 1 second, in milliseconds

        mGoogleApiClient.connect();

        // process background / foreground app
        if (context instanceof App) {
            context.registerComponentCallbacks(this);
            ((App) context).registerActivityLifecycleCallbacks(new BackgroundActivityLifecycle());
        }

        locationObservable = Observable.create(new ObservableOnSubscribe<Location>() {
            @Override
            public void subscribe(ObservableEmitter<Location> e) throws Exception {
                observableEmitter.add(e);
                if (prevLocation != null) {
                    e.onNext(prevLocation);
                }
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
            //    ActivityCompat#requestPermissions
            Toast.makeText(context, "Need permission", Toast.LENGTH_LONG).show();
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this, looper);
    }

    // called on main thread
    @Override
    public void onTrimMemory(int level) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            Log.d(TAG, "onTrimMemory: in background");
            background = true;
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();

                mLocationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(BACKGROUND_APP_LOCATION_INTERVAL)
                        .setFastestInterval(BACKGROUND_APP_LOCATION_INTERVAL_FASTEST);

                mGoogleApiClient.connect();
            }
        }
    }

    // called on main thread
    private void processStart() {
        Log.d(TAG, "processStart: after background");

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();

            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(ACTIVE_APP_LOCATION_INTERVAL)
                    .setFastestInterval(ACTIVE_APP_LOCATION_INTERVAL_FASTEST);

            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
    }

    @Override
    public void onLowMemory() {
    }

    class BackgroundActivityLifecycle implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (background) {
                processStart();
            }
            background = false;
        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (background) {
                processStart();
            }
            background = false;
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
