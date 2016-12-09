package ru.nbakaev.cityguide.utils;

/**
 * Created by Nikita on 10/24/2016.
 */

public class MapUtils {

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

}
