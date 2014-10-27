package com.jonasalejo.appcap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AppListAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    public ArrayList<AppRow> data;

    public AppListAdapter(Context c, ArrayList<AppRow> arraylist) {
        this.ctx = c;
        this.data = arraylist;
    }

    public ArrayList<AppRow> getData(){
        return data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) ctx.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.app_row, parent, false);
        final CheckBox checks = (CheckBox) itemView.findViewById(R.id.b);
        final TextView setappname = (TextView) itemView.findViewById(R.id.plan_name);
        final ImageView setappicon = (ImageView) itemView.findViewById(R.id.c);
        checks.setChecked(data.get(position).isSelected());

        AppRow obj = data.get(position);
        setappname.setText(obj.getAppname());
        setappicon.setImageDrawable(obj.getIcon());
        checks.setTag(position);

        checks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();
                AppRow obj = data.get(getPosition);
                data.get(position).setSelected(isChecked);
            }
        });

        return itemView;
    }

}