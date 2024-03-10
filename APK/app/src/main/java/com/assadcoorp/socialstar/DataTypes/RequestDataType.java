package com.assadcoorp.socialstar.DataTypes;

public class RequestDataType {
    String sentbyID,senttoID,requestID;
    long sentAt;

    public RequestDataType() {
    }

    public String getSentbyID() {
        return sentbyID;
    }

    public void setSentbyID(String sentbyID) {
        this.sentbyID = sentbyID;
    }

    public String getSenttoID() {
        return senttoID;
    }

    public void setSenttoID(String senttoID) {
        this.senttoID = senttoID;
    }



    public long getSentAt() {
        return sentAt;
    }

    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }
}
