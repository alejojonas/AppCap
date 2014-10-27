package com.jonasalejo.appcap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private static final int ACTIVITY_CREATE  = 0;
    int count = 0;

    private Database dbHelper;
    private Cursor planCursor;

    SwipeListView swipelistview;
    PlanAdapter adapter;
    public List<PlanRow> itemData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        swipelistview = (SwipeListView)findViewById(R.id.example_swipe_lv_list);
        itemData = new ArrayList<PlanRow>();
        adapter = new PlanAdapter(this, R.layout.plan_row, itemData);

        dbHelper = new Database(this);
        dbHelper.open();
        fillData();

        swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
                swipelistview.openAnimate(position); //when you touch front view it will open
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
                swipelistview.closeAnimate(position);//when you touch back view it will close
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
                count++;
                if(count > 1){
                    swipelistview.closeOpenedItems();
                }
            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("swipe", String.format("onStartClose %d", position));
                count--;
            }

            @Override
            public void onClickFrontView(int position) {
                Log.d("swipe", String.format("onClickFrontView %d", position));
                onStartOpen(position, 0, true);
                swipelistview.openAnimate(position); //when you touch front view it will open

            }

            @Override
            public void onClickBackView(int position) {
                Log.d("swipe", String.format("onClickBackView %d", position));
                onStartClose(position, true);
                swipelistview.closeAnimate(position);//when you touch back view it will close
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {

            }

        });

        swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH); // there are five swiping modes
        swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL); //there are four swipe actions
        swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
        swipelistview.setOffsetLeft(convertDpToPixel(0f)); // left side offset
        swipelistview.setOffsetRight(convertDpToPixel(0f)); // right side offset
        swipelistview.setAnimationTime(200); // Animation time
        swipelistview.setSwipeOpenOnLongPress(false); // enable or disable SwipeOpenOnLongPress
        swipelistview.setAdapter(adapter);

        Button addPlan = (Button) findViewById(R.id.add_plan_btn);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Prevention Plan Name ");

        addPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final EditText input = new EditText(getBaseContext());
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                            Intent newPlan = new Intent(getBaseContext(), AppList.class);
                            newPlan.putExtra("planName", input.getText().toString());
                            startActivityForResult(newPlan, ACTIVITY_CREATE);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                final AlertDialog dialog = alert.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        // Check if edittext is empty
                        if (TextUtils.isEmpty(s)) {
                            // Disable ok button
                            dialog.getButton( AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                        // Check if edittext already exist in the list of plans
                        else if(planExists(s.toString().trim())){
                            dialog.getButton( AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                        else {
                            // Something into edit text. Enable the button.
                             dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }

                    }
                });
            }
        });

    }

    private boolean planExists(String s){
        for(int i = 0; i < dbHelper.fetchAllPlanNames().size(); i++){
            if(s.equalsIgnoreCase(dbHelper.fetchAllPlanNames().get(i))){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case ACTIVITY_CREATE:
                if (resultCode == RESULT_OK){
                    Bundle extras = data.getExtras();
                    String name = extras.getString(Database.KEY_NAME);
                    String checkedApps = extras.getString(Database.KEY_CHECKED);
                    dbHelper.createPlan(name, checkedApps);
                    fillData();
                    break;
                }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void fillData() {
        planCursor = dbHelper.fetchAllPlans();
        startManagingCursor(planCursor);
        itemData.clear();
        while(planCursor.moveToNext()){
            itemData.add(new PlanRow(planCursor.getString(planCursor.getColumnIndexOrThrow(Database.KEY_NAME))));
        }
        adapter.notifyDataSetChanged();
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

}