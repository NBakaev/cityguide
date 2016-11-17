package ru.nbakaev.cityguide.settings;

import com.orm.SugarRecord;

/**
 * Created by ya on 11/17/2016.
 */

public class SettingsService {

    public static AppSettings getSettings() {
        AppSettings first = SugarRecord.first(AppSettings.class);
        if (first != null) {
            return first;
        }
        AppSettings appSettings = new AppSettings();
        SugarRecord.save(appSettings);
        return appSettings;
    }

    public static void saveSettings(AppSettings appSettings) {
        SugarRecord.save(appSettings);
    }

}
