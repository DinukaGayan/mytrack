package com.openarc.mytrack;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.openarc.mytrack.app.Config;
import com.openarc.mytrack.app.MyApplication;
import com.openarc.mytrack.helper.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class PasswordResetFragment extends Fragment {


    EditText _currentPassword,_newPassword,_confirmPassword;
    Button _reset;
    View view;
    Context context;
    ProgressDialog pDialog;
    private PrefManager pref;
    String jwt;
    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();

    public PasswordResetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (imageLoader == null)
            imageLoader = MyApplication.getInstance().getImageLoader();

        view = inflater.inflate(R.layout.fragment_password_reset, container, false);
        NetworkImageView thumbNail = (NetworkImageView) view.findViewById(R.id.thumbnail);
        thumbNail.setImageUrl("http://yourdomain.com/images/propic/default.jpg",imageLoader);

        return view;
    }

}
