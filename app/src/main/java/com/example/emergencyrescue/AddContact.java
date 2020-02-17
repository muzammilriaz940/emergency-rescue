package com.example.emergencyrescue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddContact extends MainActivity implements
        View.OnClickListener  {

    Button addContactBtn;
    ListView showContactList;
    ArrayList<String> listContacts = new ArrayList<>();
    DatabaseReference dref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createDynamicView(R.layout.activity_add_contact, R.id.nav_addContact);

        showContactList = findViewById(R.id.showContactList);
        addContactBtn = (Button) findViewById(R.id.addContactBtn);

        addContactBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AddContact.this, LoadContact.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            if(resultCode == RESULT_OK){

                final ArrayAdapter<String> adapter=new ArrayAdapter<String>(AddContact.this,android.R.layout.simple_dropdown_item_1line,listContacts);
                showContactList.setAdapter(adapter);

                dref = FirebaseDatabase.getInstance().getReference("EmergencyContacts");

                dref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        listContacts.add(dataSnapshot.getValue(String.class));
                        adapter.notifyDataSetChanged();
                        Toast.makeText(AddContact.this, "by zaid", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Toast.makeText(AddContact.this, "Testing by zaid", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        listContacts.remove(dataSnapshot.getValue(String.class));
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
    }
}
