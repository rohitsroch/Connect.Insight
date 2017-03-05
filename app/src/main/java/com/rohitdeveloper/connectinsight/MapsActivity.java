package com.rohitdeveloper.connectinsight;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private ArrayList<Person> bestSimilarPerson;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        bestSimilarPerson = new ArrayList<Person>();
        bestSimilarPerson = (ArrayList<Person>) getIntent().getSerializableExtra("BestSimilarPerson");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        if (isNetworkAvailable()) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            new LovelyStandardDialog(MapsActivity.this)
                    .setTopColorRes(R.color.indigo)
                    .setButtonsColorRes(R.color.colorRed)
                    .setIcon(R.drawable.ic_signal_wifi_off)
                    .setTitle("No Internet Connection!")
                    .setPositiveButton("Cancel", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                             finish();
                        }
                    })
                    .show();
        }

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }else{
            mMap.setMyLocationEnabled(true);
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        LatLng LAT_LNG_CAMERA_VIEW=null;
        boolean flag=true;


        // Add a marker
        for(Person person:bestSimilarPerson){
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            ArrayList<Address> addresses=null;
            try {
                addresses = (ArrayList<Address>) geocoder.getFromLocationName(person.getPerson_location(),4);
                if(!addresses.isEmpty())
                {   Log.d(TAG,"Address:Sucess");
                    LatLng location_latLng= new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());

                    if(flag){
                        flag=false;
                        LAT_LNG_CAMERA_VIEW=new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                    }
                    mMap.addMarker(new MarkerOptions().position(location_latLng).title(person.getPerson_name()).snippet(person.getPerson_screen_name()));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(LAT_LNG_CAMERA_VIEW)      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }



}
