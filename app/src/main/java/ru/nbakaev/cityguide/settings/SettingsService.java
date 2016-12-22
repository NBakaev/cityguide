package ru.nbakaev.cityguide.settings;

import android.content.Context;

import ru.nbakaev.cityguide.App;
import ru.nbakaev.cityguide.poi.db.DaoSession;
import ru.nbakaev.cityguide.utils.AppUtils;

/**
 * Created by ya on 11/17/2016.
 */

public class SettingsService {

    private Context context;
    private DaoSession daoSession;
    private AppSettings appSettings;

    public SettingsService(Context context) {
        this.context = context;
        this.daoSession = ((App) context).getDaoSession();
    }

    public static String getServerUrl(){
        return "https://s2.nbakaev.ru/api/v1/";
    }

    public boolean isFirstRun(){
        return getSettings().isFirstRun();
    }

    public AppSettings getSettings() {
        if (appSettings != null){
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

}
