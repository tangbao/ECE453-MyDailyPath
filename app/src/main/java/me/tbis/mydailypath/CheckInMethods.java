package me.tbis.mydailypath;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Zhongze Tang on 2017/11/8.
 *
 */

public class CheckInMethods {
    private DBOpenHelper helper;

    CheckInMethods(Context context){
        helper = new DBOpenHelper(context);
    }

    long addCheckin(String name, String longitude, String latitude, String time, String address){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("longitude", longitude);
        values.put("latitude", latitude);
        values.put("time", time);
        values.put("address", address);
        long _id = db.insert("checkin", null, values);
        db.close();
        return _id;
    }

    void delete(String _id){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("checkin", "_id=?", new String[]{_id});
        db.close();
    }

    List<Map<String, String>> getAllCheckin(){
        List<Map<String, String>> checkins = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("checkin", new String[]{"_id", "name", "longitude", "latitude", "time", "address"},
                null, null, null, null, null);
        while(cursor.moveToNext()){
            Map<String, String> map = new HashMap<>();
            map.put("_id", cursor.getLong(cursor.getColumnIndex("_id"))+"");
            map.put("name", cursor.getString(cursor.getColumnIndex("name")));
            map.put("time", cursor.getString(cursor.getColumnIndex("time")));
            map.put("longitude", cursor.getString(cursor.getColumnIndex("longitude")));
            map.put("latitude", cursor.getString(cursor.getColumnIndex("latitude")));
            map.put("address", cursor.getString(cursor.getColumnIndex("address")));
            checkins.add(map);
        }
        cursor.close();
        db.close();
        return checkins;
    }

    long addMaker(String name, String longitude, String latitude){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("longitude", longitude);
        values.put("latitude", latitude);
        long _id = db.insert("marker", null, values);
        db.close();
        return _id;
    }

    List<Map<String, String>> getAllMarker(){
        List<Map<String, String>> markers = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("marker", new String[]{"_id", "name", "longitude, Latitude"},
                null,null,null,null,null);
        while(cursor.moveToNext()){
            Map<String, String> map = new HashMap<>();
            map.put("_id", cursor.getLong(cursor.getColumnIndex("_id"))+"");
            map.put("name", cursor.getString(cursor.getColumnIndex("name")));
            map.put("longitude", cursor.getString(cursor.getColumnIndex("longitude")));
            map.put("latitude", cursor.getString(cursor.getColumnIndex("latitude")));
            markers.add(map);
        }
        cursor.close();
        db.close();
        return markers;
    }
}
