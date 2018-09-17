package com.openarc.mytrack.model;

/**
 * Created by dinuka on 1/1/2017.
 */

public class Birthday {
    private String NAME, BDAY,EEENO,MOBILE,EMAIL;


    public Birthday() {
    }

    public Birthday(String NAME, String BDAY,String EEENO,String MOBILE,String EMAIL) {
        this.NAME = NAME;
        this.BDAY = BDAY;
        this.EEENO = EEENO;
        this.MOBILE = MOBILE;
        this.EMAIL = EMAIL;

    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getBDAY() {
        return BDAY;
    }

    public void setBDAY(String BDAY) {
        this.BDAY = BDAY;
    }

    public String getEEENO() {
        return EEENO;
    }

    public void setEEENO(String EEENO) {
        this.EEENO = EEENO;
    }

    public String getMOBILE() {
        return MOBILE;
    }

    public void setMOBILE(String MOBILE) {
        this.MOBILE = MOBILE;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }
}
