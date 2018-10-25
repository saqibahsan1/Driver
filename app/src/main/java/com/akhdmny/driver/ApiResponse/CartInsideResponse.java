
package com.akhdmny.driver.ApiResponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CartInsideResponse {

    @SerializedName("cartitems")
    @Expose
    private List<Cartitem> cartitems = null;
    @SerializedName("discount")
    @Expose
    private Object discount;
    @SerializedName("service_amount")
    @Expose
    private Integer serviceAmount;
    @SerializedName("final_amount")
    @Expose
    private Integer finalAmount;

    public List<Cartitem> getCartitems() {
        return cartitems;
    }

    public void setCartitems(List<Cartitem> cartitems) {
        this.cartitems = cartitems;
    }

    public Object getDiscount() {
        return discount;
    }

    public void setDiscount(Object discount) {
        this.discount = discount;
    }

    public Integer getServiceAmount() {
        return serviceAmount;
    }

    public void setServiceAmount(Integer serviceAmount) {
        this.serviceAmount = serviceAmount;
    }

    public Integer getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Integer finalAmount) {
        this.finalAmount = finalAmount;
    }

}
