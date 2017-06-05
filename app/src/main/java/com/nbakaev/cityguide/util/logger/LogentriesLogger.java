package com.nbakaev.cityguide.util.logger;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.logentries.logger.AndroidLogger;

import java.io.IOException;

/**
 * Created by Nikita Bakaev on 1/24/2017.
 */

public class LogentriesLogger {

    public static AndroidLogger instance(Context context) {
        try {
            ApplicationInfo app = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = app.metaData;
            AndroidLogger logger = AndroidLogger.createInstance(context, false, true, false, null, 0, bundle.getString("logentries.token.ApiKey"), true);
            return logger;
        } catch (IOException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
