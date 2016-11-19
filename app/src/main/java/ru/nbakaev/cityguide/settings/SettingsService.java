package ru.nbakaev.cityguide.settings;

import com.orm.SugarRecord;

/**
 * Created by ya on 11/17/2016.
 */

public class SettingsService {

    private static AppSettings appSettings;

    public static String getServerUrl(){
        return "https://s2.nbakaev.ru/api/v1/";
    }

    public static AppSettings getSettings() {
        if (appSettings != null){
            return appSettings;
        }

        AppSettings first = SugarRecord.first(AppSettings.class);
        if (first != null) {
            SettingsService.appSettings = first;
            return first;
        }
        AppSettings appSettings = new AppSettings();
        SugarRecord.save(appSettings);
        SettingsService.appSettings = appSettings;
        return appSettings;
    }

    public static void saveSettings(AppSettings appSettings) {
        SugarRecord.save(appSettings);
    }

}
