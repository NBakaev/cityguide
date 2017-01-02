package com.nbakaev.cityguide.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.nbakaev.cityguide.city.City;
import com.nbakaev.cityguide.poi.Poi;
import com.nbakaev.cityguide.settings.SettingsService;

/**
 * Created by ya on 11/18/2016.
 */

public class CacheUtils {

    private static final String TAG = "CacheUtils";

    private SettingsService settingsService;

    public CacheUtils(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    private static String cachePath;

    static {
        File sdCardDirectory = Environment.getExternalStorageDirectory();
        cachePath = sdCardDirectory + "/cityguide/";
    }

    public static String getCacheImagePath() {
        return cachePath;
    }

    public static String getImageCachePathForPoi(Poi poi) {
        return poi.getId() + "." + Files.getFileExtension(poi.getImageUrl());
    }

    public static String getImageCachePathForCity(City city) {
        return city.getId() + "." + Files.getFileExtension(city.getImageUrl());
    }

    public static File getImageCacheFile(Poi poi) {
        return new File(getCacheImagePath(), getImageCachePathForPoi(poi));
    }

    public static File getImageCacheFile(City city) {
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

            String fileExtension = Files.getFileExtension(city.getImageUrl());
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
