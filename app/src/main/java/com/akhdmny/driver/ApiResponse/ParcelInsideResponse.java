
package com.akhdmny.driver.ApiResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParcelInsideResponse {

    @SerializedName("to_lat")
    @Expose
    private String toLat;
    @SerializedName("to_long")
    @Expose
    private String toLong;
    @SerializedName("from_lat")
    @Expose
    private String fromLat;
    @SerializedName("from_long")
    @Expose
    private String fromLong;

    public String getToLat() {
        return toLat;
    }

    public void setToLat(String toLat) {
        this.toLat = toLat;
    }

    public String getToLong() {
        return toLong;
    }

    public void setToLong(String toLong) {
        this.toLong = toLong;
    }

    public String getFromLat() {
        return fromLat;
    }

    public void setFromLat(String fromLat) {
        this.fromLat = fromLat;
    }

    public String getFromLong() {
        return fromLong;
    }

    public void setFromLong(String fromLong) {
        this.fromLong = fromLong;
    }

}
