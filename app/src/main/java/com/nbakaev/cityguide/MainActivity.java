package com.nbakaev.cityguide;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.nbakaev.cityguide.location.LocationProvider;
import com.nbakaev.cityguide.scan.QrCodeParser;
import com.nbakaev.cityguide.util.FragmentsWalker;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.nbakaev.cityguide.util.FragmentsWalker.FRAGMENT_OPEN;
import static com.nbakaev.cityguide.util.FragmentsWalker.MOVE_TO_POI_ID;
import static com.nbakaev.cityguide.util.FragmentsWalker.NEARBY;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    @Inject
    QrCodeParser qrCodeParser;

    @Inject
    LocationProvider locationProvider;

    private Snackbar showLocationRequest;

    private void startIntroActivity() {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MaterialTheme); // dismiss splash screen
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);

        if (settingsService.isFirstRun() || isNeedPermissions()) {
            startIntroActivity();
            return;
        }

        setContentView(R.layout.activity_main);
        setupDrawer();

        if (savedInstanceState != null){
            // prevent creating another new main fragment(map)
            return;
        }

        setupMainFragment();
        processNewIntent(getIntent());
        showThatWeDoNotUserLocation();
    }

    private void showThatWeDoNotUserLocation() {
        if (locationProvider.getLastKnownLocation() == null) {
            showLocationRequest = Snackbar.make(findViewById(R.id.drawer_layout), "Can not find your location", Snackbar.LENGTH_INDEFINITE);
            showLocationRequest.show();

            if (isNeedPermissions()) {
                Snackbar.make(findViewById(R.id.drawer_layout), "We need location permission", Snackbar.LENGTH_INDEFINITE).setAction("Grant", v -> {
                }).show();
            }

            Observer<Location> locationObserver = new Observer<Location>() {
                @Override
                public void onSubscribe(Disposable d) {
                    Log.d(TAG, d.toString());
                }

                @Override
                public void onNext(Location value) {
                    if (showLocationRequest != null){
                        showLocationRequest.dismiss();
                    }
                    onComplete();
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
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processNewIntent(intent);
    }

    private void processNewIntent(Intent intent) {
        String sData = intent.getDataString();
        if (sData != null) {
            if (qrCodeParser.isOurQrCode(sData)) {
                processPoiUrl(sData);
            }
        }

        String fragmentOpen = intent.getStringExtra(FRAGMENT_OPEN);
        if (fragmentOpen != null) {
            if (NEARBY.equals(fragmentOpen)) {
                FragmentsWalker.startNearbyFragment(getSupportFragmentManager());
            }
        }

        String poiOpen = intent.getStringExtra(MOVE_TO_POI_ID);
        if (poiOpen != null) {
            FragmentsWalker.startMapFragmentWithPoiOpen(getSupportFragmentManager(), poiOpen);
        }
    }

    private void processPoiUrl(String sData) {
        String poiId = qrCodeParser.getPoiFromUrl(sData);
        FragmentsWalker.startMapFragmentWithPoiOpen(getSupportFragmentManager(), poiId);
    }

    /**
     * @return true if we need to ask user to grant runtime permissions
     */
    private boolean isNeedPermissions() {
        // we have permissions
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Need location permission");
            return true;
        } else {
            return false;
        }
    }

    private void setupMainFragment() {
        FragmentsWalker.startMapFragment(getSupportFragmentManager());
    }

}
