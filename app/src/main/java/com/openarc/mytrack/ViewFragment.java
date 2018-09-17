package com.openarc.mytrack;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.openarc.mytrack.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFragment extends DialogFragment {


    public ViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_attendance_view, container, false);
        // Inflate the layout for this fragment
        getDialog().setTitle("View Attendance");
        getDialog().setCanceledOnTouchOutside(true);

        Button dismiss = (Button) rootView.findViewById(R.id.cancel);
        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return rootView;
    }

}
