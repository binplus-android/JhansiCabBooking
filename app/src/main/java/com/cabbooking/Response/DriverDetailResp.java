package com.cabbooking.Response;

public class DriverDetailResp {
    public int status;
    String error;
    public RecordList recordList;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public RecordList getRecordList() {
        return recordList;
    }

    public void setRecordList(RecordList recordList) {
        this.recordList = recordList;
    }

    public class RecordList{
        public String amount;
        public int isOutStation;
        public int isRound;
        public Object returnDate;
        public String name;
        public String profileImage;
        public String vehicleImage;
        public String vehicleModelName;
        public String vehicleColor;
        public String seat;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public int getIsOutStation() {
            return isOutStation;
        }

        public void setIsOutStation(int isOutStation) {
            this.isOutStation = isOutStation;
        }

        public int getIsRound() {
            return isRound;
        }

        public void setIsRound(int isRound) {
            this.isRound = isRound;
        }

        public Object getReturnDate() {
            return returnDate;
        }

        public void setReturnDate(Object returnDate) {
            this.returnDate = returnDate;
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

        public String getseat() {
            return seat;
        }

        public void setseat(String seat) {
            this.seat = seat;
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

        public String getVehicleColor() {
            return vehicleColor;
        }

        public void setVehicleColor(String vehicleColor) {
            this.vehicleColor = vehicleColor;
        }
    }


}
