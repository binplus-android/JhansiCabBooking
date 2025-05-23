package com.cabbooking.Response;

public class ProfileDetailResp {
    public int status;
    public RecordList recordList;
    String error;

    public RecordList getRecordList() {
        return recordList;
    }

    public void setRecordList(RecordList recordList) {
        this.recordList = recordList;
    }

    public class RecordList{
        public String name;
        public String email;
        public String contactNo;
        public String profileImage;
        public int walletAmount;
        public String referralLink;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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

        public int getWalletAmount() {
            return walletAmount;
        }

        public void setWalletAmount(int walletAmount) {
            this.walletAmount = walletAmount;
        }

        public String getReferralLink() {
            return referralLink;
        }

        public void setReferralLink(String referralLink) {
            this.referralLink = referralLink;
        }
    }
    public ProfileDetailResp() {
    }

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
}
