package ru.nbakaev.cityguide.util;

import android.content.Context;

/**
 * Created by ya on 12/31/2016.
 */

public class UiUtils {

    public static int dpToPixels(Context context, int dp){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
