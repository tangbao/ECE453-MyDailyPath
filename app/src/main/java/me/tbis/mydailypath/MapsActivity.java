package me.tbis.mydailypath;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , MyDialog.Callback{

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CheckInMethods checkInMethods;
    private Marker markerNew;
    private AddressResultReceiver mResultReceiver;
    private String mAddressOutput;

    //add location
    private String custom_name;
    private LatLng markerLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        checkInMethods = new CheckInMethods(MapsActivity.this);

        mResultReceiver = new AddressResultReceiver(new Handler());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.isDraggable()){
                    custom_name = "";
                    markerNew = marker;
                    markerLL = marker.getPosition();

                    Log.e("onMarkerClick:", marker.getPosition().toString());

                    MyDialog myDialog = new MyDialog();
                    myDialog.show(getFragmentManager());
                }
                return true;
            }
        });


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            //TODO 这里有一个不加listener position就不更新的玄学问题
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
            }
        });





        // Move camera to where you are and move the camera
        Intent intent = getIntent();
        LatLng loc_now = new LatLng(intent.getDoubleExtra("weidu",0), intent.getDoubleExtra("jingdu",0));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc_now, 10));

        List<Map<String, String>> list = checkInMethods.getAllLocation();
        for(int i = 0; i<list.size(); i++){
            LatLng latLng = new LatLng(Double.parseDouble(list.get(i).get("latitude")), Double.parseDouble(list.get(i).get("longitude")));
            String title = "Unnamed Location";
            if(!list.get(i).get("name").equals("")){
                title = list.get(i).get(("name"));
            }
            String time = checkInMethods.getLastTime(list.get(i).get("_id"));
            Marker markerInMap = mMap.addMarker(new MarkerOptions().position(latLng).title(title).snippet(time));
            markerInMap.setTag(list.get(i).get("_id"));
        }
    }

    public void onDialogClick(String customName){
        if (!customName.isEmpty()){
            custom_name = customName;
            markerNew.setTitle(custom_name);
            markerNew.setDraggable(false);
            Log.e("onDialogClick: ", markerNew.getPosition().toString());
            startIntentService(markerLL.longitude, markerLL.latitude);
        }else{
            Toast.makeText(this, "The marker must have a name, or it will not be saved",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void startIntentService(double longitude, double latitude) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_LONGITUDE, longitude);
        intent.putExtra(Constants.LOCATION_DATA_LATITUDE, latitude);
        startService(intent);
    }

    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                Log.d("before save:", markerLL.toString());
                checkInMethods.addLocation(custom_name, markerLL.longitude, markerLL.latitude, mAddressOutput);
                Log.d(TAG, getString(R.string.address_found));
            }else{
                Toast.makeText(MapsActivity.this, getString(R.string.no_address_found) ,Toast.LENGTH_LONG).show();
            }
        }
    }
}
