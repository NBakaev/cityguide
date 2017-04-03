package com.nbakaev.cityguide.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Nikita on 10/24/2016.
 */

public class MapUtils {

    private static final String DEFAULT_TITLE = "City Guide";

    /**
     * @param distance distance inb metres
     * @return
     */
    public static String printDistance(Float distance) {
        if (distance > 1000) {
            return String.format("%.2f", distance / 1000) + " km";
        }

        return String.format("%.2f", distance) + " m";
    }

    public static String setupNameHeader(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String et_lugar;

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                et_lugar = returnedAddress.getThoroughfare();
//                StringBuilder strReturnedAddress = new StringBuilder();
//                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
//                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("");
//                }
//                et_lugar = strReturnedAddress.toString();
            } else {
                et_lugar = DEFAULT_TITLE;
            }
        } catch (IOException e) {
            et_lugar = DEFAULT_TITLE;
        }

        if (et_lugar == null) {
            et_lugar = DEFAULT_TITLE;
        }
        return et_lugar;
    }

}
