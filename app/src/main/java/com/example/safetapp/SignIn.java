package com.example.safetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class SignIn extends AppCompatActivity {

    MaterialButton SignIn;
    TextView SignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        SignIn = findViewById(R.id.sign_in_btn);
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(com.example.safetapp.SignIn.this, Home.class);
                startActivity(i);
            }
        });

        SignUp = findViewById(R.id.signUp_text2);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(com.example.safetapp.SignIn.this, Choose_user_type.class);
                startActivity(i);
            }
        });
    }
}
