package com.openarc.mytrack.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

/**
 * Created by dinuka on 12/13/2016.
 */

public class PrefManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "MYTRACK";

    // All Shared Preferences Keys
    private static final String KEY_IS_WAITING_FOR_SMS = "IsWaitingForSms";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_JWT = "jwt";
    private static final String KEY_PASSWORD = "password";

    private static final String KEY_DATESUBMIT = "date";
    private static final String KEY_TIMEIN = "timein";
    private static final String KEY_TIMEOUT = "timeout";


    private static final String KEY_ATTENDANCEQUERY_DATEFROM= "datefrom";
    private static final String KEY_ATTENDANCEQUERY_DATETO = "dateto";


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setIsWaitingForSms(boolean isWaiting) {
        editor.putBoolean(KEY_IS_WAITING_FOR_SMS, isWaiting);
        editor.commit();
    }

    public boolean isWaitingForSms() {
        return pref.getBoolean(KEY_IS_WAITING_FOR_SMS, false);
    }

    public void setKeyJwt(String jwt) {
        editor.putString(KEY_JWT, jwt);
        editor.commit();
    }

    public String getKeyJwt() {
        return pref.getString(KEY_JWT, null);
    }

    public void setKeyPassword(String password) {
        editor.putString(KEY_PASSWORD, password);
        editor.commit();
    }

    public String getKeyPassword() {
        return pref.getString(KEY_PASSWORD, null);
    }

    public String getKeyEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public void setKeyEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }


    public void saveAttendanceRecord(String date, String timein, String timeout) {
        editor.putString(KEY_DATESUBMIT, date);
        editor.putString(KEY_TIMEIN, timein);
        editor.putString(KEY_TIMEOUT, timeout);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> profile = new HashMap<>();
        profile.put("name", pref.getString(KEY_NAME, null));
        profile.put("email", pref.getString(KEY_EMAIL, null));
        profile.put("jwt", pref.getString(KEY_JWT, null));
        return profile;
    }

    public HashMap<String, String> getAttendanceRecordDetails() {
        HashMap<String, String> record = new HashMap<>();
        record.put("date", pref.getString(KEY_DATESUBMIT, null));
        record.put("timein", pref.getString(KEY_TIMEIN, null));
        record.put("timeout", pref.getString(KEY_TIMEOUT, null));
        return record;
    }


    public void createLogin(String name, String email, String jwt) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_JWT, jwt);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }


    public  String getKeyName() {
        return pref.getString(KEY_NAME, null);
    }

    public void createAttendanceQuery(String datefrom, String dateto) {
        editor.putString(KEY_ATTENDANCEQUERY_DATEFROM, datefrom);
        editor.putString(KEY_ATTENDANCEQUERY_DATETO, dateto);
        editor.commit();
    }

    public static String getKeyAttendancequeryDatefrom() {
        return KEY_ATTENDANCEQUERY_DATEFROM;
    }

    public static String getKeyAttendancequeryDateto() {
        return KEY_ATTENDANCEQUERY_DATETO;
    }
}
