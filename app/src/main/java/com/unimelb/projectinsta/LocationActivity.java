package com.unimelb.projectinsta;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class LocationActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location mLocation;
    private  double latitude, longitude;
    byte[] bitmapArray;
    Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Intent shareContent = getIntent();
        bitmapArray = shareContent.getByteArrayExtra("shareImage");
        imageBitmap = BitmapFactory.decodeByteArray(bitmapArray,0,bitmapArray.length);
        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.location);
        mapFragment.getMapAsync((OnMapReadyCallback) this);*/


    }
}
