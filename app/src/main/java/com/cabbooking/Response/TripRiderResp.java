package com.cabbooking.Response;

public class TripRiderResp {
    public int status;
    public RecordList recordList;
    String  error;

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

    public RecordList getRecordList() {
        return recordList;
    }

    public void setRecordList(RecordList recordList) {
        this.recordList = recordList;
    }

    public class RecordList{
        public int tripId;
        public int tripStatus;

        public int getTripId() {
            return tripId;
        }

        public void setTripId(int tripId) {
            this.tripId = tripId;
        }

        public int getTripStatus() {
            return tripStatus;
        }

        public void setTripStatus(int tripStatus) {
            this.tripStatus = tripStatus;
        }
    }

}
