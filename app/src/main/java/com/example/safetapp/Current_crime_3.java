package com.example.safetapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
                submitReport();
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
                            startActivity(new Intent(Current_crime_3.this,ReportFIR.class));
                        }
                        else{
                            startActivity(new Intent(Current_crime_3.this,ReportFIR.class));
                        }
                    }
                });

        builder.show();
    }
}
