package com.openarc.mytrack.util;

/**
 * Created by dinuka on 12/13/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.openarc.mytrack.R;
import com.openarc.mytrack.app.MyApplication;
import com.openarc.mytrack.model.Birthday;
import com.openarc.mytrack.model.Contacts;

import java.util.List;

public class BirthdayAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Birthday> birthdayList;
    RelativeLayout listRow;
    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
//    private PrefManager pref;

    public BirthdayAdapter(Activity activity, List<Birthday> birthdayList) {
        this.activity = activity;
        this.birthdayList = birthdayList;
    }

    @Override
    public int getCount() {
        return birthdayList.size();
    }

    @Override
    public Object getItem(int location) {
        return birthdayList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        pref = new PrefManager(convertView.getContext());

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_birthday, null);

        if (imageLoader == null)
            imageLoader = MyApplication.getInstance().getImageLoader();

//        CircleImageView thumbNail = (CircleImageView) convertView.findViewById(R.id.thumbnail);
        NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        listRow = (RelativeLayout) convertView.findViewById(R.id.listRow);
//        TextView mobile = (TextView) convertView.findViewById(R.id.mobile);
//        TextView extension = (TextView) convertView.findViewById(R.id.extension);
//
        Birthday a = birthdayList.get(position);
//
//
        thumbNail.setDefaultImageResId(R.drawable.avatar);
        thumbNail.setErrorImageResId(R.drawable.avatar);
//        // thumbnail image
        thumbNail.setImageUrl("http://yourdomain.com/images/propic/" + a.getEEENO() +".jpg", imageLoader);
//
        name.setText(a.getNAME());
        date.setText(a.getBDAY());

        if(a.getNAME().toString().equals("TODAY") || a.getNAME().toString().equals("THIS MONTH")){
            thumbNail.setVisibility(View.GONE);
            listRow.setBackgroundColor(Color.parseColor("#66000000"));
        }else{
            thumbNail.setVisibility(View.VISIBLE);
            listRow.setBackgroundColor(Color.parseColor("WHITE"));
        }


        return convertView;
    }

}