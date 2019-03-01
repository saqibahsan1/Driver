
package com.akhdmny.driver.ApiResponse.TransactionPojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("driver_id")
    @Expose
    private Integer driverId;
    @SerializedName("coupon_id")
    @Expose
    private Object couponId;
    @SerializedName("type")
    @Expose
    private Object type;
    @SerializedName("location_type")
    @Expose
    private Object locationType;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("final_amount")
    @Expose
    private Double finalAmount;
    @SerializedName("discount_amount")
    @Expose
    private Integer discountAmount;
    @SerializedName("discount_percent")
    @Expose
    private Integer discountPercent;
    @SerializedName("address")
    @Expose
    private Object address;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("long")
    @Expose
    private Double _long;
    @SerializedName("driver_accept_order_lat")
    @Expose
    private Double driverAcceptOrderLat;
    @SerializedName("driver_accept_order_long")
    @Expose
    private Double driverAcceptOrderLong;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("description")
    @Expose
    private Object description;
    @SerializedName("cancel_reason")
    @Expose
    private Object cancelReason;
    @SerializedName("bid")
    @Expose
    private Integer bid;
    @SerializedName("tip")
    @Expose
    private Integer tip;
    @SerializedName("is_bid")
    @Expose
    private Integer isBid;
    @SerializedName("client_note")
    @Expose
    private Object clientNote;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("remember_token")
    @Expose
    private Object rememberToken;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Object getCouponId() {
        return couponId;
    }

    public void setCouponId(Object couponId) {
        this.couponId = couponId;
    }

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public Object getLocationType() {
        return locationType;
    }

    public void setLocationType(Object locationType) {
        this.locationType = locationType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Object getAddress() {
        return address;
    }

    public void setAddress(Object address) {
        this.address = address;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLong() {
        return _long;
    }

    public void setLong(Double _long) {
        this._long = _long;
    }

    public Double getDriverAcceptOrderLat() {
        return driverAcceptOrderLat;
    }

    public void setDriverAcceptOrderLat(Double driverAcceptOrderLat) {
        this.driverAcceptOrderLat = driverAcceptOrderLat;
    }

    public Double getDriverAcceptOrderLong() {
        return driverAcceptOrderLong;
    }

    public void setDriverAcceptOrderLong(Double driverAcceptOrderLong) {
        this.driverAcceptOrderLong = driverAcceptOrderLong;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public Object getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(Object cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Integer getBid() {
        return bid;
    }

    public void setBid(Integer bid) {
        this.bid = bid;
    }

    public Integer getTip() {
        return tip;
    }

    public void setTip(Integer tip) {
        this.tip = tip;
    }

    public Integer getIsBid() {
        return isBid;
    }

    public void setIsBid(Integer isBid) {
        this.isBid = isBid;
    }

    public Object getClientNote() {
        return clientNote;
    }

    public void setClientNote(Object clientNote) {
        this.clientNote = clientNote;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Object getRememberToken() {
        return rememberToken;
    }

    public void setRememberToken(Object rememberToken) {
        this.rememberToken = rememberToken;
    }

}
