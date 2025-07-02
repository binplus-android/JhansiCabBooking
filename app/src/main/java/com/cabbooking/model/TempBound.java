package com.cabbooking.model;




public class TempBound {
    public TempBound(Double nlat, Double nlon, Double slat, Double slng) {
        Nlat = nlat;
        Nlon = nlon;
        Slat = slat;
        Slng = slng;
    }

    Double Nlat,Nlon,Slat,Slng;
    
    

    public Double getNlat() {
        return Nlat;
    }

    public void setNlat(Double nlat) {
        Nlat = nlat;
    }

    public Double getNlon() {
        return Nlon;
    }

    public void setNlon(Double nlon) {
        Nlon = nlon;
    }

    public Double getSlat() {
        return Slat;
    }

    public void setSlat(Double slat) {
        Slat = slat;
    }

    public Double getSlng() {
        return Slng;
    }

    public void setSlng(Double slng) {
        Slng = slng;
    }
}
