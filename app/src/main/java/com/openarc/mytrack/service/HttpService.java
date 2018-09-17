package com.openarc.mytrack.service;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.openarc.mytrack.MainActivity;
import com.openarc.mytrack.app.Config;
import com.openarc.mytrack.app.MyApplication;
import com.openarc.mytrack.helper.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dinuka on 12/10/2016.
 */
public class HttpService extends IntentService {

    private static String TAG = HttpService.class.getSimpleName();
    private PrefManager pref;



    public HttpService() {
        super(HttpService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        pref = new PrefManager(getApplicationContext());

        if (intent != null) {
            String username = pref.getKeyEmail();
            String password = pref.getKeyPassword();
            String otp = intent.getStringExtra("otp");

            Toast.makeText(getApplicationContext(),username,Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),password,Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),otp,Toast.LENGTH_LONG).show();
            verifyOtp(username,password,otp);
        }
    }

    /**
     * Posting the OTP to server and activating the user
     *
     * @param otp otp received in the SMS
     */
    private void verifyOtp(final String userName,final String password,final String otp) {
//        pDialog = new ProgressDialog(getApplicationContext(),ProgressDialog.THEME_HOLO_DARK);
//        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pDialog.setMessage("Verifying...");
//        pDialog.show();
        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("username", userName);
        jsonParams.put("password", password);
        jsonParams.put("passcode", otp);

        JsonObjectRequest strReq = new JsonObjectRequest(
                Request.Method.POST,
                Config.URL_VERIFY_OTP,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {


                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());

                    try {
                        if(response.getString("respcode").equals("AR")){
                            Log.e("RESPONSE",response.getString("message"));
                            Toast.makeText(getApplicationContext(),response.getString("message"),Toast.LENGTH_LONG).show();
                        }else if(response.getString("respcode").equals("IP")){
                            Toast.makeText(getApplicationContext(),response.getString("message"),Toast.LENGTH_LONG).show();
                            Log.e("RESPONSE",response.getString("message"));
                        }else if(response.getString("respcode").equals("OK")){

                            if(response.getString("jwt") != ""){
                                Log.e("RESPONSE",response.getString("message"));
                                String name = response.getString("name");
                                String email = response.getString("usermail");
                                String jwt = response.getString("jwt");
                                String mobile = "";

                                pref.createLogin(name, email, mobile);
                                pref.setKeyJwt(jwt);
                                Intent intent = new Intent(HttpService.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),response.getString("message"),Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("otp", otp);
                    Log.e(TAG, "Posting params: " + params.toString());
                    return params;
                }

            };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }


}
