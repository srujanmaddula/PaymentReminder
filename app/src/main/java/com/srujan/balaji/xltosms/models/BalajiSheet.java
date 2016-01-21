package com.srujan.balaji.xltosms.models;

/**
 * Created by mobility on 31/12/15.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BalajiSheet {

    @SerializedName("account")
    @Expose
    private String account;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("debits")
    @Expose
    private String debits;
    @SerializedName("credits")
    @Expose
    private String credits;
    @SerializedName("email")
    @Expose
    private String email;

    /**
     *
     * @return
     * The account
     */
    public String getAccount() {
        return account;
    }

    /**
     *
     * @param account
     * The account
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     *
     * @return
     * The phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     *
     * @param phone
     * The phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     *
     * @return
     * The debits
     */
    public String getDebits() {
        return debits;
    }

    /**
     *
     * @param debits
     * The debits
     */
    public void setDebits(String debits) {
        this.debits = debits;
    }

    /**
     *
     * @return
     * The credits
     */
    public String getCredits() {
        return credits;
    }

    /**
     *
     * @param credits
     * The credits
     */
    public void setCredits(String credits) {
        this.credits = credits;
    }

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

}