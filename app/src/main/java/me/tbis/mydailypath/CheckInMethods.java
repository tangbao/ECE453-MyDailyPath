package me.tbis.mydailypath;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zhongze Tang on 2017/11/8.
 *
 */

class CheckInMethods {
    private DBOpenHelper helper;

    CheckInMethods(Context context){
        helper = new DBOpenHelper(context);
    }

    long addCheckin(String time, String longitude, String latitude, long location_id){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("time", time);
        values.put("longitude", longitude);
        values.put("latitude", latitude);
        values.put("location_id", location_id);
        long _id = db.insert("checkin", null, values);
        db.close();
        return _id;
    }

    void delCheckin(String _id){
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("checkin", new String[]{"location_id"}, "_id=?", new String[]{_id}, null, null, null);
        String location_id = "";
        while(cursor.moveToNext()){
            location_id = cursor.getLong(cursor.getColumnIndex("location_id"))+"";
        }
        cursor = db.query("checkin", new String[]{_id}, "location_id=?", new String[]{location_id}, null, null,null);

        if(cursor.getCount() == 1) {
            db.delete("location", "_id=?", new String[]{location_id});
        }

        db.delete("checkin", "_id=?", new String[]{_id});
        cursor.close();
        db.close();
    }

    List<Map<String, String>> getAllCheckin(){
        List<Map<String, String>> checkins = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT checkin._id, checkin.time, checkin.longitude, " +
                "checkin.latitude, checkin.location_id, location.name, location.address FROM checkin " +
                "JOIN location WHERE checkin.location_id = location._id", null);
        while(cursor.moveToNext()){
            Map<String, String> map = new HashMap<>();
            map.put("_id", cursor.getLong(cursor.getColumnIndex("_id"))+"");
            map.put("name", cursor.getString(cursor.getColumnIndex("name")));
            map.put("time", cursor.getString(cursor.getColumnIndex("time")));
            map.put("longitude", cursor.getString(cursor.getColumnIndex("longitude")));
            map.put("latitude", cursor.getString(cursor.getColumnIndex("latitude")));
            map.put("location_id", cursor.getLong(cursor.getColumnIndex("location_id"))+"");
            map.put("address", cursor.getString(cursor.getColumnIndex("address")));
            checkins.add(map);
        }
        cursor.close();
        db.close();
        return checkins;
    }

    Map<String, String> findLocation(double long1, double lati1){
        Map<String, String> map = new HashMap<>();
        String location_id = "-1";
        String location_name = "";
        List<Map<String, String>> locations = getAllLocation();
        for(int i = 0; i < locations.size(); i++){
            if(getDistance(long1, lati1, Double.parseDouble(locations.get(i).get("longitude")), Double.parseDouble(locations.get(i).get("latitude"))) <= 30.0){
                location_id = locations.get(i).get("_id");
                location_name = locations.get(i).get("name");
                break;
            }
        }
        map.put("_id", location_id);
        map.put("name", location_name);
        return map;
    }

    long addLocation(String name, double longitude, double latitude, String address){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("longitude", longitude);
        values.put("latitude", latitude);
        values.put("address", address);
        long _id = db.insert("location", null, values);
        db.close();
        return _id;
    }

    List<Map<String, String>> getAllLocation(){
        List<Map<String, String>> locations = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("location", new String[]{"_id", "name", "longitude, Latitude", "address"},
                null,null,null,null,null);
        while(cursor.moveToNext()){
            Map<String, String> map = new HashMap<>();
            map.put("_id", cursor.getLong(cursor.getColumnIndex("_id"))+"");
            map.put("name", cursor.getString(cursor.getColumnIndex("name")));
            map.put("longitude", cursor.getString(cursor.getColumnIndex("longitude")));
            map.put("latitude", cursor.getString(cursor.getColumnIndex("latitude")));
            map.put("address", cursor.getString(cursor.getColumnIndex("address")));
            locations.add(map);
        }
        cursor.close();
        db.close();
        return locations;
    }

    String getLastTime(String _id){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT time FROM checkin WHERE location_id = " + _id +
                " ORDER BY _id DESC LIMIT 0, 1",null);
        String time = "Not check-in here yet.";
        while(cursor.moveToNext()){
            time = "Last check-in time: " + cursor.getString(cursor.getColumnIndex("time"));
        }
        cursor.close();
        db.close();
        return time;
    }

    double getDistance(double long1, double lat1, double long2, double lat2) {
        double a, b, R;
        R = 6378137; // 地球半径
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - long2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2   * R
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
        return d;
    }
}
