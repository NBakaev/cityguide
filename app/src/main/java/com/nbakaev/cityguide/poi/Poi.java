package com.nbakaev.cityguide.poi;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Poi implements ClusterItem {

    private String name;
    private String description;
    private PoiLocation location;
    private String id;
    private String cityId;
    private Date lastUpdate;
    private float rating;

    private PoiContent content = new PoiContent();

    public PoiContent getContent() {
        return content;
    }

    public void setContent(PoiContent content) {
        this.content = content;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PoiLocation getLocation() {
        return location;
    }

    public void setLocation(PoiLocation location) {
        this.location = location;
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

    @Override
    public LatLng getPosition() {
        return new LatLng(getLocation().getLatitude(), getLocation().getLongitude());
    }

    static public class PoiContent {
        private String imageUrl;

        private List<String> imageUrls = new ArrayList<>();
        private String videoUrl;

        private String audioUrl;

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

        public String getAudioUrl() {
            return audioUrl;
        }

        public void setAudioUrl(String audioUrl) {
            this.audioUrl = audioUrl;
        }
    }

    static public class PoiLocation {

        public PoiLocation() {
        }

        public PoiLocation(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        private double latitude = 0.0;
        private double longitude = 0.0;

        public Location toLocation() {
            Location location = new Location("Server");
            location.setLatitude(this.latitude);
            location.setLongitude(this.longitude);
            return location;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PoiLocation that = (PoiLocation) o;

            if (Double.compare(that.latitude, latitude) != 0) return false;
            return Double.compare(that.longitude, longitude) == 0;

        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(latitude);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(longitude);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Poi poi = (Poi) o;

        if (Float.compare(poi.rating, rating) != 0) return false;
        if (name != null ? !name.equals(poi.name) : poi.name != null) return false;
        if (description != null ? !description.equals(poi.description) : poi.description != null) return false;
        if (location != null ? !location.equals(poi.location) : poi.location != null) return false;
        if (id != null ? !id.equals(poi.id) : poi.id != null) return false;
        if (cityId != null ? !cityId.equals(poi.cityId) : poi.cityId != null) return false;
        if (lastUpdate != null ? !lastUpdate.equals(poi.lastUpdate) : poi.lastUpdate != null) return false;
        return content != null ? content.equals(poi.content) : poi.content == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (cityId != null ? cityId.hashCode() : 0);
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        result = 31 * result + (rating != +0.0f ? Float.floatToIntBits(rating) : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }
}
