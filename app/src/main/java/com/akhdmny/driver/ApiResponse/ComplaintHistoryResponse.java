
package com.akhdmny.driver.ApiResponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ComplaintHistoryResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("response")
    @Expose
    private List<ComplaintHistoryInsideResponse> response = null;
    @SerializedName("error")
    @Expose
    private Object error;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<ComplaintHistoryInsideResponse> getResponse() {
        return response;
    }

    public void setResponse(List<ComplaintHistoryInsideResponse> response) {
        this.response = response;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

}
