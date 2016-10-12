package ru.nbakaev.cityguide;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import ru.nbakaev.cityguide.poi.Poi;
import ru.nbakaev.cityguide.poi.PoiProvider;
import ru.nbakaev.cityguide.locaton.LocationProvider;

import static ru.nbakaev.cityguide.poi.PoiProvider.DISTANCE_POI_DOWNLOAD;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    public static final String TAG = MapsActivity.class.getSimpleName();

//    private ExecutorService locationProcessor = Executors.newFixedThreadPool(1);

    private Location prevLocation;
    private Location locationForPoi;

    @Inject
    PoiProvider poiProvider;

    @Inject
    LocationProvider locationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the mMap is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setUpToolbar();
        setUpDrawer();


        App.getAppComponent().inject(this);
    }

    /**
     * Manipulates the mMap once available.
     * This callback is triggered when the mMap is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

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
//            mMap.setBuildingsEnabled(false);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });

        subscribeToMapsChange();

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

    private void drawMarkers(Location cameraCenter) {
        if (cameraCenter == null) {
            return;
        }


        // if distance between downloaded POI and current location > 20 metres - download new POIs
        if (locationForPoi == null || cameraCenter.distanceTo(locationForPoi) >= DISTANCE_POI_DOWNLOAD){
            mMap.clear();
            locationForPoi = cameraCenter;
        }else{
            return;
        }
        List<Poi> data = poiProvider.getData(cameraCenter.getLatitude(), cameraCenter.getLongitude(), DISTANCE_POI_DOWNLOAD);

        for (Poi poi : data) {
            LatLng loca = new LatLng(poi.getLocation().getLatitude(), poi.getLocation().getLongitude());
            this.mMap.addMarker(new MarkerOptions().position(loca).title("Marker in " + poi.getName()));
        }
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

        locationProvider.getCurrentUserLocation().subscribe(locationObserver);
    }

    private void handleNewLocation(Location location) {

        if (prevLocation == null) {
            // first run app or disconnect - move to current user location
            moveAndZoomCameraToLocation(location);
        }

        prevLocation = location;
        drawMarkers(prevLocation);

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private void moveAndZoomCameraToLocation(final Location location) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the mMap to location user
                        .zoom(17)                   // Sets the zoom
//                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                drawMarkers(location);
            }
        }, 1000);
    }

}
