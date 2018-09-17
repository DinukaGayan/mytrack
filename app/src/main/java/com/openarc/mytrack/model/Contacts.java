package com.openarc.mytrack.model;

/**
 * Created by dinuka on 12/13/2016.
 */

public class Contacts {
    private String NAME,EMAIL, MOBILE,EEENO,EXTENSION;


    public Contacts() {
    }

    public Contacts(String NAME, String EMAIL, String MOBILE , String EEENO, String EXTENSION) {
        this.NAME = NAME;
        this.EMAIL = EMAIL;
        this.MOBILE = MOBILE;
        this.EEENO = EEENO;
        this.EXTENSION = EXTENSION;

    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getMOBILE() {
        return MOBILE;
    }

    public void setMOBILE(String MOBILE) {
        this.MOBILE = MOBILE;
    }

    public String getEEENO() {
        return EEENO;
    }

    public void setEEENO(String EEENO) {
        this.EEENO = EEENO;
    }

    public String getEXTENSION() {
        return EXTENSION;
    }

    public void setEXTENSION(String EXTENSION) {
        this.EXTENSION = EXTENSION;
    }
}