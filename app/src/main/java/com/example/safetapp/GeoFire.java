package com.example.safetapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GeoFire extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GeoQueryDataEventListener {

    private GoogleMap mMap;
    private TextToSpeech mTTS;

    private static final int MY_PERMISSION_REQUEST_CODE = 240;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 260;

    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Location location;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;
    private List <LatLng>dangerousAreaLocation;
    int colour[]=new int[4];

    VerticalSeekBar seekBar;
    Vibrator vibrator;

    Marker myLocation;
    DatabaseReference ref;
    com.firebase.geofire.GeoFire geoFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fire);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        dangerousAreaLocation=new ArrayList<>();

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        seekBar = findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(i),2000,null);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ref = FirebaseDatabase.getInstance().getReference("My Location");
        geoFire = new com.firebase.geofire.GeoFire(ref);


        setUpLocation();
        initArea();
    }

    private void initArea() {
        dangerousAreaLocation.add(new LatLng(28.4728,77.4820));
        dangerousAreaLocation.add(new LatLng(28.5703,77.3218));
        dangerousAreaLocation.add(new LatLng(28.5221,77.2102));
        dangerousAreaLocation.add(new LatLng(28.6304,77.2177));
        colour[0]=0x44ff0000;
        colour[1]=0x44008000;
        colour[2]=0x440397F7;
        colour[3]=0x449355F2;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSION_REQUEST_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(checkPlayServices())
                    {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
                break;


        }
    }

    private void setUpLocation()
    {
        if((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ))

        {

            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
            } ,MY_PERMISSION_REQUEST_CODE);
}


        else
        {
            if(checkPlayServices())
            {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
    }

    private void displayLocation() {

        if((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ))
            return;

        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(location !=null)
        {
            final double Latitude = location.getLatitude();
            final double Longitude = location.getLongitude();

            //adding to firebase
            geoFire.setLocation("You", new GeoLocation(Latitude, Longitude), new com.firebase.geofire.GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {


                    //adding marker
                    if(myLocation!=null)
                        myLocation.remove();  //remove old marker
                    myLocation = mMap.addMarker(new MarkerOptions()
                                     .position(new LatLng(Latitude, Longitude))
                                      .title("You"));

                    //move camera to this location
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Latitude, Longitude),12.0f));
                }
            });


            Toast.makeText(this, String.format("Your location has been changed to : %f, %f", Latitude, Longitude), Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "Error detecting your location", Toast.LENGTH_SHORT).show();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {

        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        googleApiClient.connect();
    }

    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode!= ConnectionResult.SUCCESS)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Toast.makeText(this, "This device is not supported!", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        int i=0;

        //Create Dangerous area

       for(LatLng latLng : dangerousAreaLocation)
       {    while(i<4) {
           mMap.addCircle(new CircleOptions()
                   .center(latLng)
                   .radius(2500)   //1km
                   .strokeColor(Color.BLACK)
                   .fillColor(colour[i])
                   .strokeWidth(3.0f)
           );
           i++;
           break;
       }

           GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude),1f);
           geoQuery.addGeoQueryDataEventListener(GeoFire.this);
       }

        //adding geoquery
        //1f=10000m=1km

    }

    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }

    private void sendNotification(String title, String content) {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content);

        NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent i = new Intent(this, GeoFire.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;

        notificationManager.notify(new Random().nextInt(), notification);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {

        if((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ))
            return;
LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);

    }

    @Override
    public void onConnectionSuspended(int i) {

        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location l) {
        location = l;
        displayLocation();

    }

    @Override
    public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
        sendNotification("Safety App", String.format(("%s Entered dangerous Area"), dataSnapshot));
        String text ="Alert! You entered dangerous area.Be careful.";
        vibrator.vibrate(2000);
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onDataExited(DataSnapshot dataSnapshot) {
        sendNotification("Safety App", String.format(("%s Exited dangerous Area"), dataSnapshot));
        String text ="Thank God! You exited dangerous area.You are safe now.";
        vibrator.vibrate(2000);
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);

    }

    @Override
    public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
        Toast.makeText(GeoFire.this, String.format("%s Moved within the dangerous area [%f, %f]", dataSnapshot, location.latitude, location.longitude), Toast.LENGTH_SHORT).show();
        String text ="Alert! You are in dangerous area.Be careful.";
        vibrator.vibrate(2000);
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        Log.e("ERROR", ""+error);
    }
}
