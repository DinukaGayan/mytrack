package com.openarc.mytrack;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.openarc.mytrack.app.Config;
import com.openarc.mytrack.app.MyApplication;
import com.openarc.mytrack.helper.PrefManager;
import com.openarc.mytrack.model.Birthday;
import com.openarc.mytrack.receiver.ConnectivityReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,ConnectivityReceiver.ConnectivityReceiverListener {

    AlertDialog.Builder alertDialogBuilder;
    NavigationView navigationView = null;
    Toolbar toolbar = null;
    TextView username,useremail;
    private PrefManager pref;
    Boolean isConnected;
    TextView birthday;
    Context context;
    String jwt;
    private ProgressDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alertDialogBuilder = new AlertDialog.Builder(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        birthday = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_birthday));

        pref = new PrefManager(getApplicationContext());
        jwt = pref.getKeyJwt();

        context = this;



        // Checking if user session
        // if not logged in, take user to sms screen
        if (!pref.isLoggedIn()) {
            logout();
        }

        checkConnection();

        initializeCountDrawer();

//        if(!checkConnection()){
//            finish();
//            Toast.makeText(this,"Please check your internet Connection",Toast.LENGTH_LONG).show();
//        }

        //How to change elements in the header programatically
        View headerView = navigationView.getHeaderView(0);
        username = (TextView) headerView.findViewById(R.id.username);
        useremail = (TextView) headerView.findViewById(R.id.useremail);


        // Displaying user information from shared preferences
        HashMap<String, String> profile = pref.getUserDetails();
        username.setText(profile.get("name"));
        useremail.setText(profile.get("email"));


        navigationView.setNavigationItemSelectedListener(this);


        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
    }



    private void initializeCountDrawer(){
        pDialog = new ProgressDialog(context,ProgressDialog.THEME_HOLO_DARK);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Loading...");
        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("jwt", jwt);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.URL_BIRTHDAYS,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("data").equals("exit")){
                                Toast.makeText(context,"Session Expired",Toast.LENGTH_LONG).show();
                                pref.clearSession();
                                Intent intent = new Intent(context,LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }else if(Integer.parseInt(response.getString("TodayCount")) > 0 ){

                                birthday.setGravity(Gravity.CENTER_VERTICAL);
                                birthday.setTypeface(null, Typeface.BOLD);
                                birthday.setTextColor(getResources().getColor(R.color.colorAccent));
                                birthday.setText(response.getString("TodayCount"));
                                pDialog.dismiss();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context,"Error Occured While Fetching data",Toast.LENGTH_LONG).show();
                            pDialog.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Please try again",Toast.LENGTH_LONG).show();
                pDialog.dismiss();
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


    private void logout() {
        pref.clearSession();
        Intent intent = new Intent(this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            Toast.makeText(this,"Back Pressed",Toast.LENGTH_LONG).show();
            return;
//            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            StringBuilder sb = new StringBuilder();
            sb.append("This is the mobile version for the MyTrack web based resource management tool.");
            sb.append("\n");
            sb.append("it works as your virtual supervisor,");
            sb.append("\n");
            sb.append("which helps employees to monitor their attendance and KPI indicators.");
            ShowAlert(sb.toString());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_attendance) {
            getSupportActionBar().setTitle("Attendace");
            //Set the fragment initially
            AttendanceFragment fragment = new AttendanceFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmanetforloading, fragment);
            fragmentTransaction.commit();
            // Handle the camera action
        } else if (id == R.id.nav_leave) {
            getSupportActionBar().setTitle("Leave Utilization");

            //Set the fragment initially
            LeaveUtilizationFragment fragment = new LeaveUtilizationFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmanetforloading, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_home) {
            getSupportActionBar().setTitle("Home");

            HomeFragment fragment = new HomeFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmanetforloading, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_tools) {
            getSupportActionBar().setTitle("Contacts");

            ContactsFragment fragment = new ContactsFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmanetforloading, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_birthday) {
            getSupportActionBar().setTitle("Birthdays");
            BirthdayFragment fragment = new BirthdayFragment();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmanetforloading, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    public boolean checkConnection() {
        isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
        if(isConnected){
            return true;
        }else{
            return false;
        }
    }
    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
//            message = "Good! Connected to Internet";
//            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
            Snackbar snackbar = Snackbar.make(findViewById(R.id.toolbar), message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    protected void ShowAlert(String message){
        // set title
        alertDialogBuilder
                .setTitle("MyTrack")
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
}
