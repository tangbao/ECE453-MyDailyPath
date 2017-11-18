package me.tbis.mydailypath;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Zhongze Tang on 2017/11/8.
 *
 */

public class DBOpenHelper extends SQLiteOpenHelper{

    DBOpenHelper(Context context) {
        super(context, "mydailypath.db", null, 1);
        //context, name of db file, cursor factory, version of database
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table checkin  (_id integer primary key autoincrement, time text, longitude text, latitude text, location_id integer) ");
        db.execSQL("create table location (_id integer primary key autoincrement, name text, longitude text, latitude text, address text) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //nothing to do
    }

}
