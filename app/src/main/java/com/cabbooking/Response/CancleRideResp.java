package com.cabbooking.Response;

import java.util.ArrayList;

public class CancleRideResp {
    public int status;
    public String message,error;
    public RecordList recordList;

    public class RecordList{
        public int id;
        public int userId;
        public int driverId;
        public String pickup;
        public String pickupLat;
        public String pickupLng;
        public String destination;
        public String destinationLat;
        public String destinationLng;
        public int isOutstation;
        public int isRound;
        public int vehicleType;
        public String distance;
        public String amount;
        public int tripStatus;
        public ArrayList<Status> status;
        public int isActive;
        public int isDelete;
        public String created_at;
        public String updated_at;

        public class Status{
            public String status;
            public String time;
        }
    }

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

    public RecordList getRecordList() {
        return recordList;
    }

    public void setRecordList(RecordList recordList) {
        this.recordList = recordList;
    }
}
