package com.cabbooking.model;

import java.util.ArrayList;

public class BookingHistoryModel {
    public int status;
    public String message,error;
    public ArrayList<RecordList> recordList;
    public int totalCount;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<RecordList> getRecordList() {
        return recordList;
    }

    public void setRecordList(ArrayList<RecordList> recordList) {
        this.recordList = recordList;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public class RecordList{
        public int tripId;
        public String amount;
        public int tripStatus;
        public String tripStatusName;
        public String name;
        public String profileImage;
        public String vehicleImage;
        public String vehicleTypeImage;
        public String vehicleModelName;
        public String createdAt;

        public String getVehicleTypeImage() {
            return vehicleTypeImage;
        }

        public void setTripStatus(int tripStatus) {
            this.tripStatus = tripStatus;
        }

        public String getTripStatusName() {
            return tripStatusName;
        }

        public void setTripStatusName(String tripStatusName) {
            this.tripStatusName = tripStatusName;
        }

        public void setVehicleTypeImage(String vehicleTypeImage) {
            this.vehicleTypeImage = vehicleTypeImage;
        }

        public int getTripId() {
            return tripId;
        }

        public void setTripId(int tripId) {
            this.tripId = tripId;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }



        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getVehicleImage() {
            return vehicleImage;
        }

        public void setVehicleImage(String vehicleImage) {
            this.vehicleImage = vehicleImage;
        }

        public String getVehicleModelName() {
            return vehicleModelName;
        }

        public void setVehicleModelName(String vehicleModelName) {
            this.vehicleModelName = vehicleModelName;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
}
