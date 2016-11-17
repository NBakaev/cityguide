package ru.nbakaev.cityguide.poi.db;

import com.orm.dsl.Table;

import java.util.ArrayList;
import java.util.List;

import ru.nbakaev.cityguide.poi.Poi;

/**
 * Created by ya on 11/17/2016.
 */
@Table
public class PoiDb {

    // sugar orm require ID field to be long type
    private Long id;

    private String name;
    private String description;

    private double latitude = 0.0;
    private double longitude = 0.0;

    // real id, from server
    private String poiId;
    private String imageUrl;

    public PoiDb() {
    }

    public static PoiDb of(Poi poi) {
        PoiDb poiDb = new PoiDb();
        poiDb.setDescription(poi.getDescription());
        poiDb.setName(poi.getName());
        poiDb.setId((long) poi.getId().hashCode());
        poiDb.setImageUrl(poi.getImageUrl());
        poiDb.setLatitude(poi.getLocation().getLatitude());
        poiDb.setLongitude(poi.getLocation().getLongitude());

        poiDb.setPoiId(poi.getId());
        return poiDb;
    }

    public static List<PoiDb> of(List<Poi> poi) {
        ArrayList<PoiDb> poiDbs = new ArrayList<>();
        for (Poi poi1 : poi) {
            poiDbs.add(PoiDb.of(poi1));
        }
        return poiDbs;
    }

    public static Poi toPoi(PoiDb poiDb) {
        Poi poi = new Poi();
        poi.setId(poiDb.getPoiId());
        poi.setName(poiDb.getName());
        poi.setDescription(poiDb.getDescription());
        poi.setLocation(new Poi.PoiLocation(poiDb.getLatitude(), poiDb.getLongitude()));

        return poi;
    }

    public static List<Poi> toPoiList(List<PoiDb> poiDb) {
        ArrayList<Poi> poiDbs = new ArrayList<>();
        for (PoiDb poi1 : poiDb) {
            poiDbs.add(PoiDb.toPoi(poi1));
        }
        return poiDbs;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
