package com.nbakaev.cityguide.poi.server;

import com.nbakaev.cityguide.poi.Poi;

/**
 * Created by Nikita on 10/14/2016.
 */
public class SearchRequestDto extends Poi.PoiLocation {

    private double radius;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
