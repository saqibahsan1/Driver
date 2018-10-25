
package com.akhdmny.driver.ApiResponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderConfirmationResponse {

    @SerializedName("driver")
    @Expose
    private OrderConfirmation_Driver driver;
    @SerializedName("services")
    @Expose
    private List<OrderConfirmationServices> services = null;

    public OrderConfirmation_Driver getDriver() {
        return driver;
    }

    public void setDriver(OrderConfirmation_Driver driver) {
        this.driver = driver;
    }

    public List<OrderConfirmationServices> getServices() {
        return services;
    }

    public void setServices(List<OrderConfirmationServices> services) {
        this.services = services;
    }

}
