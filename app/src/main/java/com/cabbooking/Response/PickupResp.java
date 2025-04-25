package com.cabbooking.Response;

import java.util.ArrayList;
import java.util.Date;

public class PickupResp {
    public String message;
    public Data data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{
        public String userId;
        public String pickup;
        public double pickupLat;
        public double pickupLng;
        public String destination;
        public double destinationLat;
        public double destinationLng;
        public String isOutstation;
        public String isRound;
        public int vehicleType;
        public String distance;
        public int amount;
        public int tripStatus;
        public String  updated_at;
        public String  created_at;
        public int id;
        public ArrayList<Status> status;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getPickup() {
            return pickup;
        }

        public void setPickup(String pickup) {
            this.pickup = pickup;
        }

        public double getPickupLat() {
            return pickupLat;
        }

        public void setPickupLat(double pickupLat) {
            this.pickupLat = pickupLat;
        }

        public double getPickupLng() {
            return pickupLng;
        }

        public void setPickupLng(double pickupLng) {
            this.pickupLng = pickupLng;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public double getDestinationLat() {
            return destinationLat;
        }

        public void setDestinationLat(double destinationLat) {
            this.destinationLat = destinationLat;
        }

        public double getDestinationLng() {
            return destinationLng;
        }

        public void setDestinationLng(double destinationLng) {
            this.destinationLng = destinationLng;
        }

        public String getIsOutstation() {
            return isOutstation;
        }

        public void setIsOutstation(String isOutstation) {
            this.isOutstation = isOutstation;
        }

        public String getIsRound() {
            return isRound;
        }

        public void setIsRound(String isRound) {
            this.isRound = isRound;
        }

        public int getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(int vehicleType) {
            this.vehicleType = vehicleType;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public int getTripStatus() {
            return tripStatus;
        }

        public void setTripStatus(int tripStatus) {
            this.tripStatus = tripStatus;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public ArrayList<Status> getStatus() {
            return status;
        }

        public void setStatus(ArrayList<Status> status) {
            this.status = status;
        }

        public class Status{
            public String status;
            public String time;

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }
        }
    }
}

