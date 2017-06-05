package com.nbakaev.cityguide.settings;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.nbakaev.cityguide.App;
import com.nbakaev.cityguide.city.DaoSession;
import com.nbakaev.cityguide.util.AppUtils;

/**
 * Created by Nikita Bakaev on 11/17/2016.
 */

public class SettingsService {

    private Context context;
    private DaoSession daoSession;
    private AppSettings appSettings;
    private boolean isOfflineForced = false;

    private BitmapFactory.Options options;

    public SettingsService(Context context) {
        this.context = context;
        this.daoSession = ((App) context).getDaoSession();

        if (!isNetworkAvailable()) {
            isOfflineForced = true;
        }
    }

    public boolean isOffline() {
        return isOfflineForced || getSettings().isOffline();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getServerUrl() {
        return "https://cityguide.nbakaev.com/api/v1/";
    }

    public boolean isFirstRun() {
        return getSettings().isFirstRun();
    }

    public AppSettings getSettings() {
        if (appSettings != null) {
            return appSettings;
        }

        AppSettings first = daoSession.getAppSettingsDao().queryBuilder().unique();
        if (first != null) {
            appSettings = first;
            return first;
        }
        AppSettings appSettings = new AppSettings();
        daoSession.getAppSettingsDao().save(appSettings);
        return appSettings;
    }

    public void saveSettings(AppSettings appSettings) {
        daoSession.getAppSettingsDao().save(appSettings);
    }

    public void saveSettingsAndRestart(AppSettings appSettings) {
        daoSession.getAppSettingsDao().save(appSettings);
        daoSession.getAppSettingsDao().getDatabase().close();
        AppUtils.doRestart(context); // restart app to reload dagger
    }

    /**
     *
     * @return true if no internet connection and app is forced to work in offline mode
     */
    public boolean isOfflineForced() {
        return isOfflineForced;
    }

    public BitmapFactory.Options getDefaultBitmapOptions() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // in offline cache if already have image with inSampleSize = 7
        if (!this.isOffline()) {
            options.inSampleSize = 6;
        }

        return options;
    }

    public BitmapFactory.Options getDefaultBitmapOptionsAvatar() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // in offline cache if already have image with inSampleSize = 7
        if (!this.isOffline()) {
            options.inSampleSize = 2;
        }

        return options;
    }
}
