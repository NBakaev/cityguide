package ru.nbakaev.cityguide.poi;

/**
 * Created by Nikita on 10/14/2016.
 */
public class SearchRequest extends Poi.PoiLocation {

    private double radius;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
