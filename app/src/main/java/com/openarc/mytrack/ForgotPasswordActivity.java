package com.openarc.mytrack;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.openarc.mytrack.app.Config;
import com.openarc.mytrack.app.MyApplication;
import com.openarc.mytrack.helper.PrefManager;
import com.openarc.mytrack.receiver.ConnectivityReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dinuka on 12/10/2016.
 */
public class ForgotPasswordActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    EditText _eeeNo,_emailText,_passwordText,_confirmPasswordText,inputOtp;
    Button btnVerify,btnVerifyOtp,btnCancelOtp,btnChange,btnChangeCancel;
    TextView _signinLink,_signupLink;
    boolean isConnected;
    private RequestQueue mQueue;
    ProgressDialog pDialog;
    private PrefManager pref;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pref = new PrefManager(this);

        viewPager = (ViewPager) findViewById(R.id.viewPagerChangePassword);
        viewPager.setOffscreenPageLimit(3);
        inputOtp = (EditText) findViewById(R.id.inputOtp);
        _eeeNo = (EditText) findViewById(R.id.eeeno);
        _emailText = (EditText) findViewById(R.id.email);
        _passwordText = (EditText) findViewById(R.id.newpassword);
        _confirmPasswordText = (EditText) findViewById(R.id.confirmpassword);

        btnVerify =  (Button) findViewById(R.id.validate_email);

        btnVerifyOtp = (Button) findViewById(R.id.btn_verify_otp);
        btnCancelOtp = (Button) findViewById(R.id.btn_cancel_otp);


        btnChange = (Button) findViewById(R.id.Change_Password);
        btnChangeCancel = (Button) findViewById(R.id.Change_Password_cancel);


        _signupLink = (TextView) findViewById(R.id.email_sign_up_button);
        _signinLink = (TextView)  findViewById(R.id.email_sign_in_button);

        checkConnection();

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        _signinLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signin activity
                finish();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        btnVerifyOtp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                verifyOtp();
            }
        });

        btnCancelOtp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
                _emailText.setText("");
                _passwordText.setText("");
                _confirmPasswordText.setText("");
                _eeeNo.setText(null);
                _emailText.setText("");

            }
        });




        btnVerify.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(ValidateEmailEEEno()){
                    MakeVerifyEmailRequest();
                }
            }
        });


        btnChange.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(ValidatePassword()){
                    ChangePassword();
                }
            }
        });

        btnChangeCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
                _emailText.setText("");
                _passwordText.setText("");
                _confirmPasswordText.setText("");
                _eeeNo.setText(null);
                _emailText.setText("");
            }
        });



        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }


        });

        /**
         * Checking if the device is waiting for sms
         * showing the user OTP screen
         */


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            CreatePermissions();
        }


    }

    public boolean ValidateEmailEEEno(){
        Boolean valid = true;
        String eeeno = _eeeNo.getText().toString();
        String email = _emailText.getText().toString();
        _eeeNo.setError(null);
        _emailText.setError(null);

        if(eeeno.length() == 0){
            _eeeNo.setError("invalid eee no");
            valid = false;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("invalid email address");
            valid = false;
        }
        return valid;
    }

    public void CreatePermissions(){
        final int REQUEST_CODE_ASK_PERMISSIONS = 123;
        String permission = Manifest.permission.READ_SMS;
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission)) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_CODE_ASK_PERMISSIONS);
        }

        permission = Manifest.permission.CALL_PHONE;
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission)) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_CODE_ASK_PERMISSIONS);
        }

        permission = Manifest.permission.READ_CONTACTS;
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission)) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_CODE_ASK_PERMISSIONS);
        }

        permission = Manifest.permission.WRITE_CONTACTS;
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission)) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }


    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }


    public void onRegisterFailed() {
        _signupLink.setEnabled(true);
    }


    private boolean checkConnection() {
        isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
        if(isConnected){
            return true;
        }else{
            return false;
        }
    }

    public boolean ValidatePassword() {
        boolean valid = true;

        String password = _passwordText.getText().toString();
        String confirmpassword = _confirmPasswordText.getText().toString();

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (confirmpassword.isEmpty() || confirmpassword.length() < 4 || confirmpassword.length() > 10) {
            _confirmPasswordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        }

        if(!confirmpassword.equals(password)){
            _confirmPasswordText.setError("password mismatched");
            valid = false;
        }else{
            _confirmPasswordText.setError(null);
        }

        return valid;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Tag used to cancel the request

    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar.make(findViewById(R.id.email_sign_in_button), message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }



    protected void MakeVerifyEmailRequest(){

        String eeeno = _eeeNo.getText().toString();
        String email = _emailText.getText().toString();

        pDialog = new ProgressDialog(this,ProgressDialog.THEME_HOLO_DARK);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Verifying...");
        pDialog.show();
        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("eeeno", eeeno);
        jsonParams.put("email", email);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.URL_VERIFYEMAIL,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("respcode").equals("ER")){
                                pDialog.dismiss();
                                ShowAlert(response.getString("message"));
                            }else if(response.getString("respcode").equals("OK")){
                                pDialog.dismiss();
                                ShowAlert(response.getString("message"));
                                viewPager.setCurrentItem(1);
                            }
                        } catch (JSONException e) {
                            pDialog.dismiss();
                            e.printStackTrace();
                        }
                        onRegisterFailed();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                ShowAlert(error.getMessage());
                onRegisterFailed();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", "My useragent");
                return headers;
            }

        };

        MyApplication.getInstance().addToRequestQueue(jsonObjReq);
    }



    protected void VerifyChangePasswordOTP(){

        String OTP = inputOtp.getText().toString();
        String email = _emailText.getText().toString();

        pDialog = new ProgressDialog(this,ProgressDialog.THEME_HOLO_DARK);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Verifying...");
        pDialog.show();
        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("otp", OTP);
        jsonParams.put("email", email);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.URL_VERIFY_PASSWORD_RESET_OTP,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("respcode").equals("ER")){
                                pDialog.dismiss();
                                ShowAlert(response.getString("message"));
                            }else if(response.getString("respcode").equals("OK")){
                                pDialog.dismiss();
                                ShowAlert(response.getString("message"));
                                viewPager.setCurrentItem(2);
                            }
                        } catch (JSONException e) {
                            pDialog.dismiss();
                            e.printStackTrace();
                        }
                        onRegisterFailed();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                ShowAlert(error.getMessage());
                onRegisterFailed();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", "My useragent");
                return headers;
            }

        };

        MyApplication.getInstance().addToRequestQueue(jsonObjReq);
    }

    protected void ChangePassword(){

        String newpassword = _confirmPasswordText.getText().toString();
        String email = _emailText.getText().toString();

        pDialog = new ProgressDialog(this,ProgressDialog.THEME_HOLO_DARK);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Verifying...");
        pDialog.show();
        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("newpassword", newpassword);
        jsonParams.put("email", email);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.URL_CHANGE_PASSWORD,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("respcode").equals("ER")){
                                pDialog.dismiss();
                                ShowAlert(response.getString("message"));
                            }else if(response.getString("respcode").equals("OK")){
                                pDialog.dismiss();
                                Toast.makeText(ForgotPasswordActivity.this,response.getString("message"),Toast.LENGTH_LONG).show();
                                finish();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            pDialog.dismiss();
                            e.printStackTrace();
                        }
                        onRegisterFailed();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                ShowAlert(error.getMessage());
                onRegisterFailed();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", "My useragent");
                return headers;
            }

        };

        MyApplication.getInstance().addToRequestQueue(jsonObjReq);
    }

    protected void ShowAlert(String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle("MyTrack");

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    /**
     * sending the OTP to server and activating the user
     */
    private void verifyOtp() {
        String otp = inputOtp.getText().toString().trim();

        if (!otp.isEmpty()) {
            VerifyChangePasswordOTP();
        } else {
            Toast.makeText(getApplicationContext(), "Please enter the OTP", Toast.LENGTH_SHORT).show();
        }
    }


    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            destroyItem((View) container, position, object);
        }

        public Object instantiateItem(View collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.layout_check;
                    break;
                case 1:
                    resId = R.id.layout_otp;
                    break;
                case 2:
                    resId = R.id.layout_change_password;
                    break;
            }
            return findViewById(resId);
        }
    }
}
