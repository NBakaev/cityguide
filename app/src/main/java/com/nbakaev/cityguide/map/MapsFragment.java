package com.nbakaev.cityguide.map;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.nbakaev.cityguide.App;
import com.nbakaev.cityguide.BaseActivity;
import com.nbakaev.cityguide.BaseFragment;
import com.nbakaev.cityguide.R;
import com.nbakaev.cityguide.location.LocationProvider;
import com.nbakaev.cityguide.poi.Poi;
import com.nbakaev.cityguide.poi.PoiClusterRenderer;
import com.nbakaev.cityguide.poi.PoiDetails;
import com.nbakaev.cityguide.poi.PoiProvider;
import com.nbakaev.cityguide.poi.db.DBService;
import com.nbakaev.cityguide.settings.SettingsService;
import com.nbakaev.cityguide.util.CacheUtils;

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

import static com.nbakaev.cityguide.poi.PoiProvider.DISTANCE_POI_DOWNLOAD;
import static com.nbakaev.cityguide.poi.PoiProvider.DISTANCE_POI_DOWNLOAD_MOVE_CAMERA_REFRESH;
import static com.nbakaev.cityguide.util.FragmentsWalker.MOVE_TO_POI_ID;
import static com.nbakaev.cityguide.util.MapUtils.setupNameHeader;

public class MapsFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private static final String TAG = "MapsFragment";

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

    @Inject
    SettingsService settingsService;

    private Date lastDateUserMovingCamera = null;

    // if we start activity with new Intent().putExtra("MOVE_TO_POI_ID", poi.getId());
    // this variable contains id of POI to which go
    private String moveToPoiId = null;
    private Poi moveToPoiObject = null;

    private Set<String> renderedPois = new HashSet<>();

    private ClusterManager<Poi> clusterManager;

    private boolean googleMapsInit = false;

    private BaseActivity baseActivity;

    private View googleMapsFragment;
    private PoiDetails poiDetails;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        baseActivity = (BaseActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//         if activity started with "go to poi"
        Bundle extras = this.getArguments();
        if (extras != null) {
            String value = extras.getString(MOVE_TO_POI_ID);
            if (value != null) {
                moveToPoiId = value;

                // if we already have initialized gmaps we will not have callback that will move us
                if (googleMapsInit) {
                    moveToIntentPOI();
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);

        try {
            googleMapsFragment = inflater.inflate(R.layout.fragment_maps, container, false);
        } catch (InflateException e) {
            // here we have InflateException because we have nested fragment which can be already inflated if we press back button
            // so, we can create container in xml and dynamically replace that container with created SupportMapFragment
            // or can just ignore; see http://stackoverflow.com/questions/18206615/how-to-use-google-map-v2-inside-fragment
        }

        // Obtain the SupportMapFragment and get notified when the mMap is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        poiDetails = new PoiDetails(baseActivity, googleMapsFragment, poiProvider, settingsService);

        prevLocation = null;
        return googleMapsFragment;
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
        clusterManager = new ClusterManager<>(baseActivity, googleMap);
        PoiClusterRenderer poiClusterRenderer = new PoiClusterRenderer(baseActivity, mMap, clusterManager, poiProvider, settingsService, cacheUtils);
        clusterManager.setRenderer(poiClusterRenderer);

        subscribeToMapsChange();

        // checked in MainActivity
        //noinspection MissingPermission
        mMap.setMyLocationEnabled(true);
        locationGrantedPermission();

        mMap.setOnMapLoadedCallback(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (poiDetails != null) {
                    poiDetails.hide();
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                // do not show dialog on clustered marker
                // hack clustered marker has  null tag
                if (marker.getTag() == null || !(marker.getTag() instanceof Poi)) {
                    return false;
                }

                Poi poi = (Poi) marker.getTag();
                poiDetails.showPoiDialog(poi);
                return false;
            }
        });

        if (moveToPoiId != null) {
            moveToIntentPOI();
        }

        googleMapsInit = true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onMapLoaded() {
        poiDetails.init();
        showBottomViewOnLoad();
    }

    private void showBottomViewOnLoad() {
        // we show dialog here, not in onMapReady(), because map layout is refreshed, and
        // our dialog is hide
        if (moveToPoiId != null && moveToPoiObject != null) {
            poiDetails.showPoiDialog(moveToPoiObject);
        }
    }

    private void moveToIntentPOI() {
        poiProvider.getById(moveToPoiId).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<Poi>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Poi value) {
                        processMoveToIntentPoi(value);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void processMoveToIntentPoi(Poi poi) {
        Location location = new Location("loca");
        location.setLatitude(poi.getLocation().getLatitude());
        location.setLongitude(poi.getLocation().getLongitude());
        moveAndZoomCameraToLocation(location, false);

        lastDateUserMovingCamera = new Date();
        moveToPoiObject = poi;
    }

    private void locationGrantedPermission() {
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(false);

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

    private void processNewLocation(final Location cameraCenter) {
        if (cameraCenter == null) {
            return;
        }

        // if distance between downloaded POI and current location > 20 metres - download new POIs
        if (locationForPoi == null || cameraCenter.distanceTo(locationForPoi) >= DISTANCE_POI_DOWNLOAD_MOVE_CAMERA_REFRESH) {
            locationForPoi = cameraCenter;
            toolbar.setTitle(setupNameHeader(baseActivity.getApplicationContext(), locationForPoi.getLatitude(), locationForPoi.getLongitude()));
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
     * TODO: is it work correctly ???. Maybe better to save last handled location and prevLocation#distanceTo(cameraLocation)
     *
     * @return true if user move cameraPosition, and that's why camera location change.
     * false if cameraPosition changes because of location change
     */
    private boolean isUserMovingCamera() {
        return (lastDateUserMovingCamera == null || (lastDateUserMovingCamera.getTime() - new Date().getTime()) / -1000 < 15);
    }

}
