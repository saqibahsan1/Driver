package com.akhdmny.driver.Requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerificationReguest {
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("code")
    @Expose
    private Integer code;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
