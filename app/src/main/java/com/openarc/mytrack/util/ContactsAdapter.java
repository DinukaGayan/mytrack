package com.openarc.mytrack.util;

/**
 * Created by dinuka on 12/13/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.openarc.mytrack.R;
import com.openarc.mytrack.app.MyApplication;
import com.openarc.mytrack.helper.PrefManager;
import com.openarc.mytrack.model.Attendance;
import com.openarc.mytrack.model.Contacts;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Contacts> contactList;
    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
//    private PrefManager pref;

    public ContactsAdapter(Activity activity, List<Contacts> contactList) {
        this.activity = activity;
        this.contactList = contactList;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int location) {
        return contactList.get(location);
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
            convertView = inflater.inflate(R.layout.list_row_contacts, null);

        if (imageLoader == null)
            imageLoader = MyApplication.getInstance().getImageLoader();


//        CircleImageView thumbNail = (CircleImageView) convertView.findViewById(R.id.thumbnail);
        NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView email = (TextView) convertView.findViewById(R.id.email);
        TextView mobile = (TextView) convertView.findViewById(R.id.mobile);
        TextView extension = (TextView) convertView.findViewById(R.id.extension);

        Contacts a = contactList.get(position);


        thumbNail.setDefaultImageResId(R.drawable.avatar);
        thumbNail.setErrorImageResId(R.drawable.avatar);
        // thumbnail image
//        thumbNail.setImageUrl(a.getThumbnailUrl(), imageLoader);
        thumbNail.setImageUrl("http://yourdomain.com/images/propic/" + a.getEEENO() +".jpg", imageLoader);

        name.setText(a.getNAME());
        email.setText(a.getEMAIL());
        mobile.setText(a.getMOBILE());
        extension.setText(a.getEXTENSION());

        return convertView;
    }

}