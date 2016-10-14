package ru.nbakaev.cityguide.poi;

import android.location.Location;

public class Poi {

    private byte[] image;
    private String name;
    private String description;
    private PoiLocation location;
    private String id;


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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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


    static public class PoiLocation {

        public PoiLocation() {
        }

        public PoiLocation(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        private double latitude = 0.0;
        private double longitude = 0.0;

        public Location toLocation(){
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

}
