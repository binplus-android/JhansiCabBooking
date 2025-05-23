package com.cabbooking.model;

public class PickupAdressModel {
    String name ,formatted_address;
    double lat;
    double lng;

    public PickupAdressModel(String name, double lat, double lng, String formatted_address) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.formatted_address = formatted_address;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
