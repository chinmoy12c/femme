package com.example.safetapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Report_Crime extends FragmentActivity implements OnMapReadyCallback,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{


    private GoogleMap mMap;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    RadioButton current, later;
    Button Submit;
    LatLng latLng, centre;
    String Address, city;
    TextView LOcationAddress;
    ImageView img_pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report__crime);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        current = findViewById(R.id.current_crime);
        later = findViewById(R.id.later_crime);
        Submit = findViewById(R.id.submit_button);
        LOcationAddress = findViewById(R.id.location_address);
        img_pin = findViewById(R.id.imgLocationPinUp);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(current.isChecked())
                {
                    Intent i = new Intent(Report_Crime.this, CurrentCrime.class);
                    i.putExtra("Address", Address);
                    i.putExtra("City", city);
                    i.putExtra("Latitude", latLng.latitude);
                    i.putExtra("Longitude", latLng.longitude);
                    startActivity(i);
                }

                else if(later.isChecked())
                {
                    Intent i = new Intent(Report_Crime.this, LaterActivity.class);
                    startActivity(i);
                }

                else
                {
                    Toast.makeText(Report_Crime.this, "Please choose a valid option", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                mMap.setBuildingsEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.setBuildingsEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {


        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        mLastLocation = location;
        if (location == null) {
//            mCurrLocationMarker.remove();
            Toast.makeText(this, "Error detecting Location!", Toast.LENGTH_SHORT).show();
        }

        else {

            if(mCurrLocationMarker == null) {
                //Place current location marker
               latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mCurrLocationMarker = mMap.addMarker(markerOptions);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

            }

            else
            {
                mCurrLocationMarker.setPosition(latLng);
            }

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            Double lats = latLng.latitude;
            Double longs = latLng.longitude;

            try {
                List<Address> myAddress = geocoder.getFromLocation(lats, longs,1);
                Address = myAddress.get(0).getAddressLine(0);
                city = myAddress.get(0).getLocality();
                LOcationAddress.setText(Address +" ,"+ city);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                centre = mMap.getCameraPosition().target;

                img_pin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mCurrLocationMarker.remove();
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(centre);
                        markerOptions.title("Chosen Position");
                        markerOptions.draggable(true);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        mCurrLocationMarker = mMap.addMarker(markerOptions);
                       // img_pin.setVisibility(View.GONE);
                        latLng = mCurrLocationMarker.getPosition();
                        LOcationAddress.setText(getStringAddress(centre.latitude, centre.longitude));
                    }
                });
//
            }
        });



    }

    private String getStringAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> myAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude,1);
            Address = myAddress.get(0).getAddressLine(0);
            city = myAddress.get(0).getLocality();
            LOcationAddress.setText(Address +" ,"+ city);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Address + " " + city;
    }


}
