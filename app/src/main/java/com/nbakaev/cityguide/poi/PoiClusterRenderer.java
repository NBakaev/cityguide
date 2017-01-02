package com.nbakaev.cityguide.poi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import com.nbakaev.cityguide.map.MapsFragment;
import com.nbakaev.cityguide.settings.SettingsService;
import com.nbakaev.cityguide.util.CacheUtils;
import com.nbakaev.cityguide.util.StringUtils;

/**
 * Created by ya on 11/19/2016.
 */

public class PoiClusterRenderer extends DefaultClusterRenderer<Poi> {

    private static final String TAG = MapsFragment.class.getSimpleName();
    private PoiProvider poiProvider;
    private final BitmapFactory.Options options = new BitmapFactory.Options();
    private SettingsService settingsService;
    private CacheUtils cacheUtils;

    public PoiClusterRenderer(Context context, GoogleMap map, ClusterManager<Poi> clusterManager, PoiProvider poiProvider, SettingsService settingsService, CacheUtils cacheUtils) {
        super(context, map, clusterManager);
        this.poiProvider = poiProvider;
        this.settingsService = settingsService;
        this.cacheUtils = cacheUtils;

        // in offline cache if already have image with inSampleSize = 7
        if (!settingsService.isOffline()) {
            options.inSampleSize = 7;
        }
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 1;
    }

    @Override
    protected void onClusterItemRendered(final Poi poi, final Marker marker) {
        super.onClusterItemRendered(poi, marker);

        marker.setTag(poi);

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
                        cacheUtils.cachePoiImage(bitmap, poi);
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
        } else {
            // if have not image - show name
            marker.showInfoWindow();
        }
    }

    @Override
    protected void onBeforeClusterItemRendered(final Poi poi, final MarkerOptions markerOptions) {
        markerOptions.title(poi.getName());
//        if (!StringUtils.isEmpty(poi.getDescription())) {
//            markerOptions.snippet(poi.getDescription());
//        }
    }

}
