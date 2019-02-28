
package com.akhdmny.driver.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CancelReasonModel {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Code")
    @Expose
    private Integer code;
    @SerializedName("Result")
    @Expose
    private Result result;
    @SerializedName("UserBlocked")
    @Expose
    private Integer userBlocked;
    @SerializedName("pages")
    @Expose
    private Integer pages;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Integer getUserBlocked() {
        return userBlocked;
    }

    public void setUserBlocked(Integer userBlocked) {
        this.userBlocked = userBlocked;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

}
