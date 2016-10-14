package ru.nbakaev.cityguide.poi;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Nikita on 10/11/2016.
 */
public interface PoiProvider {

    public static final int DISTANCE_POI_DOWNLOAD = 2000; // download all data for 1km

    Observable<List<Poi>> getData(double x0, double y0, int radius);
}
