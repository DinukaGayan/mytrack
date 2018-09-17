package com.openarc.mytrack.model;

/**
 * Created by dinuka on 12/13/2016.
 */

import java.util.ArrayList;

public class Attendance {
    private String DATE, INTIME,OUTTIME,DAY,REMARK,ISHOLIDAY,HOLIDAYDESC,APPRSTAT,ISLEAVE,LEAVEDESC;


    public Attendance() {
    }

    public Attendance(String DATE, String INTIME,String OUTTIME ,String DAY,String REMARK,String ISHOLIDAY,
                      String HOLIDAYDESC,String APPRSTAT,String ISLEAVE,String LEAVEDESC) {
        this.DATE = DATE;
        this.INTIME = INTIME;
        this.OUTTIME = OUTTIME;
        this.DAY = DAY;
        this.REMARK = REMARK;
        this.ISHOLIDAY = ISHOLIDAY;
        this.HOLIDAYDESC = HOLIDAYDESC;
        this.APPRSTAT = APPRSTAT;
        this.ISLEAVE = ISLEAVE;
        this.LEAVEDESC = LEAVEDESC;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public String getINTIME() {
        return INTIME;
    }

    public void setINTIME(String INTIME) {
        this.INTIME = INTIME;
    }

    public String getOUTTIME() {
        return OUTTIME;
    }

    public void setOUTTIME(String OUTTIME) {
        this.OUTTIME = OUTTIME;
    }

    public String getDAY() {
        return DAY;
    }

    public void setDAY(String DAY) {
        this.DAY = DAY;
    }

    public String getREMARK() {
        return REMARK;
    }

    public void setREMARK(String REMARK) {
        this.REMARK = REMARK;
    }

    public String getISHOLIDAY() {
        return ISHOLIDAY;
    }

    public void setISHOLIDAY(String ISHOLIDAY) {
        this.ISHOLIDAY = ISHOLIDAY;
    }

    public String getHOLIDAYDESC() {
        return HOLIDAYDESC;
    }

    public void setHOLIDAYDESC(String HOLIDAYDESC) {
        this.HOLIDAYDESC = HOLIDAYDESC;
    }

    public String getAPPRSTAT() {
        return APPRSTAT;
    }

    public void setAPPRSTAT(String APPRSTAT) {
        this.APPRSTAT = APPRSTAT;
    }

    public String getISLEAVE() {
        return ISLEAVE;
    }

    public void setISLEAVE(String ISLEAVE) {
        this.ISLEAVE = ISLEAVE;
    }

    public String getLEAVEDESC() {
        return LEAVEDESC;
    }

    public void setLEAVEDESC(String LEAVEDESC) {
        this.LEAVEDESC = LEAVEDESC;
    }
}