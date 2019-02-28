package com.akhdmny.driver.Singletons;

import com.akhdmny.driver.ApiResponse.AcceptModel.Driver;
import com.akhdmny.driver.ApiResponse.AcceptModel.Order;
import com.akhdmny.driver.ApiResponse.AcceptModel.User;
import com.akhdmny.driver.ApiResponse.MyOrderDetails.OrderDetail;

public class CurrentOrder {

    public static CurrentOrder shared;

    public Driver driver;
    public User user;
    public OrderDetail order;
    public int orderId;
    public int userId;
    public int driverId;
    public int finalAmount;


    public static CurrentOrder getInstance(){
        if (shared == null){ //if there is no instance available... create new one
            shared = new CurrentOrder();
        }

        return shared;
    }

}
