package com.example.safetapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class StressTracerMap extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap googleMap;
    LatLng senderBounds;
    String senderUsername,userDocumentStress;

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
        userDocumentStress = intent.getStringExtra("userDocumentStress");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        updateCamera(senderBounds);
        setUpdateListener();
    }

    private void setUpdateListener() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("updatedCoords").document(userDocumentStress).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()){
                    Log.d("Updated","got updated data");
                    double latitude  = documentSnapshot.getDouble("latitude");
                    double longitude = documentSnapshot.getDouble("longitude");
                    updateCamera(new LatLng(latitude,longitude));
                }
            }
        });
    }

    private void updateCamera(LatLng senderBounds) {
        googleMap.clear();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(senderBounds,14f));
        googleMap.addMarker(new MarkerOptions().position(senderBounds).title(senderUsername+" is in danger!"));                //TODO:change marker icon
    }
}
