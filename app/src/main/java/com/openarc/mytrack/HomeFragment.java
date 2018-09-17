package com.openarc.mytrack;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.openarc.mytrack.app.Config;
import com.openarc.mytrack.app.MyApplication;
import com.openarc.mytrack.helper.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    TextView _DSUI,_CSUI,_CAWH,_MAWH;
    ProgressDialog pDialog;
    private PrefManager pref;
    private String jwt,CSUI,DSUI,CAWH,MAWH;
    Context context;
    View view;
    de.hdodenhof.circleimageview.CircleImageView img1,img2,img3,img4;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = HomeFragment.this.getContext();

        _CAWH = (TextView) view.findViewById(R.id.CAWH);
        _MAWH = (TextView) view.findViewById(R.id.MAWH);
        _CSUI = (TextView) view.findViewById(R.id.CSUI);
        _DSUI = (TextView) view.findViewById(R.id.DSUI);

        img1 = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.cardview_image1);
        img2 = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.cardview_image2);
        img3 = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.cardview_image3);
        img4 = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.cardview_image4);



        pref = new PrefManager(HomeFragment.this.getContext());
        jwt = pref.getKeyJwt();

        pDialog = new ProgressDialog(context,ProgressDialog.THEME_HOLO_DARK);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Loading...");

        if(((MainActivity) getActivity()).checkConnection()){

            GetSUI();
            GetAWH();
        }



        return view;
    }

    protected void GetSUI(){

        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("jwt", jwt);
        Log.i("SUI",jwt.toString());
        Log.i("SUI", Config.URL_SUI.toString());
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.URL_SUI,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("SUI",response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject = response.getJSONObject("SUI");
                            CSUI = jsonObject.getString("SUI") + "%";
                            _CSUI = (TextView) view.findViewById(R.id.CSUI);
                            _CSUI.setText(CSUI);

                            if(Float.parseFloat(jsonObject.getString("SUI")) > 92){
                                img3.setImageResource(R.drawable.up);
                            }else{
                                img3.setImageResource(R.drawable.down);
                            }

                            jsonObject = response.getJSONObject("DailySUI");
                            DSUI = jsonObject.getString("DailySUI") + "%";
                            _DSUI = (TextView) view.findViewById(R.id.DSUI);
                            _DSUI.setText(DSUI);

                            if(Float.parseFloat(jsonObject.getString("DailySUI")) > 92){
                                img4.setImageResource(R.drawable.up);
                            }else{
                                img4.setImageResource(R.drawable.down);
                            }


                            pDialog.dismiss();

                        } catch (JSONException e) {
                            Toast.makeText(context,"Error occured while fetching SUI",Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                            pDialog.dismiss();

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Toast.makeText(context,"Please try again later ",Toast.LENGTH_LONG).show();
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


    protected void GetAWH(){
        pDialog.show();

        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("jwt", jwt);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.URL_AWH,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = new JSONArray();
                            jsonArray = (JSONArray) response.getJSONArray("data");
                            JSONObject obj = jsonArray.getJSONObject(0);

                            CAWH = obj.getString("ATSH_CUMULATIVEAWH");
                            _CAWH = (TextView) view.findViewById(R.id.CAWH);
                            _CAWH.setText(CAWH);


                            if(Float.parseFloat(CAWH) > 9.5){
                                img1.setImageResource(R.drawable.up);
                            }else{
                                img1.setImageResource(R.drawable.down);
                            }

                            MAWH = obj.getString("ATSH_MONTHLYAWH");
                            _MAWH = (TextView) view.findViewById(R.id.MAWH);
                            _MAWH.setText(MAWH);


                            if(Float.parseFloat(MAWH) > 9.5){
                                img2.setImageResource(R.drawable.up);
                            }else{
                                img2.setImageResource(R.drawable.down);
                            }

                            pDialog.dismiss();

                        } catch (JSONException e) {
                            Toast.makeText(HomeFragment.this.getContext(),"Error occured while fetching AWH",Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                            pDialog.dismiss();

                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(context,"Please try again later ",Toast.LENGTH_LONG).show();
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




}
