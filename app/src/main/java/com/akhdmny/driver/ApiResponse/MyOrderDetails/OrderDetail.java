package com.akhdmny.driver.ApiResponse.MyOrderDetails;


import com.akhdmny.driver.ApiResponse.UserAcceptedResponse.CartItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderDetail{

    @SerializedName("amount")
    @Expose
    private float amount;
    @SerializedName("cartItems")
    @Expose
    private CartItem[] cartItems;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("discount_amount")
    @Expose
    private int discountAmount;
    @SerializedName("discount_percent")
    @Expose
    private int discountPercent;
    @SerializedName("final_amount")
    @Expose
    private float finalAmount;
    @SerializedName("lat")
    @Expose
    private float lat;
    @SerializedName("long")
    @Expose
    private float longField;
    @SerializedName("order_id")
    @Expose
    private int orderId;
    @SerializedName("tip")
    @Expose
    private int tip;

    public void setAmount(float amount){
        this.amount = amount;
    }
    public float getAmount(){
        return this.amount;
    }
    public void setCartItems(CartItem[] cartItems){
        this.cartItems = cartItems;
    }
    public CartItem[] getCartItems(){
        return this.cartItems;
    }
    public void setCurrency(String currency){
        this.currency = currency;
    }
    public String getCurrency(){
        return this.currency;
    }
    public void setDiscountAmount(int discountAmount){
        this.discountAmount = discountAmount;
    }
    public int getDiscountAmount(){
        return this.discountAmount;
    }
    public void setDiscountPercent(int discountPercent){
        this.discountPercent = discountPercent;
    }
    public int getDiscountPercent(){
        return this.discountPercent;
    }
    public void setFinalAmount(float finalAmount){
        this.finalAmount = finalAmount;
    }
    public float getFinalAmount(){
        return this.finalAmount;
    }
    public void setLat(float lat){
        this.lat = lat;
    }
    public float getLat(){
        return this.lat;
    }
    public void setLongField(float longField){
        this.longField = longField;
    }
    public float getLongField(){
        return this.longField;
    }
    public void setOrderId(int orderId){
        this.orderId = orderId;
    }
    public int getOrderId(){
        return this.orderId;
    }
    public void setTip(int tip){
        this.tip = tip;
    }
    public int getTip(){
        return this.tip;
    }



}
