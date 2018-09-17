package com.openarc.mytrack;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cocosw.bottomsheet.BottomSheet;
import com.openarc.mytrack.app.Config;
import com.openarc.mytrack.app.MyApplication;
import com.openarc.mytrack.helper.PrefManager;
import com.openarc.mytrack.model.Birthday;
import com.openarc.mytrack.model.Contacts;
import com.openarc.mytrack.util.BirthdayAdapter;
import com.openarc.mytrack.util.ContactsAdapter;

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
public class BirthdayFragment extends Fragment {

    AlertDialog.Builder alertDialogBuilder;
    private ProgressDialog pDialog;
    private List<Birthday> birthdayList = new ArrayList<Birthday>();
    private BirthdayAdapter adapter;
    private String jwt;
    private PrefManager pref;
    Context context;
    Birthday selectedRecord;
    ListView listViewData;

    public BirthdayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_birthday, container, false);
        context = BirthdayFragment.this.getContext();
        pref = new PrefManager(BirthdayFragment.this.getContext());
        jwt = pref.getKeyJwt();

        alertDialogBuilder = new AlertDialog.Builder(context);

        listViewData = (ListView) view.findViewById(R.id.listBirthdays);
        adapter = new BirthdayAdapter(this.getActivity(), birthdayList);
        listViewData.setAdapter(adapter);


        pDialog = new ProgressDialog(BirthdayFragment.this.getContext(),ProgressDialog.THEME_HOLO_DARK);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Loading...");

        if(((MainActivity) getActivity()).checkConnection()){
            pDialog.show();
            MakeRequest();
        }

        listViewData.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                selectedRecord = birthdayList.get(pos);
                if(!selectedRecord.getBDAY().equals("")) {
                    new BottomSheet.Builder(BirthdayFragment.this.getActivity()).title("Actions").sheet(R.menu.bottom_menu_birthday).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.dial:
                                    if (!selectedRecord.getMOBILE().equals("")) {
                                        alertDialogBuilder
                                                .setTitle("MyTrack")
                                                .setMessage("Confirm to make a call to " + selectedRecord.getNAME().toString() + " ?")
                                                .setCancelable(false)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        final int REQUEST_CODE_ASK_PERMISSIONS = 123;
                                                        String permission = Manifest.permission.CALL_PHONE;
                                                        if (ActivityCompat.shouldShowRequestPermissionRationale(BirthdayFragment.this.getActivity(), permission)) {
                                                            ActivityCompat.requestPermissions(BirthdayFragment.this.getActivity(), new String[]{permission}, REQUEST_CODE_ASK_PERMISSIONS);
                                                        } else {
                                                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + selectedRecord.getMOBILE()));
                                                            startActivity(intent);
                                                        }
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .create()
                                                .show();

                                    } else {
                                        Toast.makeText(context, "Mobile number is blank", Toast.LENGTH_LONG).show();
                                    }
                                    break;


                                case R.id.sms:

                                    final int REQUEST_CODE_ASK_PERMISSIONS = 123;
                                    String permission = Manifest.permission.SEND_SMS;
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(BirthdayFragment.this.getActivity(), permission)) {
                                        ActivityCompat.requestPermissions(BirthdayFragment.this.getActivity(), new String[]{permission}, REQUEST_CODE_ASK_PERMISSIONS);
                                    } else {
                                        if (!selectedRecord.getMOBILE().equals("")) {

                                            alertDialogBuilder
                                                    .setTitle("MyTrack")
                                                    .setMessage("Confirm to send birthday wish to " + selectedRecord.getNAME().toString() + " ?")
                                                    .setCancelable(false)
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
                                                            String permission = Manifest.permission.CALL_PHONE;
                                                            if (ActivityCompat.shouldShowRequestPermissionRationale(BirthdayFragment.this.getActivity(), permission)) {
                                                                ActivityCompat.requestPermissions(BirthdayFragment.this.getActivity(), new String[]{permission}, REQUEST_CODE_ASK_PERMISSIONS);
                                                            } else {
                                                                try {
                                                                    SmsManager sms = SmsManager.getDefault();
                                                                    sms.sendTextMessage(selectedRecord.getMOBILE().toString(), null, "Happy Birthday " + selectedRecord.getNAME() + "! \n " + "From : " + pref.getKeyName(), null, null);
                                                                    Toast.makeText(context, "Message sent!", Toast.LENGTH_LONG).show();
                                                                } catch (Exception e) {
                                                                    Toast.makeText(context, "Message not sent!", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .create()
                                                    .show();

                                        } else {
                                            Toast.makeText(context, "Mobile number is blank", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    break;

                                //                            case R.id.email:
                                //                                break;
                            }
                        }
                    }).show();
                }
                return true;
            }
        });

        return view;
    }

    protected void MakeRequest(){

        birthdayList.clear();
        adapter.notifyDataSetChanged();

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
                                getActivity().finish();
                            }else if(response.getString("data")!="" || response.getString("Today")!=""){


                                if(Integer.parseInt(response.getString("TodayCount")) > 0){
                                    JSONArray jsonArray = new JSONArray();
                                    jsonArray = (JSONArray) response.getJSONArray("Today");

                                    Birthday birthday = new Birthday();
                                    birthday.setNAME("TODAY");
                                    birthday.setBDAY("");
                                    birthday.setMOBILE("");
                                    birthdayList.add(birthday);

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        try {

                                            JSONObject obj = jsonArray.getJSONObject(i);
                                            birthday = new Birthday();
                                            birthday.setBDAY(obj.getString("dob"));
                                            birthday.setNAME(obj.getString("name"));
                                            birthday.setMOBILE(obj.getString("mobile"));
                                            birthday.setEMAIL(obj.getString("email"));
                                            birthday.setEEENO(obj.getString("eeeno"));
                                            birthdayList.add(birthday);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                if(response.getString("data")!=""){

                                    JSONArray jsonArray = new JSONArray();
                                    jsonArray = (JSONArray) response.getJSONArray("data");

                                    Birthday birthday = new Birthday();
                                    birthday.setNAME("THIS MONTH");
                                    birthday.setBDAY("");
                                    birthday.setMOBILE("");
                                    birthdayList.add(birthday);

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        try {

                                            JSONObject obj = jsonArray.getJSONObject(i);
                                            birthday = new Birthday();
                                            birthday.setBDAY(obj.getString("dob"));
                                            birthday.setNAME(obj.getString("name"));
                                            birthday.setMOBILE(obj.getString("mobile"));
                                            birthday.setEMAIL(obj.getString("email"));
                                            birthday.setEEENO(obj.getString("eeeno"));
                                            birthdayList.add(birthday);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                pDialog.dismiss();
                                Toast.makeText(BirthdayFragment.this.getContext(),"Long click item for more options",Toast.LENGTH_LONG).show();


                            }else{
                                Toast.makeText(context,"Please try again",Toast.LENGTH_LONG).show();
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

}
