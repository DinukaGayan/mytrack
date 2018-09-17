package com.openarc.mytrack.model;

/**
 * Created by dinuka on 12/26/2016.
 */

public class Leaves {
    String Code,Descr,Entitled,Utilized,Available;

    public Leaves() {
    }

    public Leaves(String CODE, String DESCR, String ENTITLED, String UTILIZED, String AVAILABLE) {
        this.Code = CODE;
        this.Descr = DESCR;
        this.Entitled = ENTITLED;
        this.Utilized = UTILIZED;
        this.Available = AVAILABLE;

    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getDescr() {
        return Descr;
    }

    public void setDescr(String descr) {
        Descr = descr;
    }

    public String getEntitled() {
        return Entitled;
    }

    public void setEntitled(String entitled) {
        Entitled = entitled;
    }

    public String getUtilized() {
        return Utilized;
    }

    public void setUtilized(String utilized) {
        Utilized = utilized;
    }

    public String getAvailable() {
        return Available;
    }

    public void setAvailable(String available) {
        Available = available;
    }
}
