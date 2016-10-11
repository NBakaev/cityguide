package ru.nbakaev.cityguide.provider;

import java.util.List;

import ru.nbakaev.cityguide.model.Poi;

/**
 * Created by Nikita on 10/11/2016.
 */
public interface PoiProvider {
    List<Poi> getData(double x0, double y0);
}
