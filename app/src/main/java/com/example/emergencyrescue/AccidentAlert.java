package com.example.emergencyrescue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AccidentAlert extends MainActivity implements
        View.OnClickListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createDynamicView(R.layout.activity_accident_alert, R.id.nav_home);
        View parentLayout = findViewById(R.id.content_frame);
        parentLayout.setBackgroundColor(getResources().getColor(android.R.color.background_dark));

        String pickUpRequestUserId = getIntent().getStringExtra("pickUpRequestUserId");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PickUpRequest");
        reference.child("DC614w9DqtVeGKhxGqvfA1Z5qh63").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String g = Objects.requireNonNull(dataSnapshot.child("g").getValue()).toString();
                TextView victimCurrentAddress = findViewById(R.id.victimCurrentAddress);
                if(victimCurrentAddress != null) {
                    victimCurrentAddress.setText(g);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(MainActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, NavigateToAccident.class);
        startActivity(i);
    }
}
