package ru.nbakaev.cityguide.poi;

import java.util.List;

/**
 * Created by Nikita on 10/11/2016.
 */
public interface PoiProvider {
    List<Poi> getData(double x0, double y0);
}
