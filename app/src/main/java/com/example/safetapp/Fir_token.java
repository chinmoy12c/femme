package com.example.safetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.safetapp.Adapter.FirAdapter;
import com.example.safetapp.Model.FirRecord;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Fir_token extends AppCompatActivity {
    private List<FirRecord> firList;
    private FirAdapter firAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fir_token);
        final RecyclerView recyclerView=findViewById(R.id.recycler_fir);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firList=new ArrayList<>();
        DatabaseReference dbFir= FirebaseDatabase.getInstance().getReference().child("FIRdetail");

        dbFir.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    FirRecord firRecord=ds.getValue(FirRecord.class);
                    firList.add(firRecord);
                }
                firAdapter=new FirAdapter(Fir_token.this,firList);
                recyclerView.setAdapter(firAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

