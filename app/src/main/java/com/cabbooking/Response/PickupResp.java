package com.cabbooking.Response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class PickupResp implements Parcelable {
    public int status;
    public String message, error;
    public RecordList recordList;

    public PickupResp() {}

    protected PickupResp(Parcel in) {
        status = in.readInt();
        message = in.readString();
        error = in.readString();
        recordList = in.readParcelable(RecordList.class.getClassLoader());
    }

    public static final Creator<PickupResp> CREATOR = new Creator<PickupResp>() {
        @Override
        public PickupResp createFromParcel(Parcel in) {
            return new PickupResp(in);
        }

        @Override
        public PickupResp[] newArray(int size) {
            return new PickupResp[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeString(message);
        dest.writeString(error);
        dest.writeParcelable(recordList, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and setters

    public RecordList getRecordList() { return recordList; }
    public void setRecordList(RecordList recordList) { this.recordList = recordList; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    // Static inner class RecordList
    public static class RecordList implements Parcelable {
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
        public Double amount;
        public int tripStatus;
        public String upStringd_at;
        public String created_at;
        public int id;
        public ArrayList<Status> status;

        public RecordList() {}

        protected RecordList(Parcel in) {
            userId = in.readString();
            pickup = in.readString();
            pickupLat = in.readDouble();
            pickupLng = in.readDouble();
            destination = in.readString();
            destinationLat = in.readDouble();
            destinationLng = in.readDouble();
            isOutstation = in.readString();
            isRound = in.readString();
            vehicleType = in.readInt();
            distance = in.readString();
            amount = in.readDouble();
            tripStatus = in.readInt();
            upStringd_at = in.readString();
            created_at = in.readString();
            id = in.readInt();
            status = in.createTypedArrayList(Status.CREATOR);
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
            dest.writeString(userId);
            dest.writeString(pickup);
            dest.writeDouble(pickupLat);
            dest.writeDouble(pickupLng);
            dest.writeString(destination);
            dest.writeDouble(destinationLat);
            dest.writeDouble(destinationLng);
            dest.writeString(isOutstation);
            dest.writeString(isRound);
            dest.writeInt(vehicleType);
            dest.writeString(distance);
            dest.writeDouble(amount);
            dest.writeInt(tripStatus);
            dest.writeString(upStringd_at);
            dest.writeString(created_at);
            dest.writeInt(id);
            dest.writeTypedList(status);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        // Getters and setters...


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

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public int getTripStatus() {
            return tripStatus;
        }

        public void setTripStatus(int tripStatus) {
            this.tripStatus = tripStatus;
        }

        public String getUpStringd_at() {
            return upStringd_at;
        }

        public void setUpStringd_at(String upStringd_at) {
            this.upStringd_at = upStringd_at;
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
    }

    // Static inner class Status
    public static class Status implements Parcelable {
        public String status;
        public String time;

        public Status() {}

        protected Status(Parcel in) {
            status = in.readString();
            time = in.readString();
        }

        public static final Creator<Status> CREATOR = new Creator<Status>() {
            @Override
            public Status createFromParcel(Parcel in) {
                return new Status(in);
            }

            @Override
            public Status[] newArray(int size) {
                return new Status[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(status);
            dest.writeString(time);
        }

        @Override
        public int describeContents() {
            return 0;
        }


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
