package com.openarc.mytrack;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.cocosw.bottomsheet.BottomSheet;
import com.openarc.mytrack.app.Config;
import com.openarc.mytrack.app.MyApplication;
import com.openarc.mytrack.helper.PrefManager;
import com.openarc.mytrack.model.Attendance;
import com.openarc.mytrack.util.AttendanceAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends Fragment  implements
        DatePickerDialog.OnDateSetListener{



    public AttendanceFragment() {
        // Required empty public constructor
    }

    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private List<Attendance> attendanceList = new ArrayList<Attendance>();
    private AttendanceAdapter adapter;
    private String jwt;
    private PrefManager pref;
    Context context;
    FragmentManager fm;
    Attendance selectedRecord;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pref = new PrefManager(AttendanceFragment.this.getContext());
        jwt = pref.getKeyJwt();



        ((MainActivity) getActivity()).checkConnection();

        View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        context = AttendanceFragment.this.getContext();
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);


        ListView listViewData = (ListView) view.findViewById(R.id.list);

        adapter = new AttendanceAdapter(this.getActivity(), attendanceList);
        listViewData.setAdapter(adapter);

        listViewData.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
//                fab.show();
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
                if(totalItemCount > 0 && visibleItemCount < totalItemCount && (firstVisibleItem + visibleItemCount) ==  totalItemCount)
                {
                    fab.hide();
                }else{
                    fab.show();
                }
            }
        });

        listViewData.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {


                selectedRecord = attendanceList.get(pos);

                pref.saveAttendanceRecord(selectedRecord.getDATE(),selectedRecord.getINTIME(),selectedRecord.getOUTTIME());

//                Toast.makeText(AttendanceFragment.this.getContext(),selectedRecord.getDATE(),Toast.LENGTH_LONG).show();


                new BottomSheet.Builder(AttendanceFragment.this.getActivity()).title("Actions").sheet(R.menu.bottom_menu).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.report:
                                fm = getFragmentManager();
                                ReportFragment report = new ReportFragment();
                                report.setStyle(report.STYLE_NORMAL, R.style.CustomDialog);
                                report.show(fm, "");
                                break;
//                            case R.id.view:
//                                fm = getFragmentManager();
//                                ViewFragment view = new ViewFragment();
//                                view.setStyle(view.STYLE_NORMAL, R.style.CustomDialog);
//                                view.show(fm, "");
//                                break;

//                            case R.id.delete:
//                                break;
                        }
                    }
                }).show();
                return true;

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                        AttendanceFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
//                dpd.(mAutoHighlight);
                dpd.show(AttendanceFragment.this.getActivity().getFragmentManager(), "Datepickerdialog");
            }
        });
        Toast.makeText(AttendanceFragment.this.getContext(),"Press search button to start searching",Toast.LENGTH_LONG).show();

        return view;

    }

     protected void MakeRequest(String inpdatefrom,String inpdateto){

        attendanceList.clear();
        adapter.notifyDataSetChanged();

        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("jwt", jwt);
        jsonParams.put("DATEFROM", inpdatefrom);
        jsonParams.put("DATETO", inpdateto);

        Log.i("params",jsonParams.toString());
        pDialog = new ProgressDialog(AttendanceFragment.this.getContext(),ProgressDialog.THEME_HOLO_DARK);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Loading...");
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.URL_ATTENDANCE,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, response.toString());
                            if(response.getString("data").equals("exit")){
                                Toast.makeText(AttendanceFragment.this.getContext(),"Session Expired",Toast.LENGTH_LONG).show();
                                pref.clearSession();
                                Intent intent = new Intent(context,LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                getActivity().finish();
                            }else if(response.getString("data")!=""){
                                JSONArray jsonArray = new JSONArray();
                                jsonArray = (JSONArray) response.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {

                                        JSONObject obj = jsonArray.getJSONObject(i);
                                        Attendance attendance = new Attendance();
                                        attendance.setDATE(obj.getString("DATE"));
                                        attendance.setINTIME(obj.getString("INTIME"));
                                        attendance.setOUTTIME(obj.getString("OUTTIME"));
                                        attendance.setDAY(obj.getString("DAY"));
                                        attendance.setREMARK(obj.getString("REMARK"));
                                        attendance.setISLEAVE(obj.getString("ISLEAVE"));
                                        attendance.setLEAVEDESC(obj.getString("LEAVEDESC"));
                                        attendance.setISHOLIDAY(obj.getString("ISHOLIDAY"));
                                        attendance.setHOLIDAYDESC(obj.getString("HOLIDAYDESC"));
                                        attendance.setAPPRSTAT(obj.getString("APPRSTAT"));


                                        // adding movie to movies array
                                        attendanceList.add(attendance);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                pDialog.dismiss();
                                adapter.notifyDataSetChanged();
                                Toast.makeText(AttendanceFragment.this.getContext(),"Long click item for more options",Toast.LENGTH_LONG).show();

                            }else{
                                Toast.makeText(AttendanceFragment.this.getContext(),"Please try again",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AttendanceFragment.this.getContext(),"Error Occured While Fetching data",Toast.LENGTH_LONG).show();
                            pDialog.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(AttendanceFragment.this.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                Toast.makeText(AttendanceFragment.this.getContext(),"Please try again",Toast.LENGTH_LONG).show();
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


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth,int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
//        String date = "You picked the following date: From- "+dayOfMonth+"/"+(++monthOfYear)+"/"+year+" To "+dayOfMonthEnd+"/"+(++monthOfYearEnd)+"/"+yearEnd;
//        Toast.makeText(AttendanceFragment.this.getContext(),date.toString(),Toast.LENGTH_LONG).show();

        String fromDate = year + "-" + (++monthOfYear) + "-" + dayOfMonth;
        String toDate = yearEnd + "-" + (++monthOfYearEnd) + "-" + dayOfMonthEnd;
//        Toast.makeText(AttendanceFragment.this.getContext(),fromDate.toString(),Toast.LENGTH_LONG).show();
//        Toast.makeText(AttendanceFragment.this.getContext(),toDate.toString(),Toast.LENGTH_LONG).show();
        pref.createAttendanceQuery(fromDate,toDate);

        if(((MainActivity) getActivity()).checkConnection()){
            MakeRequest(fromDate,toDate);
        }

    }


}
