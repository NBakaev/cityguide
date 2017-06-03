package com.nbakaev.cityguide.city;

import com.nbakaev.cityguide.poi.Poi;

import java.util.Date;

public class City {
    private String name;
    private String id;
    private Date lastUpdate;
    private int pois = 0;
    private String description;

    private Poi.PoiContent content = new Poi.PoiContent();

    private Poi.PoiLocation location = new Poi.PoiLocation();

    public Poi.PoiContent getContent() {
        return content;
    }

    public void setContent(Poi.PoiContent content) {
        this.content = content;
    }

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
        if (obj instanceof City) {
            City city = (City) obj;
            return this.id.equals(city.id) && this.lastUpdate.equals(city.lastUpdate);
        }
        return super.equals(obj);
    }
}
