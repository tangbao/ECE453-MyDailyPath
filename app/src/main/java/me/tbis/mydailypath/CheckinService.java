package me.tbis.mydailypath;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static me.tbis.mydailypath.Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS;
import static me.tbis.mydailypath.Constants.UPDATE_INTERVAL_IN_MILLISECONDS;


public class CheckinService extends Service {
    private CheckInMethods checkInMethods;

    private static final String TAG = CheckinService.class.getSimpleName();
    private static final String ACTION = "me.tbis.mydailypath.TIMER";

    //Provides the entry point to the Fused Location Provider API.
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    private String mAddressOutput;
    private AddressResultReceiver mResultReceiver;

    private AlarmReceiver alarmReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onCreate(){
        checkInMethods = new CheckInMethods(getBaseContext());

        //receive address
        mResultReceiver = new AddressResultReceiver(new Handler());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        alarmReceiver = new AlarmReceiver();
        IntentFilter filter = new IntentFilter(ACTION);
        registerReceiver(alarmReceiver, filter);

        prepareLocationUpdate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.e("Service","service onStartCommand");

        if (!checkPermissions()) {
            Log.e("Check-in Service", "No permission");
        } else {
            startLocationUpdates();
        }

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int Minutes = 15*1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + Minutes;
        Intent i = new Intent(ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //close AlarmManager when the service destroy
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.cancel(pi);

    }

    private void updateLocationUI(){

    }

    private void prepareLocationUpdate(){
        //set update interval and fastest update interval
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //Creates a callback for receiving location events.
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                // Determine whether a Geocoder is available.
                if (!Geocoder.isPresent()) {
                    return;
                }
                startIntentService();
                updateLocationUI();
            }
        };

        //build locations settings request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }


    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                        updateLocationUI();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                        }

                        updateLocationUI();
                    }
                });
    }

    private void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mCurrentLocation);
        startService(intent);
    }


    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            updateLocationUI();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //showToast(getString(R.string.address_found));
                Log.d(TAG, getString(R.string.address_found));
            }

        }
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Service", "received");
            Log.e("Current Location", mCurrentLocation.toString());
            Log.e("Current Address", mAddressOutput);

            startCheckIn();

            //start service again
            Intent i = new Intent(context, CheckinService.class);
            context.startService(i);
        }
    }

    private void startCheckIn(){
        Map<String, String> map = checkInMethods.findLocation(mCurrentLocation.getLongitude(),
                mCurrentLocation.getLatitude());
        long location_id = Long.parseLong(map.get("_id"));

        String location_name = "Auto check-in";
        if(location_id != -1){
            location_name = map.get("name");
        }else{
            location_id = checkInMethods.addLocation(location_name, mCurrentLocation.getLongitude(),
                    mCurrentLocation.getLatitude(), mAddressOutput);
        }

        Log.d("startCheckin","yes");
        checkIn(location_name, location_id);
    }

    private void checkIn(String customName, long location_id){
        Log.d("Checkin","yes");
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        String date = sDateFormat.format(new java.util.Date());

        long _id = checkInMethods.addCheckin(date, mCurrentLocation.getLongitude() + "",
                mCurrentLocation.getLatitude() + "", location_id);
        UpdateListView(_id, customName, date);
    }

    private void UpdateListView(long _id, String customName, String date){
        Intent intent = new Intent(Constants.ACTION_UPDATEUI);
        intent.putExtra("_id", _id);
        intent.putExtra("name", customName);
        intent.putExtra("time", date);
        intent.putExtra("latitude", mCurrentLocation.getLatitude()+"");
        intent.putExtra("longitude", mCurrentLocation.getLongitude()+"");
        intent.putExtra("address", mAddressOutput);
        sendBroadcast(intent);
    }

}
