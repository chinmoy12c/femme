package com.example.safetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

public class Sign_in_up extends AppCompatActivity {
MaterialButton SignIn, SignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_up);

        SignIn = findViewById(R.id.sign_in);

        SignUp = findViewById(R.id.sign_up);

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Sign_in_up.this, com.example.safetapp.SignIn.class);
                startActivity(i);
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Sign_in_up.this, Sign_up.class);
                startActivity(i);
            }
        });
    }
}
