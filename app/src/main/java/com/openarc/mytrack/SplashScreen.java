package com.openarc.mytrack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.openarc.mytrack.receiver.ConnectivityReceiver;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    AlertDialog.Builder alertDialogBuilder;
    Boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        alertDialogBuilder = new AlertDialog.Builder(this);
        if(checkConnection()) {
//            getVersion();
            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }

    private boolean checkConnection() {
        isConnected = ConnectivityReceiver.isConnected();
        if(!isConnected){
            alertDialogBuilder
                    .setTitle("MyTrack")
                    .setMessage("This application requires a active internet connection")
                    .setCancelable(false)
                    .setNegativeButton("Ok",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.dismiss();
                            finish();

                        }
                    })
                    .create()
                    .show();
        }
        return isConnected;
    }


//    private void getVersion(){
//
//        String newVersion;
//        String onlineVersion = BuildConfig.VERSION_NAME;
//        try {
//
//            newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=com.dinuka.myapplication&hl=it")
//                    .timeout(30000)
//                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
//                    .referrer("http://www.google.com")
//                    .get()
//                    .select("div[itemprop=softwareVersion]")
//                    .first()
//                    .ownText();
//
//
//            Log.d("VERSION",newVersion);
//
//            if (newVersion != null && !newVersion.isEmpty()) {
//                if (Float.valueOf("1.0") < Float.valueOf(newVersion)) {
//                    Toast.makeText(SplashScreen.this,newVersion,Toast.LENGTH_LONG).show();
//                    Log.d("update", "Current version " + onlineVersion + "playstore version " + newVersion);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}