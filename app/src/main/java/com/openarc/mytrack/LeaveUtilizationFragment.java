package com.openarc.mytrack;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.openarc.mytrack.app.Config;
import com.openarc.mytrack.app.MyApplication;
import com.openarc.mytrack.helper.PrefManager;
import com.openarc.mytrack.model.Attendance;
import com.openarc.mytrack.model.Contacts;
import com.openarc.mytrack.model.Leaves;
import com.openarc.mytrack.util.ContactsAdapter;
import com.openarc.mytrack.util.LeavesAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeaveUtilizationFragment extends Fragment {

    Context context;
    private PrefManager pref;
    String jwt;
    ListView listViewData;
    private LeavesAdapter adapter;
    private List<Leaves> leavesList = new ArrayList<Leaves>();
    private ProgressDialog pDialog;



    public LeaveUtilizationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_leave_utilization, container, false);
        context = LeaveUtilizationFragment.this.getContext();
        pref = new PrefManager(LeaveUtilizationFragment.this.getContext());
        jwt = pref.getKeyJwt();
        listViewData = (ListView) view.findViewById(R.id.list);
        listViewData.setTextFilterEnabled(true);

        adapter = new LeavesAdapter(LeaveUtilizationFragment.this.getActivity(),leavesList);
        listViewData.setAdapter(adapter);

        if(((MainActivity) getActivity()).checkConnection()){
            MakeRequest();
        }

        // Inflate the layout for this fragment
        return view;
    }



    protected void MakeRequest(){

        leavesList.clear();
        adapter.notifyDataSetChanged();

        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("jwt", jwt);

        pDialog = new ProgressDialog(context,ProgressDialog.THEME_HOLO_DARK);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Loading...");
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.URL_LEAVE_UTILIZATION,
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
                                getActivity().finish();
                            }else if(response.getString("data")!=""){
                                pDialog.dismiss();
                                JSONArray jsonArray = new JSONArray();
                                jsonArray = (JSONArray) response.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {

                                        JSONObject obj = jsonArray.getJSONObject(i);
                                        Leaves leave = new Leaves();
                                        leave.setCode(obj.getString("Code"));
                                        leave.setDescr(obj.getString("Descr"));
                                        leave.setEntitled(obj.getString("entitle"));
                                        leave.setUtilized(obj.getString("utilized"));
                                        leave.setAvailable(obj.getString("available"));

                                        leavesList.add(leave);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }else{
                                Toast.makeText(context,"Please try again",Toast.LENGTH_LONG).show();
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
                Toast.makeText(context,error.getMessage(),Toast.LENGTH_LONG).show();
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

}
