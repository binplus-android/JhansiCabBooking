package com.cabbooking.Response;

import java.util.ArrayList;

public class ServiceLOcationResp {

    public int status;
    String error;
    public ArrayList<RecordList> recordList;

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

    public ArrayList<RecordList> getRecordList() {
        return recordList;
    }

    public void setRecordList(ArrayList<RecordList> recordList) {
        this.recordList = recordList;
    }

    public class RecordList{
        public String name;
        public String lat;
        public String lng;
        public String bound;

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

        public String getBound() {
            return bound;
        }

        public void setBound(String bound) {
            this.bound = bound;
        }
    }


}
