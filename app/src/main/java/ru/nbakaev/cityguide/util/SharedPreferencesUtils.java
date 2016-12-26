package ru.nbakaev.cityguide.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Наташа on 22.12.2016.
 */

public class SharedPreferencesUtils {
    final public static String SP_KEY = "CITYGUIDE_SHARED_PREFERENCES";
    final public static String TRACK_ME = "TRACK_ME";

    SharedPreferences sp;

    public SharedPreferencesUtils(Context context) {
        sp = context.getSharedPreferences(SP_KEY, Activity.MODE_PRIVATE);
    }

    public boolean getTrackMe() {
        return sp.getBoolean(TRACK_ME, false);
    }

    public void setTrackMe(boolean trackMe) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(TRACK_ME, trackMe);
        editor.commit();
    }

}
