package ru.nbakaev.cityguide.poi.db;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.ArrayList;
import java.util.List;

import ru.nbakaev.cityguide.poi.Poi;

@Entity
public class PoiDb {

    @Id(autoincrement = false)
    private Long id;

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

    public PoiDb() {
    }



    @Generated(hash = 75642946)
    public PoiDb(Long id, String name, String description, double latitude, double longitude,
            String poiId, String imageUrl, List<String> imageUrls, String videoUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.poiId = poiId;
        this.imageUrl = imageUrl;
        this.imageUrls = imageUrls;
        this.videoUrl = videoUrl;
    }



    public static PoiDb of(Poi poi) {
        PoiDb poiDb = new PoiDb();
        poiDb.setId((long) poi.getId().hashCode());
        poiDb.setDescription(poi.getDescription());
        poiDb.setName(poi.getName());
        poiDb.setImageUrl(poi.getImageUrl());
        poiDb.setLatitude(poi.getLocation().getLatitude());
        poiDb.setLongitude(poi.getLocation().getLongitude());
        poiDb.setVideoUrl(poi.getVideoUrl());
        poiDb.setImageUrls(poi.getImageUrls());

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
        poi.setImageUrl(poiDb.getImageUrl());
        poi.setVideoUrl(poiDb.getVideoUrl());
        poi.setImageUrls(poiDb.getImageUrls());

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
}
