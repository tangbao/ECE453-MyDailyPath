//package me.tbis.mydailypath;
//
//import android.app.Service;
//import android.content.Intent;
//import android.location.Geocoder;
//import android.location.Location;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Looper;
//import android.os.ResultReceiver;
//import android.widget.Toast;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationSettingsRequest;
//
//import java.text.SimpleDateFormat;
//import java.util.Locale;
//
//
//public class CheckinService extends Service {
//    private CheckInMethods checkInMethods;
//    private String mAddressOutput;
//    private AddressResultReceiver mResultReceiver;
//    private Location mCurrentLocation;
//    private FusedLocationProviderClient mFusedLocationClient;
//    private LocationRequest mLocationRequest;
//    private LocationCallback mLocationCallback;
//
//    public CheckinService() {
//        checkInMethods = new CheckInMethods(getBaseContext());
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        //throw new UnsupportedOperationException("Not yet implemented");
//        return null;
//    }
//
//    @SuppressWarnings("MissingPermission")
//    @Override
//    public void onCreate(){
//        mResultReceiver = new AddressResultReceiver(new Handler());
//        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
//                mLocationCallback, Looper.myLooper());
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId){
//        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
//        String date = sDateFormat.format(new java.util.Date());
//        long _id = checkInMethods.addCheckin("Auto Check-in", mCurrentLocation.getLongitude() + "", mCurrentLocation.getLatitude() + "",
//                date, mAddressOutput);
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    private void prepareLocationUpdate(){
//        //set update interval and fastest update interval
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
//        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        //Creates a callback for receiving location events.
//        mLocationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//
//                mCurrentLocation = locationResult.getLastLocation();
//                // Determine whether a Geocoder is available.
//                if (!Geocoder.isPresent()) {
//                    showSnackbar(getString(R.string.no_geocoder_available));
//                    return;
//                }
//
//                startIntentService();
//                updateLocationUI();
//            }
//        };
//
//        //build locations settings request
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
//        builder.addLocationRequest(mLocationRequest);
//        mLocationSettingsRequest = builder.build();
//    }
//
//    private void updateLocationUI(){
//
//    }
//
//    /**
//     * Receiver for data sent from FetchAddressIntentService.
//     */
//    private class AddressResultReceiver extends ResultReceiver {
//        AddressResultReceiver(Handler handler) {
//            super(handler);
//        }
//
//        /**
//         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
//         */
//        @Override
//        protected void onReceiveResult(int resultCode, Bundle resultData) {
//
//            // Display the address string or an error message sent from the intent service.
//            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
//            updateLocationUI();
//
//            // Show a toast message if an address was found.
//            if (resultCode == Constants.SUCCESS_RESULT) {
//                Toast.makeText(getBaseContext(),"Auto check-in successfully.",Toast.LENGTH_LONG).show();
//            }
//
//        }
//    }
//}
