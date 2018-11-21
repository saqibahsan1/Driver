
package com.akhdmny.driver.ApiResponse.OrdersResponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderDetails {

    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("cartItems")
    @Expose
    private List<CartItem> cartItems = null;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("discount_percent")
    @Expose
    private Integer discountPercent;
    @SerializedName("discount_amount")
    @Expose
    private Integer discountAmount;
    @SerializedName("final_amount")
    @Expose
    private Double finalAmount;

    public Double getToLat() {
        return toLat;
    }

    public void setToLat(Double toLat) {
        this.toLat = toLat;
    }

    public Double getToLong() {
        return toLong;
    }

    public void setToLong(Double toLong) {
        this.toLong = toLong;
    }

    @SerializedName("lat")
    @Expose
    private Double toLat;
    @SerializedName("long")
    @Expose
    private Double toLong;


    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Double finalAmount) {
        this.finalAmount = finalAmount;
    }

}
