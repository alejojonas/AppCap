package com.jonasalejo.appcap;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppList extends Activity {
    private ListView list;
    ArrayList<AppRow> res;
    ArrayList<String> selectedApps = new ArrayList<String>();
    AppListAdapter adapter;

    private boolean isEdit;
    private Database dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_list);

        list = (ListView) findViewById(R.id.list);
        List<ApplicationInfo> myapps = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        res = new ArrayList<AppRow>();

        isEdit = false;
        isEdit = getIntent().getExtras().getBoolean("isEdit");

        if(isEdit == true) {
            Gson gson = new Gson();
            String[] checkedApps = gson.fromJson(getIntent().getStringExtra(Database.KEY_CHECKED), String[].class );

            for (int i = 0; i < myapps.size(); i++) {
                ApplicationInfo p = myapps.get(i);
                if ((p.flags & p.FLAG_SYSTEM) == 0 && getPackageManager().getLaunchIntentForPackage(p.packageName) != null  && !p.packageName.equals(getPackageName())) {
                    AppRow model = new AppRow();
                    model.setAppname(p.loadLabel(getPackageManager()).toString());
                    model.setIcon(p.loadIcon(getPackageManager()));
                    if(Arrays.asList(checkedApps).contains(model.getAppname())){
                        model.setSelected(true);
                    }
                    res.add(model);
                }
            }
        } else {
            System.out.println(getPackageName());

            for (int i = 0; i < myapps.size(); i++) {
                ApplicationInfo p = myapps.get(i);
                if((p.flags & p.FLAG_SYSTEM) == 0 && getPackageManager().getLaunchIntentForPackage(p.packageName) != null && !p.packageName.equals(getPackageName())){
                    AppRow model  = new AppRow();
                    model.setAppname(p.loadLabel(getPackageManager()).toString());
                    model.setIcon(p.loadIcon(getPackageManager()));

                    res.add(model);
                }
            }
        }

        adapter = new AppListAdapter(getApplicationContext(), res);
        adapter.notifyDataSetChanged();
        list.setAdapter(adapter);

        Button save = (Button) findViewById(R.id.save_btn);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(AppRow app : res){
                    if(app.isSelected()){
                        selectedApps.add(app.getAppname());
                    }
                }

                String json = new Gson().toJson(selectedApps);

                if(isEdit == true) {
                    dbHelper = new Database(getBaseContext());
                    dbHelper.open();
                    dbHelper.updatePlan(getIntent().getExtras().getString("planName"), json );

                    Intent returnToMain = new Intent(getBaseContext(), MainActivity.class);
                    v.getContext().startActivity(returnToMain);

                } else{
                    String planName = getIntent().getExtras().getString("planName");
                    Bundle bundle = new Bundle();
                    bundle.putString(Database.KEY_NAME, planName);
                    bundle.putString(Database.KEY_CHECKED, json);

                    Intent data = new Intent();
                    data.putExtras(bundle);
                    setResult(RESULT_OK, data);
                    finish();
                }

            }
        });



    }





}