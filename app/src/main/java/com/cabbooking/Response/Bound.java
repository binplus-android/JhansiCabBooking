package com.cabbooking.Response;

public class Bound {
        public String name;
        public String lat;
        public String lng;
        public LatLng ne;
        public LatLng sw;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public LatLng getNe() {
        return ne;
    }

    public void setNe(LatLng ne) {
        this.ne = ne;
    }

    public LatLng getSw() {
        return sw;
    }

    public void setSw(LatLng sw) {
        this.sw = sw;
    }

    public static class LatLng {
            public double lat;
            public double lng;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }


}
