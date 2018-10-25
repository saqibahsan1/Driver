package com.akhdmny.driver.Utils;



public class Show_Chat_Conversation_Data_Items {
    private String body;
    private String sender;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsDelivered() {
        return isDelivered;
    }

    public void setIsDelivered(String isDelivered) {
        this.isDelivered = isDelivered;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Show_Chat_Conversation_Data_Items(String body, String sender, String id, String isDelivered,
                                             String isRead, String senderId, String senderImage,
                                             String senderName, String time, String type) {
        this.body = body;
        this.sender = sender;
        this.id = id;
        this.isDelivered = isDelivered;
        this.isRead = isRead;
        this.senderId =senderId;
        this.senderImage = senderImage;
        this.senderName= senderName;
        this.time = time;
        this.type = type;
    }

    private String id;
    private String isDelivered;
    private String isRead;
    private String senderId;
    private String senderImage;
    private String senderName;
    private String time;
    private String type;

    public Show_Chat_Conversation_Data_Items()
    {
    }



    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
