package ru.nbakaev.cityguide.poi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class City {
    private String name;
    private String id;
    private Date lastUpdate;
    private int pois = 0;
    private String imageUrl;
    private String description;

    private List<String> imageUrls = new ArrayList<>();
    private String videoUrl;

    private double approximateRadius = 0;
    private Poi.PoiLocation location = new Poi.PoiLocation();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getPois() {
        return pois;
    }

    public void setPois(int pois) {
        this.pois = pois;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public double getApproximateRadius() {
        return approximateRadius;
    }

    public void setApproximateRadius(double approximateRadius) {
        this.approximateRadius = approximateRadius;
    }

    public Poi.PoiLocation getLocation() {
        return location;
    }

    public void setLocation(Poi.PoiLocation location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof City)
        {
            City city = (City) obj;
            return this.id.equals(city.id) && this.lastUpdate.equals(city.lastUpdate);
        }
        return super.equals(obj);
    }
}
