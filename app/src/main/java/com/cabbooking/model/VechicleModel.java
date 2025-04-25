package com.cabbooking.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class VechicleModel {
    public int status;
    String error;
    public ArrayList<RecordList> recordList;

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
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

    public static class RecordList implements Parcelable {
        public int id;
        public String name;
        public String icon;
        public String description;
        public int fare;

        public RecordList() {}

        protected RecordList(Parcel in) {
            id = in.readInt();
            name = in.readString();
            icon = in.readString();
            description = in.readString();
            fare = in.readInt();
        }

        public static final Creator<RecordList> CREATOR = new Creator<RecordList>() {
            @Override
            public RecordList createFromParcel(Parcel in) {
                return new RecordList(in);
            }

            @Override
            public RecordList[] newArray(int size) {
                return new RecordList[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(name);
            dest.writeString(icon);
            dest.writeString(description);
            dest.writeInt(fare);
        }

        @Override
        public int describeContents() {
            return 0;
        }



        // Your existing getters and setters below

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public int getFare() { return fare; }
        public void setFare(int fare) { this.fare = fare; }
    }

}
