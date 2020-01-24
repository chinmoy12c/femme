package com.example.safetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Sign_up extends AppCompatActivity {

    MaterialButton SignUp;
    String User_type;
    TextView SignIn_;
    TextInputLayout user_id;
    TextInputEditText user_id_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        user_id = findViewById(R.id.aadhar_layout);
        user_id_edit = findViewById(R.id.aadhar_edit_text);

        SignUp = findViewById(R.id.sign_up_btn);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Sign_up.this, Home.class);
                startActivity(i);
            }
        });
        SignIn_=findViewById(R.id.signIn_text2);

        SignIn_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Sign_up.this, SignIn.class);
                startActivity(i);
            }
        });

        User_type=getIntent().getStringExtra("User_type");

        if(User_type == "Tourist")
        {
            user_id.setHint("PassportId");
            user_id_edit.setHint("PassportId");
        }
    }
}
