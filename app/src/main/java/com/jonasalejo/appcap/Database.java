package com.jonasalejo.appcap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Jonas on 9/24/2014.
 */
public class Database {

    public static final String KEY_NAME = "plans_name";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_CHECKED = "checked_apps";

    private static final String DATABASE_NAME = "plansDb";
    private static final String DATABASE_TABLE = "planTable";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "create table planTable (_id integer primary key autoincrement, "
                    + "plans_name text not null, checked_apps text not null);";

    private DbHelper myDbHelper;
    private final Context myCtx;
    private SQLiteDatabase myDb;

    private static class DbHelper extends SQLiteOpenHelper{
        DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
//                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public Database(Context ctx){
        myCtx = ctx;
    }

    public Database open() throws SQLException {
        myDbHelper = new DbHelper(myCtx);
        myDb = myDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        myDbHelper.close();
    }

    public long createPlan(String name, String checkedApps) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_CHECKED, checkedApps);

        return myDb.insert(DATABASE_TABLE, null, initialValues);
    }


    public boolean deletePlan(String name) {
        return myDb.delete(DATABASE_TABLE, KEY_NAME + "= '" + name + "'", null) > 0;

    }

    public String fetchCheckedApps(String name) {
       Cursor c = myDb.rawQuery("SELECT " + KEY_CHECKED + " FROM " + DATABASE_TABLE + " WHERE " + KEY_NAME + "= '" + name + "'", null);
       c.moveToFirst();
       return c.getString(0);
    }

    public ArrayList<String> fetchAllPlanNames(){
        ArrayList<String> arrayList = new ArrayList<String>();
        int i = 0;
        Cursor c = myDb.rawQuery("SELECT " + KEY_NAME + " FROM " + DATABASE_TABLE , null);
        c.moveToFirst();
        while(c.isAfterLast() == false){
            arrayList.add(i, c.getString(0));
            i++;
            c.moveToNext();
        }

        return arrayList;
    }

    public Cursor fetchAllPlans() {
        return myDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME,
                KEY_CHECKED}, null, null, null, null, null);
    }

    public Cursor fetchAllPlans(long rowId) throws SQLException {
        Cursor mCursor =
                myDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                                KEY_NAME, KEY_CHECKED}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public boolean updatePlan(String name, String checkedApps) {
        ContentValues args = new ContentValues();
        args.put(KEY_CHECKED, checkedApps);

        return myDb.update(DATABASE_TABLE, args, KEY_NAME + "= '" + name + "'", null) > 0;
    }

}
