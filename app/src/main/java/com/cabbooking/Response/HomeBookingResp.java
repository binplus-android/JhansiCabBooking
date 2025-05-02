package com.cabbooking.Response;

import java.util.ArrayList;

public class HomeBookingResp {
    public int status;
    public ArrayList<RecordList> recordList;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<RecordList> getRecordList() {
        return recordList;
    }

    public void setRecordList(ArrayList<RecordList> recordList) {
        this.recordList = recordList;
    }

    public class RecordList{
        public int tripId;
        public String name;
        public String profileImage;
        public String vehicleNumber;
        public String vehicleImage;
        public String vehicleModelName;

        public int getTripId() {
            return tripId;
        }

        public void setTripId(int tripId) {
            this.tripId = tripId;
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

        public String getVehicleNumber() {
            return vehicleNumber;
        }

        public void setVehicleNumber(String vehicleNumber) {
            this.vehicleNumber = vehicleNumber;
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
    }

}
