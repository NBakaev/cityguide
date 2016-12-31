package ru.nbakaev.cityguide;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import javax.inject.Inject;

import ru.nbakaev.cityguide.scan.QrCodeParser;
import ru.nbakaev.cityguide.util.FragmentsOrganizer;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    @Inject
    QrCodeParser qrCodeParser;

    private void startIntroActivity() {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MaterialTheme);
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);

        if (settingsService.isFirstRun() || needPermissions()) {
            startIntroActivity();
            return;
        }

        setContentView(R.layout.activity_main);

        setUpToolbar();
        setUpDrawer();
        toolbar.setTitle(getString(R.string.title_activity_main));

        setupMainFragment();
        processNewIntent(getIntent());
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

        String fragmentOpen = intent.getStringExtra("FRAGMENT_OPEN");
        if (fragmentOpen != null) {
            if ("NEARBY".equals(fragmentOpen)) {
                FragmentsOrganizer.startNearbyFragment(getSupportFragmentManager());
            }
        }

        String poiOpen = intent.getStringExtra("MOVE_TO_POI_ID");
        if (poiOpen != null) {
            FragmentsOrganizer.startMapFragmentWithPoiOpen(getSupportFragmentManager(), poiOpen);
        }
    }

    private void processPoiUrl(String sData) {
        String poiId = qrCodeParser.getPoiFromUrl(sData);
        FragmentsOrganizer.startMapFragmentWithPoiOpen(getSupportFragmentManager(), poiId);
    }

    /**
     * @return true if we need to ask user to grant runtime permissions
     */
    private boolean needPermissions() {
        // we have permissions
        if (ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        // we have permissions
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Need location permission");
            startIntroActivity();
            return true;
        }
        return false;
    }

    private void setupMainFragment() {
        FragmentsOrganizer.startMapFragment(getSupportFragmentManager());
    }

}
