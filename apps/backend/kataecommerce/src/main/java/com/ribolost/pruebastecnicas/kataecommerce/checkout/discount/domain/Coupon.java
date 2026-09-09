package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.domain;

public class Coupon {

    private String code;
    private boolean active;
    private boolean used;

    public Coupon() {
    }

    public Coupon(String code, boolean active, boolean used) {
        this.code = code;
        this.active = active;
        this.used = used;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
