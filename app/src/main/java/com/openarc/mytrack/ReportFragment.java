package com.openarc.mytrack;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.openarc.mytrack.app.Config;
import com.openarc.mytrack.app.MyApplication;
import com.openarc.mytrack.helper.PrefManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;


public class ReportFragment extends DialogFragment {

    Button dismiss,submit;
    TextView timeinValue,timeoutValue,date;
    View rootView;
    AlertDialog.Builder alertDialogBuilder;
    private String jwt,remarktext;
    private PrefManager pref;
    Context context;
    private ProgressDialog pDialog;
    AutoCompleteTextView textView;
    int inttimein,inttimeout;
    NumberFormat formatter = new DecimalFormat("00");


    public ReportFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_attendance_submit, container, false);
        context = ReportFragment.this.getContext();
        pref = new PrefManager(ReportFragment.this.getContext());
        jwt = pref.getKeyJwt();

        inttimein =0;
        inttimeout =0;

        alertDialogBuilder = new AlertDialog.Builder(context);

        getDialog().setTitle("Report Attendance");
        getDialog().setCanceledOnTouchOutside(true);

        dismiss = (Button) rootView.findViewById(R.id.cancel);
        submit  = (Button) rootView.findViewById(R.id.submit);
        timeinValue = (TextView) rootView.findViewById(R.id.timeinvalue);
        timeoutValue = (TextView) rootView.findViewById(R.id.timeoutvalue);
        date = (TextView) rootView.findViewById(R.id.datevalue);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_dropdown_item_1line, REMARKLIST);
        textView = (AutoCompleteTextView) rootView.findViewById(R.id.remarkvalue);
        textView.setAdapter(adapter);

        // Displaying attendance record information from shared preferences
        HashMap<String, String> selectedRecord = pref.getAttendanceRecordDetails();
        date.setText(selectedRecord.get("date"));
        timeinValue.setText(selectedRecord.get("timein"));
        timeoutValue.setText(selectedRecord.get("timeout"));

        if(!timeinValue.getText().toString().isEmpty()){
            StringTokenizer st = new StringTokenizer(timeinValue.getText().toString(), ":");
            int hour,minute;
            hour = Integer.parseInt(st.nextToken());
            minute = Integer.parseInt(st.nextToken());

            inttimein = (hour * 60) + minute;
        }

        if(!timeoutValue.getText().toString().isEmpty()){
            StringTokenizer st = new StringTokenizer(timeoutValue.getText().toString(), ":");
            int hour,minute;
            hour = Integer.parseInt(st.nextToken());
            minute = Integer.parseInt(st.nextToken());

            inttimeout = (hour * 60) + minute;
        }


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeinValue.setError(null);
                timeoutValue.setError(null);
                textView.setError(null);

                if(timeinValue.getText().toString().isEmpty()){
                    timeinValue.setError("Invalid time in");
                }else if(timeoutValue.getText().toString().isEmpty()){
                    timeoutValue.setError("Invalid time out");
                }else if(inttimein > inttimeout){
                    timeoutValue.setError("time out must be greater than time in");
                }else if(textView.getText().toString().isEmpty()){
                    textView.setError("Invalid remark");
                }else{
                    alertDialogBuilder
                            .setTitle("MyTrack")
                            .setMessage("Confirm to submit data ?")
                            .setCancelable(false)
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {

                                    if(((MainActivity) getActivity()).checkConnection()){
                                        MakeRequest(date.getText().toString(),textView.getText().toString(),timeinValue.getText().toString(),timeoutValue.getText().toString());
                                    }
                                }
                            })
                            .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
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
        });


        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        timeinValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ReportFragment.this.getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timeinValue.setText(formatter.format(selectedHour) + ":" + formatter.format(selectedMinute) + ":00" );
                        inttimein = (selectedHour * 60) + selectedMinute;
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time in");
                mTimePicker.show();

            }
        });

        timeoutValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ReportFragment.this.getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timeoutValue.setText(formatter.format(selectedHour) + ":" + formatter.format(selectedMinute) + ":00" );
                        inttimeout = (selectedHour * 60) + selectedMinute;
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time out");
                mTimePicker.show();

            }
        });

        return rootView;
    }

    private static final String[] REMARKLIST = new String[] {
            "Direct Site visit", "Time in not recorded", "Time out not recorded", "Time in & out not recorded", "Other"
    };

    protected void MakeRequest(String inpdate,String remark,String timein,String timeout){

        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("jwt", jwt);
        jsonParams.put("REPORTDATE", inpdate);
        jsonParams.put("REMARK", remark);
        jsonParams.put("REPORTTIMEIN", timein);
        jsonParams.put("REPORTTIMEOUT", timeout);

        Log.i("params",jsonParams.toString());
        pDialog = new ProgressDialog(context,ProgressDialog.THEME_HOLO_DARK);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Loading...");
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.URL_REPORTATTENDANCE,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            pDialog.dismiss();

                            if(response.getString("data").equals("exit")){
                                Toast.makeText(context,"Session Expired",Toast.LENGTH_LONG).show();
                                pref.clearSession();
                                Intent intent = new Intent(context,LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                getActivity().finish();
                            }else if(response.getString("data").equals("success")){
                                Toast.makeText(context,"Attendance updated successfully,Please do refresh",Toast.LENGTH_LONG).show();
                                getDialog().dismiss();
                            }else{
                                Toast.makeText(context,response.getString("data"),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context,"Error Occured While saving data",Toast.LENGTH_LONG).show();
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
