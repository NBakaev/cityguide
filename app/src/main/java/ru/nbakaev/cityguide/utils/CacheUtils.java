package ru.nbakaev.cityguide.utils;

import android.os.Environment;

import com.google.common.io.Files;

import java.io.File;

import ru.nbakaev.cityguide.poi.Poi;

/**
 * Created by ya on 11/18/2016.
 */

public class CacheUtils {

    private static String cachePath;
    static {
        File sdCardDirectory = Environment.getExternalStorageDirectory();
        cachePath = sdCardDirectory + "/cityguide/";;
    }

    public static String getCacheImagePath() {
        return cachePath;
    }

    public static String getImageCachePathForPoi(Poi poi) {
        return poi.getId() + "." + Files.getFileExtension(poi.getImageUrl());
    }

    public static File getImageCacheFile(Poi poi) {
        return new File(getCacheImagePath(), getImageCachePathForPoi(poi));
    }

}
