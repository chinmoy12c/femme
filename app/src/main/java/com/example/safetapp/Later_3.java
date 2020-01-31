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
                submitReport();
                Intent i = new Intent(Later_3.this, ReportFIR.class);
                i.putExtra("Description", des);
                startActivity(i);
            }
        });
    }

    private void submitReport() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to report an FIR?")
                .setItems(new String[]{"Yes", "No"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0)
                        {
                                startActivity(new Intent(Later_3.this,ReportFIR.class));
                        }
                        else{
                            startActivity(new Intent(Later_3.this,ReportFIR.class));
                        }
                    }
                });

        builder.show();
    }
}
