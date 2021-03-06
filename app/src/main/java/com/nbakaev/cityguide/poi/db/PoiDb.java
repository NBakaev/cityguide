package com.nbakaev.cityguide.poi.db;

import com.nbakaev.cityguide.poi.Poi;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class PoiDb {

    @Id(autoincrement = false)
    private Long id;

    private String cityId;
    @Convert(converter = DateGreenConverter.class, columnType = Long.class)
    private Date lastUpdate;

    private String name;

    private String description;

    private double latitude = 0.0;
    private double longitude = 0.0;

    @Index(unique = true)
    private String poiId;

    private String imageUrl;

    @Convert(converter = GreenConverter.class, columnType = String.class)
    private List<String> imageUrls = new ArrayList<>();
    private String videoUrl;

    private float rating;

    public PoiDb() {
    }


    public PoiDb(Long id, String cityId, Date lastUpdate, String name, String description, String descriptionHtml,
            double latitude, double longitude, String poiId, String imageUrl, List<String> imageUrls,
            String videoUrl, float rating) {
        this.id = id;
        this.cityId = cityId;
        this.lastUpdate = lastUpdate;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.poiId = poiId;
        this.imageUrl = imageUrl;
        this.imageUrls = imageUrls;
        this.videoUrl = videoUrl;
        this.rating = rating;
    }


    @Generated(hash = 614923651)
    public PoiDb(Long id, String cityId, Date lastUpdate, String name, String description, double latitude,
            double longitude, String poiId, String imageUrl, List<String> imageUrls, String videoUrl,
            float rating) {
        this.id = id;
        this.cityId = cityId;
        this.lastUpdate = lastUpdate;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.poiId = poiId;
        this.imageUrl = imageUrl;
        this.imageUrls = imageUrls;
        this.videoUrl = videoUrl;
        this.rating = rating;
    }


    public static PoiDb of(Poi poi) {
        PoiDb poiDb = new PoiDb();
        poiDb.setId((long) poi.getId().hashCode());
        poiDb.setDescription(poi.getDescription());
        poiDb.setName(poi.getName());
        poiDb.setImageUrl(poi.getContent().getImageUrl());
        poiDb.setLatitude(poi.getLocation().getLatitude());
        poiDb.setLongitude(poi.getLocation().getLongitude());
        poiDb.setVideoUrl(poi.getContent().getVideoUrl());
        poiDb.setImageUrls(poi.getContent().getImageUrls());
        poiDb.setCityId(poi.getCityId());
        poiDb.setLastUpdate(poi.getLastUpdate());
        poiDb.setPoiId(poi.getId());
        poiDb.setRating(poi.getRating());
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
        poi.getContent().setImageUrl(poiDb.getImageUrl());
        poi.getContent().setVideoUrl(poiDb.getVideoUrl());
        poi.getContent().setImageUrls(poiDb.getImageUrls());
        poi.setLastUpdate(poiDb.getLastUpdate());
        poi.setCityId(poiDb.getCityId());
        poi.setRating(poiDb.getRating());

        return poi;
    }

    public static List<Poi> toPoiList(List<PoiDb> poiDb) {
        ArrayList<Poi> poiDbs = new ArrayList<>();
        for (PoiDb poi1 : poiDb) {
            poiDbs.add(PoiDb.toPoi(poi1));
        }
        return poiDbs;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
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
}
