package com.cabbooking.Response;

public class BookingDetailResp {
    public int status;
    public String message;
    String error;
    public RecordList recordList;



    public RecordList getRecordList() {
        return recordList;
    }

    public void setRecordList(RecordList recordList) {
        this.recordList = recordList;
    }

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
        public String returnDate;
        public int vehicleType;
        public String distance;
        public String amount;
        public int adminShare;
        public String pickupOtp;
        public int tripStatus;
        public String status;
        public String cancelledBy;
        public String cancelReason;
        public int isActive;
        public int isDelete;
        public String created_at;
        public String updated_at;
        public String name;
        public String contactNo;
        public String profileImage;
        public String vehicleImage;
        public String vehicleTypeImage;
        public String vehicleNumber;
        public String vehicleColor;
        public String vehicleModelName;
        public String tripStatusName;
        public String paymentMode;
        public String userFeedback;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getDriverId() {
            return driverId;
        }

        public void setDriverId(int driverId) {
            this.driverId = driverId;
        }

        public String getPickup() {
            return pickup;
        }

        public void setPickup(String pickup) {
            this.pickup = pickup;
        }

        public String getPickupLat() {
            return pickupLat;
        }

        public void setPickupLat(String pickupLat) {
            this.pickupLat = pickupLat;
        }

        public String getPickupLng() {
            return pickupLng;
        }

        public void setPickupLng(String pickupLng) {
            this.pickupLng = pickupLng;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public String getDestinationLat() {
            return destinationLat;
        }

        public void setDestinationLat(String destinationLat) {
            this.destinationLat = destinationLat;
        }

        public String getDestinationLng() {
            return destinationLng;
        }

        public void setDestinationLng(String destinationLng) {
            this.destinationLng = destinationLng;
        }

        public int getIsOutstation() {
            return isOutstation;
        }

        public void setIsOutstation(int isOutstation) {
            this.isOutstation = isOutstation;
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

        public void setReturnDate(String returnDate) {
            this.returnDate = returnDate;
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

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getPickupOtp() {
            return pickupOtp;
        }

        public void setPickupOtp(String pickupOtp) {
            this.pickupOtp = pickupOtp;
        }

        public int getTripStatus() {
            return tripStatus;
        }

        public int getAdminShare() {
            return adminShare;
        }

        public void setAdminShare(int adminShare) {
            this.adminShare = adminShare;
        }

        public String getCancelledBy() {
            return cancelledBy;
        }

        public void setCancelledBy(String cancelledBy) {
            this.cancelledBy = cancelledBy;
        }

        public String getCancelReason() {
            return cancelReason;
        }

        public void setCancelReason(String cancelReason) {
            this.cancelReason = cancelReason;
        }

        public void setVehicleTypeImage(String vehicleTypeImage) {
            this.vehicleTypeImage = vehicleTypeImage;
        }

        public String getTripStatusName() {
            return tripStatusName;
        }

        public void setTripStatusName(String tripStatusName) {
            this.tripStatusName = tripStatusName;
        }

        public void setTripStatus(int tripStatus) {
            this.tripStatus = tripStatus;
        }

        public String getVehicleTypeImage() {
            return vehicleTypeImage;
        }
        

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    


        public int getIsActive() {
            return isActive;
        }

        public void setIsActive(int isActive) {
            this.isActive = isActive;
        }

        public int getIsDelete() {
            return isDelete;
        }

        public void setIsDelete(int isDelete) {
            this.isDelete = isDelete;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContactNo() {
            return contactNo;
        }

        public void setContactNo(String contactNo) {
            this.contactNo = contactNo;
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

        public String getVehicleNumber() {
            return vehicleNumber;
        }

        public void setVehicleNumber(String vehicleNumber) {
            this.vehicleNumber = vehicleNumber;
        }

        public String getVehicleColor() {
            return vehicleColor;
        }

        public void setVehicleColor(String vehicleColor) {
            this.vehicleColor = vehicleColor;
        }

        public String getVehicleModelName() {
            return vehicleModelName;
        }

        public void setVehicleModelName(String vehicleModelName) {
            this.vehicleModelName = vehicleModelName;
        }

        public String getPaymentMode() {
            return paymentMode;
        }

        public void setPaymentMode(String paymentMode) {
            this.paymentMode = paymentMode;
        }

        public String getUserFeedback() {
            return userFeedback;
        }

        public void setUserFeedback(String userFeedback) {
            this.userFeedback = userFeedback;
        }
    }




    public BookingDetailResp() {
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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
