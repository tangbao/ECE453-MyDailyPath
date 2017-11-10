package me.tbis.mydailypath;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private CheckInMethods checkInMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        checkInMethods = new CheckInMethods(MapsActivity.this);

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

        // Move camera to where you are and move the camera
        Intent intent = getIntent();
        LatLng loc_now = new LatLng(intent.getDoubleExtra("weidu",0), intent.getDoubleExtra("jingdu",0));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc_now, 10));

        List<Map<String, String>> list1 = checkInMethods.getAllCheckin();
        for(int i = 0; i<list1.size(); i++){
            LatLng latLng = new LatLng(Double.parseDouble(list1.get(i).get("latitude")), Double.parseDouble(list1.get(i).get("longitude")));
            mMap.addMarker(new MarkerOptions().position(latLng).title(list1.get(i).get("name")));
        }


        List<Map<String, String>> list2 = checkInMethods.getAllMarker();
        for(int i = 0; i<list2.size(); i++){
            LatLng latLng = new LatLng(Double.parseDouble(list2.get(i).get("latitude")), Double.parseDouble(list2.get(i).get("longitude")));
            mMap.addMarker(new MarkerOptions().position(latLng).title(list2.get(i).get("name")));
        }
    }
}
