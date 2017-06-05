package com.nbakaev.cityguide.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.common.io.Files;
import com.nbakaev.cityguide.city.City;
import com.nbakaev.cityguide.poi.Poi;
import com.nbakaev.cityguide.settings.SettingsService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Nikita Bakaev on 11/18/2016.
 */

public class CacheUtils {

    private static final String TAG = "CacheUtils";

    private SettingsService settingsService;

    private Context context;
    private File cacheDir;

    public CacheUtils(SettingsService settingsService, Context context) {
        this.settingsService = settingsService;
        this.context = context;
        cacheDir = context.getCacheDir();
    }

    private String getCacheImagePath() {
        return cacheDir.getAbsolutePath();
    }

    public String getImageCachePathForPoi(Poi poi) {
        return poi.getId() + "." + Files.getFileExtension(poi.getContent().getImageUrl());
    }

    public String getImageCachePathForCity(City city) {
        return city.getId() + "." + Files.getFileExtension(city.getContent().getImageUrl());
    }

    public  File getImageCacheFile(Poi poi) {
        return new File(getCacheImagePath(), getImageCachePathForPoi(poi));
    }

    public File getImageCacheFile(City city) {
        return new File(getCacheImagePath(), getImageCachePathForCity(city));
    }

    public void cachePoiImage(Bitmap bitmap, Poi poi) {
        if (settingsService.isOffline()) {
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

            String fileExtension = Files.getFileExtension(poi.getContent().getImageUrl());
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

    public void cacheCityImage(Bitmap bitmap, City city) {
        if (settingsService.isOffline()) {
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

            String fileExtension = Files.getFileExtension(city.getContent().getImageUrl());
            File image = getImageCacheFile(city);
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
