package com.example.safetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CurrentCrime extends AppCompatActivity {

    Button next_page;
    TextInputEditText coordiantes, place, city, time;
    double Coordiantes_lat;
    double Coordiantes_long;
    String Place;
    String City;
    String Time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_crime);

        next_page = findViewById(R.id.current_crim_next);
        next_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =  new Intent(CurrentCrime.this, Crime_current_2.class);
                startActivity(i);
            }
        });

        Coordiantes_lat = getIntent().getDoubleExtra("Latitude",00.00);
        Coordiantes_long = getIntent().getDoubleExtra("Longitude",00.00);
        Place = getIntent().getStringExtra("Address");
        City = getIntent().getStringExtra("City");


        coordiantes = findViewById(R.id.gps_coordinates_edit_text);
        place = findViewById(R.id.place_edit_text);
        city = findViewById(R.id.city_edit_text);
        time= findViewById(R.id.time_edit_text);

        coordiantes.setText(Coordiantes_lat+" "+Coordiantes_long);
        place.setText(Place);
        city.setText(City);
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Time=sdf.format(new Date());
        time.setText(Time);


    }
}
