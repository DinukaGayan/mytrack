package com.openarc.mytrack.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.openarc.mytrack.R;
import com.openarc.mytrack.app.MyApplication;
import com.openarc.mytrack.model.Attendance;
import com.openarc.mytrack.model.Leaves;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dinuka on 12/26/2016.
 */

public class LeavesAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Leaves> leaveList;
    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();

    public LeavesAdapter(Activity activity, List<Leaves> leaveList) {
        this.activity = activity;
        this.leaveList = leaveList;
    }

    @Override
    public int getCount() {
        return leaveList.size();
    }

    @Override
    public Object getItem(int location) {
        return leaveList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_leaves, null);

        if (imageLoader == null)
            imageLoader = MyApplication.getInstance().getImageLoader();

        TextView descr = (TextView) convertView.findViewById(R.id.descr);
        TextView avail = (TextView) convertView.findViewById(R.id.avail);
        TextView other = (TextView) convertView.findViewById(R.id.other);

        Leaves l = leaveList.get(position);
        descr.setText(l.getDescr());
        avail.setText(l.getAvailable());
        other.setText("Entitled : " + l.getEntitled() + "  -   Utilized : " +  l.getUtilized() );

//        if(a.getAPPRSTAT().equals("P")) {
//            thumbNail.setImageResource(R.drawable.p);
//            remark.setText("Approval Pending");
//        }else if(a.getDAY().equals("0") || a.getDAY().equals("6") ) {
//            thumbNail.setImageResource(R.drawable.w);
//            remark.setText(a.getREMARK());
//        }else if(a.getISLEAVE().equals("1")) {
//            thumbNail.setImageResource(R.drawable.l);
//            remark.setText(a.getLEAVEDESC());
//        }else if(a.getISHOLIDAY().equals("1")){
//            thumbNail.setImageResource(R.drawable.h);
//            remark.setText(a.getHOLIDAYDESC());
//        }else if(a.getINTIME().equals("") && a.getOUTTIME().equals("")){
//            thumbNail.setImageResource(R.drawable.n);
//            remark.setText("No Pay");
//        }else if(a.getINTIME().equals("") && !a.getOUTTIME().equals("")){
//            thumbNail.setImageResource(R.drawable.down);
//            remark.setText("In Time Not Recorded");
//        }else if(!a.getINTIME().equals("") && a.getOUTTIME().equals("")){
//            thumbNail.setImageResource(R.drawable.down);
//            remark.setText("Out Time Not Recorded");
//        }else{
//            thumbNail.setImageResource(R.drawable.up);
//            remark.setText(a.getREMARK());
//        }
        // thumbnail image
//        thumbNail.setImageUrl(a.getThumbnailUrl(), imageLoader);
//        thumbNail.setImageUrl("https://openclipart.org/image/800px/svg_to_png/215532/1425710397.png", imageLoader);

//        date.setText(a.getDATE());
//        time.setText("In : " + String.valueOf(a.getINTIME()) + " | " + "Out : " + String.valueOf(a.getOUTTIME()));
//        year.setText(String.valueOf(a.getDAY()));

        return convertView;
    }

}
