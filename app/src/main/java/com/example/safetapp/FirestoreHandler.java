package com.example.safetapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationBuilderWithBuilderAccessor;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FirestoreHandler {

    FirebaseFirestore databaseRef;
    Context context;

    FirestoreHandler(Context context){
        databaseRef  = FirebaseFirestore.getInstance();
        this.context = context;
    }


    void sendStressSignal(LatLng bounds){
        Constants.isStressSignalSender = true;
        Map<String,Object> userData = new HashMap<>();
        userData.put("username",Constants.USERNAME);
        userData.put("latitude",bounds.latitude);
        userData.put("longitude",bounds.longitude); //TODO: use geocodes later as a workaround for the limitation of composite queries

        String stressSignalDocument  = Constants.USERNAME+"stressDoucment";
        SharedPreferences sharedPreferences = context.getSharedPreferences("stressDetails",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("documentId",stressSignalDocument);
        editor.apply();

        databaseRef.collection("aliveSignals").document(stressSignalDocument).set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Signal","Stress signal sent");
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(1000);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Something went wrong",Toast.LENGTH_LONG).show();
            }
        });

        databaseRef.collection("recentSignals").add(userData);
        setLocationUpdater(stressSignalDocument);
    }

    void listenStressSignal(){
        databaseRef.collection("aliveSignals").limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Log.d("StressReceived",documentSnapshot.toString());
                        double latitude = documentSnapshot.getDouble("latitude");
                        double longitude = documentSnapshot.getDouble("longitude");
                        String username = documentSnapshot.getString("username");

                        if (!Constants.isStressSignalSender){
                            startAlarm();
                            sendNotification(latitude,longitude,username);
                        }
                    }
                }
            }
        });
    }

    private void setLocationUpdater(final String stressSignalDocument) {
        Thread updateCoords = new Thread(new Runnable() {
            @Override
            public void run() {
                while (Constants.isStressSignalSent){
                    try {
                        Thread.sleep(Constants.LOCATION_UPDATE_DURATION);
                        Log.d("Update","sending Location updates");
                        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
                        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location bounds = task.getResult();
                                Map<String,Object> updatedData = new HashMap<>();
                                updatedData.put("latitude",bounds.getLatitude());
                                updatedData.put("longitude",bounds.getLongitude());

                                databaseRef.collection("updatedCoords").document(stressSignalDocument).set(updatedData);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        updateCoords.start();
    }

    private void sendNotification(double latitude, double longitude, String username) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("stressDetails",Context.MODE_PRIVATE);
        String stressDocument = sharedPreferences.getString("documentId","");
        Intent notifyStress = new Intent(context,StressTracerMap.class);
        notifyStress.putExtra("latitude",latitude);
        notifyStress.putExtra("longitude",longitude);
        notifyStress.putExtra("username",username);
        notifyStress.putExtra("userDocumentStress",stressDocument);
        notifyStress.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,notifyStress,PendingIntent.FLAG_UPDATE_CURRENT);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(context,Constants.PRIMARY_CHANNEL_ID)       //TODO: add backwards compatibility
                    .setContentText(Constants.CHANNEL_NAME)
                    .setSmallIcon(R.drawable.camera)                        //TODO: change icon
                    .setContentText("Someone is in trouble")
                    .setAutoCancel(true);

            builder.setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(2,builder.build());
        }
    }

    private void startAlarm() {
        MediaPlayer mediaPlayer = MediaPlayer.create(context,R.raw.danger_alarm);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

        mediaPlayer.start();
    }

    void stopStressSignal() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("stressDetails",Context.MODE_PRIVATE);
        String stressSignalDocument = sharedPreferences.getString("documentId",null);

        if (stressSignalDocument != null) {
            databaseRef.collection("aliveSignals").document(stressSignalDocument).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Constants.isStressSignalSent = false;
                        Constants.isStressSignalSender = false;
                        Toast.makeText(context, "Stress signal stopped", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
