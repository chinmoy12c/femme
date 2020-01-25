package com.example.safetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Later_2 extends AppCompatActivity {
    Button Later_next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_later_2);

        Later_next = findViewById(R.id.later_crime_next);
        Later_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Later_2.this, Later_3.class);
                startActivity(i);
            }
        });
    }
}
