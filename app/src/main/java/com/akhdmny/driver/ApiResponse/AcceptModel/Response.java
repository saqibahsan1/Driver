
package com.akhdmny.driver.ApiResponse.AcceptModel;

import com.akhdmny.driver.ApiResponse.MyOrderDetails.OrderDetail;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("driverId")
    private int driverId;
    @SerializedName("driverInfo")
    private Driver driver;
    @SerializedName("orderDetails")
    private OrderDetail order;
    @SerializedName("userId")
    private int userId;
    @SerializedName("userInfo")
    private User user;

    public void setDriverId(int driverId){
        this.driverId = driverId;
    }
    public int getDriverId(){
        return this.driverId;
    }
    public void setDriver(Driver driver){
        this.driver = driver;
    }
    public Driver getDriver(){
        return this.driver;
    }
    public void setOrder(OrderDetail order){
        this.order = order;
    }
    public OrderDetail getOrder(){
        return this.order;
    }
    public void setUserId(int userId){
        this.userId = userId;
    }
    public int getUserId(){
        return this.userId;
    }
    public void setUser(User user){
        this.user = user;
    }
    public User getUser(){
        return this.user;
    }

}
