package com.example.safetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.spark.submitbutton.SubmitButton;

public class Later_3 extends AppCompatActivity {

    SubmitButton submitButton;
    TextInputEditText desc_edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_later_3);

        desc_edit_text = findViewById(R.id.description_edit_text);
        final String des = desc_edit_text.getText().toString().trim();

        submitButton= findViewById(R.id.submit_btn);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Later_3.this, ReportFIR.class);
                i.putExtra("Description", des);
                startActivity(i);
            }
        });
    }

    public void OnSubmit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Later_3.this);
        builder.setMessage("Do you want to file an FIR ?");
        builder.setTitle("Alert");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog,
                                int which)
            { 

            }
        });


        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog,
                                int which)
            {

                // If user click no
                // then dialog box is canceled.
                dialog.cancel();
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
