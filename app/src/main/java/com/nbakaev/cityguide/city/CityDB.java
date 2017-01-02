package com.nbakaev.cityguide.city;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nbakaev.cityguide.poi.Poi;
import com.nbakaev.cityguide.poi.db.DateGreenConverter;
import com.nbakaev.cityguide.poi.db.GreenConverter;

/**
 * Created by Наташа on 22.12.2016.
 */
@Entity
public class CityDB {
    @Id(autoincrement = false)
    private Long id;

    private String name;
    @Index(unique = true)
    private String cityId;

    @Convert(converter = DateGreenConverter.class, columnType = Long.class)

    private Date lastUpdate;
    private int pois = 0;
    private String imageUrl;
    private String description;

    @Convert(converter = GreenConverter.class, columnType = String.class)

    private List<String> imageUrls = new ArrayList<>();
    private String videoUrl;
    private double approximateRadius = 0;
    private double latitude = 0.0;
    private double longitude = 0.0;

    public CityDB() {

    }

    @Generated(hash = 1809858800)
    public CityDB(Long id, String name, String cityId, Date lastUpdate, int pois, String imageUrl, String description, List<String> imageUrls, String videoUrl, double approximateRadius, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.cityId = cityId;
        this.lastUpdate = lastUpdate;
        this.pois = pois;
        this.imageUrl = imageUrl;
        this.description = description;
        this.imageUrls = imageUrls;
        this.videoUrl = videoUrl;
        this.approximateRadius = approximateRadius;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static CityDB of(City city) {
        CityDB cityDB = new CityDB();
        cityDB.setId((long) city.getId().hashCode());
        cityDB.setPois(city.getPois());
        cityDB.setName(city.getName());
        cityDB.setCityId(city.getId());
        cityDB.setDescription(city.getDescription());
        cityDB.setImageUrl(city.getImageUrl());
        cityDB.setLastUpdate(city.getLastUpdate());
        cityDB.setLatitude(city.getLocation().getLatitude());
        cityDB.setLongitude(city.getLocation().getLongitude());
        cityDB.setVideoUrl(city.getVideoUrl());
        cityDB.setImageUrls(city.getImageUrls());
        return cityDB;
    }

    public static List<CityDB> of(List<City> cities) {
        ArrayList<CityDB> cityDBs = new ArrayList<>();
        for (City city : cities) {
            cityDBs.add(CityDB.of(city));
        }
        return cityDBs;
    }

    public static City toCity(CityDB cityDB) {
        City city = new City();
        city.setPois(cityDB.getPois());
        city.setName(cityDB.getName());
        city.setId(cityDB.getCityId());
        city.setDescription(cityDB.getDescription());
        city.setImageUrl(cityDB.getImageUrl());
        city.setLastUpdate(cityDB.getLastUpdate());
        city.setLocation(new Poi.PoiLocation(cityDB.getLatitude(), cityDB.getLongitude()));
        city.setVideoUrl(cityDB.getVideoUrl());
        city.setImageUrls(cityDB.getImageUrls());
        return city;
    }

    public static List<City> toCityList(List<CityDB> cityDBs) {
        ArrayList<City> cities = new ArrayList<>();
        for (CityDB cityDB : cityDBs) {
            cities.add(CityDB.toCity(cityDB));
        }
        return cities;
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

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

