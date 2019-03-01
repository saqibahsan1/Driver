
package com.akhdmny.driver.ApiResponse.TransactionPojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reward_ {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("from_distance_km")
    @Expose
    private Integer fromDistanceKm;
    @SerializedName("to_distance_km")
    @Expose
    private Integer toDistanceKm;
    @SerializedName("amount_in_percent")
    @Expose
    private Integer amountInPercent;
    @SerializedName("avail_limit")
    @Expose
    private Integer availLimit;
    @SerializedName("availed")
    @Expose
    private Integer availed;
    @SerializedName("valid_from")
    @Expose
    private String validFrom;
    @SerializedName("valid_to")
    @Expose
    private String validTo;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getFromDistanceKm() {
        return fromDistanceKm;
    }

    public void setFromDistanceKm(Integer fromDistanceKm) {
        this.fromDistanceKm = fromDistanceKm;
    }

    public Integer getToDistanceKm() {
        return toDistanceKm;
    }

    public void setToDistanceKm(Integer toDistanceKm) {
        this.toDistanceKm = toDistanceKm;
    }

    public Integer getAmountInPercent() {
        return amountInPercent;
    }

    public void setAmountInPercent(Integer amountInPercent) {
        this.amountInPercent = amountInPercent;
    }

    public Integer getAvailLimit() {
        return availLimit;
    }

    public void setAvailLimit(Integer availLimit) {
        this.availLimit = availLimit;
    }

    public Integer getAvailed() {
        return availed;
    }

    public void setAvailed(Integer availed) {
        this.availed = availed;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

}
