package ru.nbakaev.cityguide;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.nbakaev.cityguide.locaton.LocationProvider;
import ru.nbakaev.cityguide.poi.Poi;
import ru.nbakaev.cityguide.poi.PoiClusterRenderer;
import ru.nbakaev.cityguide.poi.PoiProvider;
import ru.nbakaev.cityguide.poi.db.DBService;
import ru.nbakaev.cityguide.utils.AppUtils;
import ru.nbakaev.cityguide.utils.CacheUtils;

import static ru.nbakaev.cityguide.poi.PoiProvider.DISTANCE_POI_DOWNLOAD;
import static ru.nbakaev.cityguide.poi.PoiProvider.DISTANCE_POI_DOWNLOAD_MOVE_CAMERA_REFRESH;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap;
    private Location prevLocation;
    private Location locationForPoi;

    @Inject
    PoiProvider poiProvider;

    @Inject
    LocationProvider locationProvider;

    @Inject
    DBService dbService;

    @Inject
    CacheUtils cacheUtils;

    private final int PERMISSION_LOCATION_CODE = 1;
    private final int PERMISSION_READ_WRITE_EXTERNAL = 2;
    private final int PERMISSION_ALL = 3;

    private Date lastDateUserMovingCamera = null;

    // if we start activity with new Intent().putExtra("MOVE_TO_POI_ID", poi.getId());
    // this variable contains id of POI to which go

    private String moveToPoiId = null;
    private Set<String> renderedPois = new HashSet<>();

    private ClusterManager<Poi> clusterManager;
    private NotificationService notificationService;
    private PoiClusterRenderer poiClusterRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the mMap is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setUpToolbar();
        setUpDrawer();

        prevLocation = null;

        App.getAppComponent().inject(this);
        notificationService = new NotificationService(getApplicationContext());

        // if activity started with "go to poi"
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("MOVE_TO_POI_ID");
            if (value != null) {
                moveToPoiId = value;
            }
        }
    }

    // TODO: optional; https://trello.com/c/7wHEDXAv/41-refactor-runtime-permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) {
            requestPermissionAll();
            return;
        }

        if (requestCode == PERMISSION_ALL) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                AppUtils.doRestart(getApplicationContext()); // restart to activate all location observable services
                return;
            }
        }

        if (requestCode == PERMISSION_LOCATION_CODE) {

            switch (grantResults[0]) {
                case PackageManager.PERMISSION_DENIED:
                    requestPermissionLocation();
                    break;
                case PackageManager.PERMISSION_GRANTED:
                    AppUtils.doRestart(getApplicationContext()); // restart to activate all location observable services
                    break;
            }
            return;
        }

        if (requestCode == PERMISSION_READ_WRITE_EXTERNAL) {

            switch (grantResults[0]) {
                case PackageManager.PERMISSION_DENIED:
                    requestPermissionStorage();
                    break;
                case PackageManager.PERMISSION_GRANTED:
                    AppUtils.doRestart(getApplicationContext()); // restart to activate all location observable services
                    break;
            }
        }
    }

    private void requestPermissionAll() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_ALL);
    }

    private void requestPermissionLocation() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_LOCATION_CODE);
    }

    private void requestPermissionStorage() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_READ_WRITE_EXTERNAL);
    }

    /**
     * Manipulates the mMap once available.
     * This callback is triggered when the mMap is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        clusterManager = new ClusterManager<>(this, googleMap);
        poiClusterRenderer = new PoiClusterRenderer(this, mMap, clusterManager, poiProvider, settingsService, cacheUtils);
        clusterManager.setRenderer(poiClusterRenderer);

        subscribeToMapsChange();

        // TODO: optional; https://trello.com/c/7wHEDXAv/41-refactor-runtime-permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionAll();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLocation();
            Toast.makeText(getApplicationContext(), "Need location permission", Toast.LENGTH_LONG).show();
            return;
        } else {
            mMap.setMyLocationEnabled(true);
        }
        locationGrantedPermission();

        if (moveToPoiId != null) {
            moveToIntentPOI();
        }
    }

    private void moveToIntentPOI() {
        // we cache all POIs from server, so if we have POI id, DB should contain this value
        Poi poi = dbService.getPoiById(moveToPoiId);
        if (poi != null) {
            processMoveToIntentPoi(poi);
        }
    }

    private void processMoveToIntentPoi(Poi poi) {
        Location location = new Location("loca");
        location.setLatitude(poi.getLocation().getLatitude());
        location.setLongitude(poi.getLocation().getLongitude());
        moveAndZoomCameraToLocation(location, false);

        lastDateUserMovingCamera = new Date();

        // TODO: refactor; extract to function that make modal for POI. this is also used in mMap#setOnMarkerClickListener
        new MaterialDialog.Builder(MapsActivity.this)
                .title(poi.getName())
                .content(poi.getDescription())
                .show();
    }

    private void locationGrantedPermission() {
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(false);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                // TODO: we show dialog depend on poi, but should get POI data, so we should "get" POI from marker
                new MaterialDialog.Builder(MapsActivity.this)
                        .title(marker.getTitle())
                        .content(marker.getSnippet())
                        .show();
                return false;
            }
        });

        // TODO: deprecated API usage
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                clusterManager.onCameraChange(cameraPosition);
                Log.d(TAG, cameraPosition.toString());
                Location location = new Location("Camera");
                location.setLatitude(cameraPosition.target.latitude);
                location.setLongitude(cameraPosition.target.longitude);

                lastDateUserMovingCamera = new Date();

                processNewLocation(location);
            }
        });
    }

    @Override
    protected void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void processNewLocation(final Location cameraCenter) {
        if (cameraCenter == null) {
            return;
        }

        // if distance between downloaded POI and current location > 20 metres - download new POIs
        if (locationForPoi == null || cameraCenter.distanceTo(locationForPoi) >= DISTANCE_POI_DOWNLOAD_MOVE_CAMERA_REFRESH) {
            locationForPoi = cameraCenter;
            toolbar.setTitle(setupNameHeader(locationForPoi.getLatitude(), locationForPoi.getLongitude()));
        } else {
            return;
        }
        // get POIs from server/offline with some distance from current location
        poiProvider.getData(cameraCenter.getLatitude(), cameraCenter.getLongitude(), DISTANCE_POI_DOWNLOAD).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Poi>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<Poi> value) {
                        dbService.cachePoiToDB(value);
                        drawMarkers(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void sendNotificationToNewPois(final List<Poi> newPoi) {
        // do not send notification if user just move map
        if (!isUserMovingCamera()) {
            return;
        }

        notificationService.showNotification(newPoi, prevLocation);
    }

    /**
     * Draw markers for current POIs. If markers is already drawn for some POI - skip POI
     *
     * @param data
     */
    private void drawMarkers(List<Poi> data) {
        final List<Poi> newPois = new ArrayList<>();

        for (final Poi poi : data) {
            if (!renderedPois.contains(poi.getId())) {
                newPois.add(poi);
            }
        }

        // if we have new poi - add new to map
        if (newPois.size() > 0) {
            clusterManager.addItems(newPois);
            // force re-cluster after add
            clusterManager.cluster();
        }

        for (final Poi poi : newPois) {
            renderedPois.add(poi.getId());
        }

        sendNotificationToNewPois(newPois);
        Log.d(TAG, "Download new Poi for location" + locationForPoi.toString());
    }

    private void subscribeToMapsChange() {

        Observer<Location> locationObserver = new Observer<Location>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, d.toString());
            }

            @Override
            public void onNext(Location value) {
                handleNewLocation(value);
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

    private void handleNewLocation(Location location) {
        if (prevLocation == null && moveToPoiId == null) {
            // first run app or disconnect - move to current user location
            moveAndZoomCameraToLocation(location, false);
        }

        prevLocation = location;
        processNewLocation(prevLocation);

        // prevent camera go to current location if user just move map
        if (!isUserMovingCamera()) {
            moveAndZoomCameraToLocation(location, true);
        }
    }

    private void moveAndZoomCameraToLocation(final Location location, final boolean animateMove) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the mMap to location user
                .zoom(16)                   // Sets the zoom
//                        .bearing(90)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        if (animateMove) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    /**
     * TODO: is it work correctly ???. Maybe better to save last handled location and {@link prevLocation#distanceTo(cameraLocation)}
     *
     * @return true if user move cameraPosition, and that's why camera location change.
     * false if cameraPosition changes because of location change
     */
    private boolean isUserMovingCamera() {
        return (lastDateUserMovingCamera == null || (lastDateUserMovingCamera.getTime() - new Date().getTime()) / -1000 < 15);
    }

}
