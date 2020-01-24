package com.example.safetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class StressTracerMap extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap googleMap;
    LatLng senderBounds;
    String senderUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stress_tracer_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMapAlarm);
        if (mapFragment != null){
            mapFragment.getMapAsync(this);
        }

        Intent intent = this.getIntent();
        senderBounds = new LatLng(intent.getDoubleExtra("latitude",45.5),intent.getDoubleExtra("longitude",56.3));
        senderUsername = intent.getStringExtra("username");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        updateCamera(senderBounds);
    }

    private void updateCamera(LatLng senderBounds) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(senderBounds,14f));
        googleMap.addMarker(new MarkerOptions().position(senderBounds).title(senderUsername+" is in danger!"));                //TODO:change marker icon
    }
}
