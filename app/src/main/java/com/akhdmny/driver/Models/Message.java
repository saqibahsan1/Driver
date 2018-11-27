package com.akhdmny.driver.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("isDelivered")
    @Expose
    private Boolean isDelivered;
    @SerializedName("isRead")
    @Expose
    private Boolean isRead;
    @SerializedName("senderId")
    @Expose
    private String senderId;
    @SerializedName("senderImage")
    @Expose
    private String senderImage;
    @SerializedName("senderName")
    @Expose
    private String senderName;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("type")
    @Expose
    private int type;


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getDelivered() {
        return isDelivered;
    }

    public void setDelivered(Boolean delivered) {
        isDelivered = delivered;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
