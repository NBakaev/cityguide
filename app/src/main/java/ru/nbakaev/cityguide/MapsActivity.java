package ru.nbakaev.cityguide;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.io.Files;
import com.orm.SugarRecord;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import ru.nbakaev.cityguide.locaton.LocationProvider;
import ru.nbakaev.cityguide.poi.LocationDiff;
import ru.nbakaev.cityguide.poi.Poi;
import ru.nbakaev.cityguide.poi.PoiProvider;
import ru.nbakaev.cityguide.poi.db.PoiDb;
import ru.nbakaev.cityguide.settings.SettingsService;
import ru.nbakaev.cityguide.utils.AppUtils;
import ru.nbakaev.cityguide.utils.StringUtils;

import static ru.nbakaev.cityguide.poi.PoiProvider.DISTANCE_POI_DOWNLOAD;
import static ru.nbakaev.cityguide.poi.PoiProvider.DISTANCE_POI_DOWNLOAD_MOVE_CAMERA_REFRESH;
import static ru.nbakaev.cityguide.utils.CacheUtils.getCacheImagePath;
import static ru.nbakaev.cityguide.utils.CacheUtils.getImageCacheFile;
import static ru.nbakaev.cityguide.utils.MapUtils.printDistance;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap;
    private Location prevLocation;
    private Location locationForPoi;

    @Inject
    PoiProvider poiProvider;

    @Inject
    LocationProvider locationProvider;

    private final BitmapFactory.Options options = new BitmapFactory.Options();

    private List<Poi> prevLocationData = new ArrayList<>();

    private Map<String, Marker> currentMarkers = new HashMap<>();

    private final int PERMISSION_LOCATION_CODE = 1;
    private final int PERMISSION_READ_WRITE_EXTERNAL = 2;
    private final int PERMISSION_ALL = 3;

    private Date lastDateUserMovingCamera = null;

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
        if (!SettingsService.getSettings().isOffline()) {
            options.inSampleSize = 7;
        }
    }

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
                    requestPermission();
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

    private void requestPermission() {
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
        subscribeToMapsChange();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionAll();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            Toast.makeText(getApplicationContext(), "Need location permission", Toast.LENGTH_LONG).show();
            return;
        } else {
            mMap.setMyLocationEnabled(true);
        }
        locationGrantedPermission();
    }

    private void locationGrantedPermission() {
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(false);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                new MaterialDialog.Builder(MapsActivity.this)
                        .title(marker.getTitle())
                        .content(marker.getSnippet())
                        .show();
                return false;
            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d(TAG, cameraPosition.toString());
                Location location = new Location("Camera");
                location.setLatitude(cameraPosition.target.latitude);
                location.setLongitude(cameraPosition.target.longitude);

                lastDateUserMovingCamera = new Date();

                drawMarkers(location);
            }
        });
    }

    private void drawMarkers(final Location cameraCenter) {
        if (cameraCenter == null) {
            return;
        }

        // if distance between downloaded POI and current location > 20 metres - download new POIs
        if (locationForPoi == null || cameraCenter.distanceTo(locationForPoi) >= DISTANCE_POI_DOWNLOAD_MOVE_CAMERA_REFRESH) {
            locationForPoi = cameraCenter;
        } else {
            return;
        }
        poiProvider.getData(cameraCenter.getLatitude(), cameraCenter.getLongitude(), DISTANCE_POI_DOWNLOAD).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Poi>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Poi> value) {
                        processDrawMarkers(value);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void sendNotificationToNewPois(List<Poi> newPoi) {
        if (!isUserMovingCamera()) {
            return;
        }

        Context ctx = getApplication();
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        for (Poi poi : newPoi) {
            NotificationCompat.Builder b = new NotificationCompat.Builder(ctx);

            b.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                    .setTicker(poi.getName())
                    .setContentTitle(poi.getName())
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
//                    .setContentInfo("Info")
            ;
            Intent notificationIntent = new Intent(this, MapsActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            b.setContentIntent(contentIntent);

            Location location = new Location("M");
            location.setLatitude(poi.getLocation().getLatitude());
            location.setLongitude(poi.getLocation().getLongitude());

            if (prevLocation != null) {
                float v = prevLocation.distanceTo(location);
                b.setContentText(poi.getDescription() != null ? poi.getDescription() + "," + printDistance(v) : printDistance(v));
            }

            notificationManager.notify(poi.getId().hashCode(), b.build());
        }

    }

    private void cachePoiToDB(List<Poi> data) {
        SugarRecord.saveInTx(PoiDb.of(data));
    }

    private void processDrawMarkers(List<Poi> data) {
        cachePoiToDB(data);

        if (data.isEmpty() && !currentMarkers.isEmpty()) {
            Set<Map.Entry<String, Marker>> entries = currentMarkers.entrySet();
            for (Map.Entry<String, Marker> entry : entries) {
                entry.getValue().remove();
            }
        }

        LocationDiff locationDiff = LocationDiff.of(prevLocationData, data);
        sendNotificationToNewPois(locationDiff.getNewPoi());

        for (Poi removePoi : locationDiff.getRemovePoi()) {
            Marker marker1 = currentMarkers.get(removePoi.getId());
            marker1.remove();
            currentMarkers.remove(removePoi.getId());
        }

        for (final Poi poi : locationDiff.getNewPoi()) {

            LatLng loca = new LatLng(poi.getLocation().getLatitude(), poi.getLocation().getLongitude());
            final MarkerOptions markerOptions = new MarkerOptions().position(loca).title(poi.getName());
            final Marker marker = mMap.addMarker(markerOptions);

            if (locationDiff.getNewPoi().contains(poi)) {
                currentMarkers.put(poi.getId(), marker);
            }

            marker.showInfoWindow();

            if (!StringUtils.isEmpty(poi.getImageUrl())) {
                Observable<ResponseBody> icon = poiProvider.getIcon(poi);
                Observer<ResponseBody> iconResult = new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, d.toString());
                    }

                    @Override
                    public void onNext(ResponseBody value) {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(value.byteStream(), null, options);
                            cachePoiImage(bitmap, poi);
                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.toString());
                    }

                    @Override
                    public void onComplete() {
                    }
                };

                icon.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(iconResult);
            }

            if (!StringUtils.isEmpty(poi.getDescription())) {
                marker.setSnippet(poi.getDescription());
            }
        }

        Log.d(TAG, "Download new Poi for location" + locationForPoi.toString());
        prevLocationData = data;
    }

    private void cachePoiImage(Bitmap bitmap, Poi poi) {
        if (SettingsService.getSettings().isOffline()) {
            return;
        }

        try {
            String cacheImagePath = getCacheImagePath();
            File file = new File(cacheImagePath);
            if (!file.exists()) {
                if (file.mkdir()) {
                    Log.d(TAG, "Cache directory is created!");
                } else {
                    Log.e(TAG, "Failed cache directory is create!");
                }
            }

            String fileExtension = Files.getFileExtension(poi.getImageUrl());
            File image = getImageCacheFile(poi);
            FileOutputStream outStream;
            outStream = new FileOutputStream(image);
            if (fileExtension.equalsIgnoreCase("png")) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);  /* 100 to keep full quality of the image */
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);  /* 100 to keep full quality of the image */
            }

            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        if (prevLocation == null) {
            // first run app or disconnect - move to current user location
            moveAndZoomCameraToLocation(location, false);
        }

        prevLocation = location;
        drawMarkers(prevLocation);

        // prevent camera go to current location if user just move map
        if (!isUserMovingCamera()) {
            moveAndZoomCameraToLocation(location, true);
        }
    }

    private void moveAndZoomCameraToLocation(final Location location, final boolean animateMove) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
        }, 0);
    }

    private boolean isUserMovingCamera() {
        return (lastDateUserMovingCamera == null || (lastDateUserMovingCamera.getTime() - new Date().getTime()) / -1000 > 5);
    }

}
