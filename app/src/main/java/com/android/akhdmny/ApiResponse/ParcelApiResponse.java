
package com.android.akhdmny.ApiResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParcelApiResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("response")
    @Expose
    private ParcelInsideResponse response;
    @SerializedName("error")
    @Expose
    private Object error;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public ParcelInsideResponse getResponse() {
        return response;
    }

    public void setResponse(ParcelInsideResponse response) {
        this.response = response;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

}
