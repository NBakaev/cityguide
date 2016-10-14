package ru.nbakaev.cityguide.utils;

public abstract class StringUtils {

    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

}
