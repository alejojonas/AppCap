package com.jonasalejo.appcap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class PlanAdapter extends ArrayAdapter {

    List data;
    Context context;
    int layoutResID;

    private Database dbHelper;

    public PlanAdapter(Context context, int layoutResourceId, List data) {
        super(context, layoutResourceId, data);
        this.data=data;
        this.context=context;
        this.layoutResID=layoutResourceId;

        dbHelper = new Database(getContext());
        dbHelper.open();

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        NewsHolder holder = null;
        View row = convertView;
        holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResID, parent, false);

            holder = new NewsHolder();

            holder.itemName = (TextView)row.findViewById(R.id.itemname);
            holder.deleteBtn=(Button)row.findViewById(R.id.delete_btn);
            holder.editBtn=(Button)row.findViewById(R.id.edit_btn);
            holder.startBtn=(Button)row.findViewById(R.id.start_btn);
            row.setTag(holder);
        }
        else
        {
            holder = (NewsHolder)row.getTag();
        }

        final PlanRow itemdata = (PlanRow) data.get(position);
        holder.itemName.setText(itemdata.getItemName());

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dbHelper.deletePlan(itemdata.getItemName());
                Intent i = new Intent(getContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                v.getContext().startActivity(i);
            }
        });

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isEdit", true);
                bundle.putString (Database.KEY_CHECKED, dbHelper.fetchCheckedApps(itemdata.getItemName()));
                bundle.putString ("planName", itemdata.getItemName());
                Intent i = new Intent(getContext(), AppList.class);
                i.putExtras(bundle);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                v.getContext().startActivity(i);
            }
        });

        holder.startBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString ("planName", itemdata.getItemName());
                Intent i = new Intent(getContext(), Timer.class);
                i.putExtras(bundle);
                v.getContext().startActivity(i);
            }
        });

        return row;

    }

    static class NewsHolder{
        TextView itemName;
        Button deleteBtn;
        Button editBtn;
        Button startBtn;
    }



}