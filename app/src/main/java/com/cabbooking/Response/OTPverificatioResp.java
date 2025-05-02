package com.cabbooking.Response;

public class OTPverificatioResp {
    public String message;
    public String error;
    public String token;
    public String token_type;
    public int status;
    public RecordList recordList;

    public OTPverificatioResp() {
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
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
        public int id;
        public String own_refer_code,profileImage;
        public int isActive;

        public RecordList() {
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getOwn_refer_code() {
            return own_refer_code;
        }

        public void setOwn_refer_code(String own_refer_code) {
            this.own_refer_code = own_refer_code;
        }

        public int getIsActive() {
            return isActive;
        }

        public void setIsActive(int isActive) {
            this.isActive = isActive;
        }
    }

}
