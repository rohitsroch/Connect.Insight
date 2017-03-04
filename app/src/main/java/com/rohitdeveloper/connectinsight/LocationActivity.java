package com.rohitdeveloper.connectinsight;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LocationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LocationActivity";

    private Button enableLocation;
    private TextView enableLocationDescription;
    private  CoordinatorLayout coordinatorLayout;


    //For getting user location
    private CurrentGeoLocation geoLocation;
    double longitude;
    double latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        enableLocationDescription=(TextView)findViewById(R.id.id_enable_location_description);
        String description = "Please "+"<font color='#cc0000'>enable location services</font>"+" so that we find people near you";
        enableLocationDescription.setText(fromHtml(description));
        enableLocation=(Button) findViewById(R.id.id_enable_location);
        enableLocation.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.id_enable_location){
            if( isNetworkAvailable() ) {
                geoLocation = new CurrentGeoLocation(LocationActivity.this);
                if (geoLocation.canGetLocation()) {
                    latitude = geoLocation.getLatitude();
                    longitude = geoLocation.getLongitude();
                    Log.d(TAG, latitude + " " + longitude);
                    Intent twitterLoginIntent = new Intent(LocationActivity.this, TwitterLoginActivity.class);
                    twitterLoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    twitterLoginIntent.putExtra("Latitude", latitude);
                    twitterLoginIntent.putExtra("Longitude", longitude);
                    startActivity(twitterLoginIntent);
                } else {
                    Toast.makeText(LocationActivity.this, "Turn On GPS", Toast.LENGTH_LONG).show();
                }
            }else{
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                  if(isNetworkAvailable()){
                                      Toast.makeText(LocationActivity.this,"Connected!",Toast.LENGTH_LONG).show();
                                  }
                            }
                        });
                // Changing message text color
                snackbar.setActionTextColor(Color.RED);
                snackbar.show();
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}
