package me.tbis.mydailypath;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Zhongze Tang on 2017/11/8.
 *
 */

public class CheckInMethods {
    private DBOpenHelper helper;

    CheckInMethods(Context context){
        helper = new DBOpenHelper(context);
    }

    void add(String name, String longitude, String latitude, String time, String address){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("longitude", longitude);
        values.put("latitude", latitude);
        values.put("time", time);
        values.put("address", address);
        db.insert("checkin", null, values);
        db.close();
    }


}
