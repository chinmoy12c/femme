package com.example.safetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
 MaterialButton start;
    private SensorManager sm;
    MediaPlayer player;
    private float acelVal;  //current value of acc and gravity
    private float acelLast;  //last value of acc and gravity
    private float acelShake;   //acc value differ from gravity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.start_btn);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Sign_in_up.class);
                startActivity(i);
            }
        });

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert sm != null;
        sm.registerListener(sensorEventListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);

        acelVal = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;
        acelShake = 0.00f;

    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            acelLast = acelVal;
            acelVal = (float)Math.sqrt((double)(x*x+y*y+z*z));
            float delta = acelVal-acelLast;
            acelShake= acelShake*0.9f +delta;

            if(acelShake>12){

                play();
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {


        }
    };

    public void play() {
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.danger_alarm);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        }

        player.start();
    }

    private void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
            Toast.makeText(this, "MediaPlayer released", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }
}
