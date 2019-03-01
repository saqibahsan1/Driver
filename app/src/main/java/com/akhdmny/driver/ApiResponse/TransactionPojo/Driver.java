
package com.akhdmny.driver.ApiResponse.TransactionPojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Driver {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("car_color")
    @Expose
    private String carColor;
    @SerializedName("car_no")
    @Expose
    private String carNo;
    @SerializedName("car_model")
    @Expose
    private Integer carModel;
    @SerializedName("car_company")
    @Expose
    private String carCompany;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("bank")
    @Expose
    private Object bank;
    @SerializedName("account_no")
    @Expose
    private Object accountNo;
    @SerializedName("IsBusy")
    @Expose
    private Object isBusy;
    @SerializedName("balance")
    @Expose
    private String balance;
    @SerializedName("Notes")
    @Expose
    private Object notes;
    @SerializedName("NationalityID")
    @Expose
    private Object nationalityID;
    @SerializedName("DRI_National_URL")
    @Expose
    private Object dRINationalURL;
    @SerializedName("DRI_Car_URL")
    @Expose
    private Object dRICarURL;
    @SerializedName("DRI_License_URL")
    @Expose
    private Object dRILicenseURL;
    @SerializedName("AcceptPartnerOrder")
    @Expose
    private Object acceptPartnerOrder;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("long")
    @Expose
    private Double _long;
    @SerializedName("nationality")
    @Expose
    private Object nationality;
    @SerializedName("front_car_photo")
    @Expose
    private Object frontCarPhoto;
    @SerializedName("back_car_photo")
    @Expose
    private Object backCarPhoto;
    @SerializedName("id_card_photo")
    @Expose
    private Object idCardPhoto;
    @SerializedName("driver_license_photo")
    @Expose
    private Object driverLicensePhoto;
    @SerializedName("car_registration_photo")
    @Expose
    private Object carRegistrationPhoto;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public Integer getCarModel() {
        return carModel;
    }

    public void setCarModel(Integer carModel) {
        this.carModel = carModel;
    }

    public String getCarCompany() {
        return carCompany;
    }

    public void setCarCompany(String carCompany) {
        this.carCompany = carCompany;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Object getBank() {
        return bank;
    }

    public void setBank(Object bank) {
        this.bank = bank;
    }

    public Object getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(Object accountNo) {
        this.accountNo = accountNo;
    }

    public Object getIsBusy() {
        return isBusy;
    }

    public void setIsBusy(Object isBusy) {
        this.isBusy = isBusy;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public Object getNotes() {
        return notes;
    }

    public void setNotes(Object notes) {
        this.notes = notes;
    }

    public Object getNationalityID() {
        return nationalityID;
    }

    public void setNationalityID(Object nationalityID) {
        this.nationalityID = nationalityID;
    }

    public Object getDRINationalURL() {
        return dRINationalURL;
    }

    public void setDRINationalURL(Object dRINationalURL) {
        this.dRINationalURL = dRINationalURL;
    }

    public Object getDRICarURL() {
        return dRICarURL;
    }

    public void setDRICarURL(Object dRICarURL) {
        this.dRICarURL = dRICarURL;
    }

    public Object getDRILicenseURL() {
        return dRILicenseURL;
    }

    public void setDRILicenseURL(Object dRILicenseURL) {
        this.dRILicenseURL = dRILicenseURL;
    }

    public Object getAcceptPartnerOrder() {
        return acceptPartnerOrder;
    }

    public void setAcceptPartnerOrder(Object acceptPartnerOrder) {
        this.acceptPartnerOrder = acceptPartnerOrder;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLong() {
        return _long;
    }

    public void setLong(Double _long) {
        this._long = _long;
    }

    public Object getNationality() {
        return nationality;
    }

    public void setNationality(Object nationality) {
        this.nationality = nationality;
    }

    public Object getFrontCarPhoto() {
        return frontCarPhoto;
    }

    public void setFrontCarPhoto(Object frontCarPhoto) {
        this.frontCarPhoto = frontCarPhoto;
    }

    public Object getBackCarPhoto() {
        return backCarPhoto;
    }

    public void setBackCarPhoto(Object backCarPhoto) {
        this.backCarPhoto = backCarPhoto;
    }

    public Object getIdCardPhoto() {
        return idCardPhoto;
    }

    public void setIdCardPhoto(Object idCardPhoto) {
        this.idCardPhoto = idCardPhoto;
    }

    public Object getDriverLicensePhoto() {
        return driverLicensePhoto;
    }

    public void setDriverLicensePhoto(Object driverLicensePhoto) {
        this.driverLicensePhoto = driverLicensePhoto;
    }

    public Object getCarRegistrationPhoto() {
        return carRegistrationPhoto;
    }

    public void setCarRegistrationPhoto(Object carRegistrationPhoto) {
        this.carRegistrationPhoto = carRegistrationPhoto;
    }

}
