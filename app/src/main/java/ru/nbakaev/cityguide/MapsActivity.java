package ru.nbakaev.cityguide;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.nbakaev.cityguide.locaton.LocationProvider;
import ru.nbakaev.cityguide.poi.LocationDiff;
import ru.nbakaev.cityguide.poi.Poi;
import ru.nbakaev.cityguide.poi.PoiProvider;
import ru.nbakaev.cityguide.utils.StringUtils;

import static ru.nbakaev.cityguide.poi.PoiProvider.DISTANCE_POI_DOWNLOAD;
import static ru.nbakaev.cityguide.poi.PoiProvider.DISTANCE_POI_DOWNLOAD_MOVE_CAMERA_REFRESH;
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
        options.inSampleSize = 7;
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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    Consider calling
            //    ActivityCompat#requestPermissions
            Toast.makeText(getApplicationContext(), "Need permission", Toast.LENGTH_LONG).show();
            return;
        } else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setTiltGesturesEnabled(false);
        }

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

    private void processNewPois(List<Poi> newPoi){
        Context ctx = getApplication();
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        for (Poi poi : newPoi){
            NotificationCompat.Builder b = new NotificationCompat.Builder(ctx);

            b.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                    .setTicker(poi.getName())
                    .setContentTitle(poi.getName())
                    .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
//                    .setContentInfo("Info")
 ;
            Intent notificationIntent = new Intent(this, MapsActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            b.setContentIntent(contentIntent);

            Location location = new Location("M");
            location.setLatitude(poi.getLocation().getLatitude());
            location.setLongitude(poi.getLocation().getLongitude());

            if (prevLocation != null){
                float v = prevLocation.distanceTo(location);
                b.setContentText(poi.getDescription() != null ? poi.getDescription() + "," + printDistance(v) : printDistance(v));
            }

            notificationManager.notify(poi.getId().hashCode(), b.build());
        }

    }

    private void processDrawMarkers(List<Poi> data) {

        if (data.isEmpty() && !currentMarkers.isEmpty()){
            Set<Map.Entry<String, Marker>> entries = currentMarkers.entrySet();
            for (Map.Entry<String, Marker> entry : entries){
                entry.getValue().remove();
            }
        }

        LocationDiff locationDiff = LocationDiff.of(prevLocationData, data);
        processNewPois(locationDiff.getNewPoi());

        for (Poi removePoi : locationDiff.getRemovePoi()){
            Marker marker1 = currentMarkers.get(removePoi.getId());
            marker1.remove();
            currentMarkers.remove(removePoi.getId());
        }

        for (final Poi poi : locationDiff.getNewPoi()) {

            LatLng loca = new LatLng(poi.getLocation().getLatitude(), poi.getLocation().getLongitude());
            final MarkerOptions markerOptions = new MarkerOptions().position(loca).title(poi.getName());
            final Marker marker = mMap.addMarker(markerOptions);

            if (locationDiff.getNewPoi().contains(poi)){
                currentMarkers.put(poi.getId(), marker);
            }

            marker.showInfoWindow();

            if (poi.getImage() != null) {
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeByteArray(poi.getImage(), 0, poi.getImage().length, options)));
            } else if (!StringUtils.isEmpty(poi.getImageUrl())) {
                Call<ResponseBody> icon = poiProvider.getIcon(poi.getImageUrl());
                icon.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeStream(response.body().byteStream(), null, options)));
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.w(TAG, call.toString());
                    }
                });

            }

            if (!StringUtils.isEmpty(poi.getDescription())) {
                marker.setSnippet(poi.getDescription());
            }

        }

        Log.d(TAG, "Download new Poi for location" + locationForPoi.toString());
        prevLocationData = data;
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
            moveAndZoomCameraToLocationWithoutAnimation(location);
        }

        prevLocation = location;
        drawMarkers(prevLocation);

        moveAndZoomCameraToLocation(location, false);
    }

    private void moveAndZoomCameraToLocation(final Location location, final boolean drawMarkers) {
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
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                if (drawMarkers) {
                    drawMarkers(location);
                }
            }
        }, 1000);
    }

    private void moveAndZoomCameraToLocationWithoutAnimation(final Location location) {
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
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                drawMarkers(location);
            }
        }, 0);
    }

}
