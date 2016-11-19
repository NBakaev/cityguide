package ru.nbakaev.cityguide.poi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.io.Files;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import ru.nbakaev.cityguide.MapsActivity;
import ru.nbakaev.cityguide.settings.SettingsService;
import ru.nbakaev.cityguide.utils.StringUtils;

import static ru.nbakaev.cityguide.utils.CacheUtils.getCacheImagePath;
import static ru.nbakaev.cityguide.utils.CacheUtils.getImageCacheFile;

/**
 * Created by ya on 11/19/2016.
 */

public class PoiClusterRenderer extends DefaultClusterRenderer<Poi>  {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private PoiProvider poiProvider;
    private final BitmapFactory.Options options = new BitmapFactory.Options();

    public PoiClusterRenderer(Context context, GoogleMap map, ClusterManager<Poi> clusterManager, PoiProvider poiProvider) {
        super(context, map, clusterManager);
        this.poiProvider = poiProvider;

        if (!SettingsService.getSettings().isOffline()) {
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
        } else {
            // if have not image - show name
            marker.showInfoWindow();
        }
    }

    @Override
    protected void onBeforeClusterItemRendered(final Poi poi, final MarkerOptions markerOptions) {
        markerOptions.title(poi.getName());
        if (!StringUtils.isEmpty(poi.getDescription())) {
            markerOptions.snippet(poi.getDescription());
        }
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
}
