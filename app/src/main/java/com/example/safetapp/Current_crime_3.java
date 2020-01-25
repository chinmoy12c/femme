package com.example.safetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.spark.submitbutton.SubmitButton;

public class Current_crime_3 extends AppCompatActivity {
SubmitButton submitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_crime_3);

        submitButton = findViewById(R.id.submit_btn);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Current_crime_3.this, Home.class);
                startActivity(i);
            }
        });
    }
}
