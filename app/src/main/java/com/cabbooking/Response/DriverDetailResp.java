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
        public String returnDate;
        public String name;
        public String profileImage;
        public String vehicleImage;
        public String vehicleTypeImage;
        public String vehicleColor;
        public String vehicleNumber;
        public String vehicleModelName;
        public int seats;

        public String getVehicleTypeImage() {
            return vehicleTypeImage;
        }

        public int getSeats() {
            return seats;
        }

        public String getVehicleNumber() {
            return vehicleNumber;
        }

        public void setVehicleTypeImage(String vehicleTypeImage) {
            this.vehicleTypeImage = vehicleTypeImage;
        }

        public void setReturnDate(String returnDate) {
            this.returnDate = returnDate;
        }

        public void setVehicleNumber(String vehicleNumber) {
            this.vehicleNumber = vehicleNumber;
        }

        public void setSeats(int seats) {
            this.seats = seats;
        }

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

        public String getReturnDate() {
            return returnDate;
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
