package com.openarc.mytrack;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.openarc.mytrack.model.Contacts;
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
public class ContactsFragment extends Fragment {
    AlertDialog.Builder alertDialogBuilder;
    private ProgressDialog pDialog;
    private List<Contacts> contactsList = new ArrayList<Contacts>();
    private List<Contacts> searchcontactsList = new ArrayList<Contacts>();
    private ContactsAdapter adapter;
    private String jwt;
    private boolean mAutoHighlight;
    private PrefManager pref;
    Context context;
    FragmentManager fm;
    Contacts selectedRecord;
    EditText inputSearch;
    ListView listViewData;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        context = ContactsFragment.this.getContext();
        pref = new PrefManager(ContactsFragment.this.getContext());
        jwt = pref.getKeyJwt();
        alertDialogBuilder = new AlertDialog.Builder(context);

        inputSearch = (EditText) view.findViewById(R.id.inputSearch);
        listViewData = (ListView) view.findViewById(R.id.list);
        listViewData.setTextFilterEnabled(true);

        adapter = new ContactsAdapter(this.getActivity(), searchcontactsList);
        listViewData.setAdapter(adapter);
        pDialog = new ProgressDialog(ContactsFragment.this.getContext(),ProgressDialog.THEME_HOLO_DARK);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Loading...");

        if(((MainActivity) getActivity()).checkConnection()){
            pDialog.show();
            MakeRequest();
        }


        /**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable cs) {

                searchcontactsList.clear();
                for (int i = 0; i < contactsList.size(); i++) {
                    if (
                        contactsList.get(i).getNAME().toLowerCase().contains(cs.toString().toLowerCase()) ||
                            contactsList.get(i).getMOBILE().toLowerCase().contains(cs.toString().toLowerCase())
                        ) {
                        searchcontactsList.add(contactsList.get(i));
                    }
                }

                adapter = new ContactsAdapter(ContactsFragment.this.getActivity(), searchcontactsList);
                listViewData.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }


        });


        listViewData.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {


                selectedRecord = searchcontactsList.get(pos);

//                pref.saveAttendanceRecord(selectedRecord.getDATE(),selectedRecord.getINTIME(),selectedRecord.getOUTTIME());
//                Toast.makeText(ContactsFragment.this.getContext(),selectedRecord.getNAME(),Toast.LENGTH_LONG).show();
                new BottomSheet.Builder(ContactsFragment.this.getActivity()).title("Actions").sheet(R.menu.bottom_menu_contact).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.dial:
                                if(!selectedRecord.getMOBILE().equals("")){
                                    alertDialogBuilder
                                            .setTitle("MyTrack")
                                            .setMessage("Confirm to make a call to " + selectedRecord.getNAME().toString() + " ?")
                                            .setCancelable(false)
                                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    final int REQUEST_CODE_ASK_PERMISSIONS = 123;
                                                    String permission = Manifest.permission.CALL_PHONE;
                                                    if (ActivityCompat.shouldShowRequestPermissionRationale(ContactsFragment.this.getActivity(),permission)) {
                                                        ActivityCompat.requestPermissions(ContactsFragment.this.getActivity(), new String[]{permission}, REQUEST_CODE_ASK_PERMISSIONS);
                                                    }else {
                                                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + selectedRecord.getMOBILE()));
                                                        startActivity(intent);
                                                    }
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .create()
                                            .show();

                                }else{
                                    Toast.makeText(context,"Mobile number is blank",Toast.LENGTH_LONG).show();
                                }
                                break;


                            case R.id.save:
                                final int REQUEST_CODE_ASK_PERMISSIONS = 123;
                                String permission = Manifest.permission.WRITE_CONTACTS;
                                if (ActivityCompat.shouldShowRequestPermissionRationale(ContactsFragment.this.getActivity(),permission)) {
                                    ActivityCompat.requestPermissions(ContactsFragment.this.getActivity(), new String[]{permission}, REQUEST_CODE_ASK_PERMISSIONS);
                                }else {
                                    alertDialogBuilder
                                            .setTitle("MyTrack")
                                            .setMessage("Confirm to save contact " + selectedRecord.getNAME().toString() + " ?")
                                            .setCancelable(false)
                                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    // if this button is clicked, just close
                                                    // the dialog box and do nothing
                                                    CreateContact(
                                                            selectedRecord.getNAME(),
                                                            selectedRecord.getMOBILE(),
                                                            "",
                                                            "",
                                                            selectedRecord.getEMAIL(),
                                                            "OpenArc Systems Management Pvt Ltd",
                                                            ""
                                                    );
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .create()
                                            .show();

                                }
                                break;
//
//                            case R.id.delete:
//                                break;
                        }
                    }
                }).show();
                return true;

            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    protected void MakeRequest(){

        contactsList.clear();
        searchcontactsList.clear();

        adapter.notifyDataSetChanged();

        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("jwt", jwt);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.URL_CONTACTS,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("data").equals("exit")){
                                Toast.makeText(ContactsFragment.this.getContext(),"Session Expired",Toast.LENGTH_LONG).show();
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
                                        Contacts contact = new Contacts();
                                        contact.setEEENO(obj.getString("eeeno"));
                                        contact.setEMAIL(obj.getString("email"));
                                        contact.setEXTENSION(obj.getString("extension"));
                                        contact.setNAME(obj.getString("name"));
                                        contact.setMOBILE(obj.getString("mobile"));

                                        contactsList.add(contact);
                                        searchcontactsList.add(contact);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                pDialog.dismiss();
                                adapter.notifyDataSetChanged();
                                Toast.makeText(ContactsFragment.this.getContext(),"Long click item for more options",Toast.LENGTH_LONG).show();

                            }else{
                                Toast.makeText(ContactsFragment.this.getContext(),"Please try again",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ContactsFragment.this.getContext(),"Error Occured While Fetching data",Toast.LENGTH_LONG).show();
                            pDialog.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ContactsFragment.this.getContext(),"Please try again",Toast.LENGTH_LONG).show();
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


    private void CreateContact(String DisplayName,String MobileNumber,String HomeNumber,String WorkNumber,String emailID,String company,String jobTitle    ){
        ArrayList <ContentProviderOperation> ops = new ArrayList < ContentProviderOperation > ();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names
        if (DisplayName != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            DisplayName).build());
        }

        //------------------------------------------------------ Mobile Number
        if (MobileNumber != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        //------------------------------------------------------ Home Numbers
        if (HomeNumber != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, HomeNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                    .build());
        }

        //------------------------------------------------------ Work Numbers
        if (WorkNumber != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Email
        if (emailID != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Organization
        if (!company.equals("") && !jobTitle.equals("")) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .build());
        }

        // Asking the Contact provider to create a new contact
        try {
            ContactsFragment.this.getActivity().getApplicationContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Toast.makeText(context, "Contact Created", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
