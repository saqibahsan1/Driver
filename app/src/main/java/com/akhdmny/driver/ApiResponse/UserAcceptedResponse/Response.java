
package com.akhdmny.driver.ApiResponse.UserAcceptedResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("orderDetails")
    @Expose
    private OrderDetails orderDetails;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("userInfo")
    @Expose
    private UserInfo userInfo;
    @SerializedName("driverId")
    @Expose
    private Object driverId;
    @SerializedName("driverInfo")
    @Expose
    private Object driverInfo;

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Object getDriverId() {
        return driverId;
    }

    public void setDriverId(Object driverId) {
        this.driverId = driverId;
    }

    public Object getDriverInfo() {
        return driverInfo;
    }

    public void setDriverInfo(Object driverInfo) {
        this.driverInfo = driverInfo;
    }

}
