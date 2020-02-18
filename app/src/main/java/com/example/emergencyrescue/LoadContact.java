package com.example.emergencyrescue;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class LoadContact extends MainActivity implements
        View.OnClickListener {

    private static final String TAG = "";
    ArrayList<String> listLoadContacts;
    ListView loadContactList;
    Button saveContactBtn;

    private FirebaseAuth mAuthentication;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createDynamicView(R.layout.activity_load_contact, R.id.nav_addContact);
        mAuthentication = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        loadContactList = findViewById(R.id.loadContactList);
        saveContactBtn = (Button) findViewById(R.id.saveContactBtn);
        displayContacts();

        saveContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Boolean result = false;
                SparseBooleanArray checked = loadContactList.getCheckedItemPositions();

                showProgressDialog("Adding Emergency Contacts");

                FirebaseUser user = mAuthentication.getCurrentUser();
                String currentUserId = user.getUid();
                mDatabaseReference = mDatabaseReference.child("EmergencyContacts").child(currentUserId);

                for (int j = 0; j < checked.size(); j++) {
                    if (checked.valueAt(j)) {

                        String item = loadContactList.getAdapter().getItem(
                                checked.keyAt(j)).toString();

                        String[] itemSeparated = item.split("\n");

                        String contactId   = ((itemSeparated[0].split(":"))[1]).trim();
                        String contactName = ((itemSeparated[1].split(":"))[1]).trim();
                        String contactNumber  = ((itemSeparated[2].split(":"))[1]).trim();

                        Emergency_Contacts EmergCont = new Emergency_Contacts(contactName, contactNumber);

                        try {

                            mDatabaseReference.child(contactId).setValue(EmergCont);
                            result = true;
                        }
                        catch (Exception e) {

                            result = false;
                            Log.e(TAG, "addEmergencyContacts Error : ", e);
                        }
                    }
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra("Data", 1);
                setResult(RESULT_OK,resultIntent);

                if(result == true) {

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                            Toast.makeText(LoadContact.this,"Contacts Added Successfully",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }, 2000);
                }
                if(result == false) {

                    hideProgressDialog();
                    Toast.makeText(LoadContact.this,"No Contacts Selected",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void displayContacts() {

        listLoadContacts = new ArrayList<>();
        ContentResolver resolver = getContentResolver();

        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null,ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY + " ASC");

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                String num = "No Number Found";
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER))) > 0)
                    num = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                listLoadContacts.add("ID: "+ id + "\n" +
                        "Name: " + name + "\n" +
                        "Phone No: " + num);
            }
        }

        loadContactList.setAdapter(new ArrayAdapter<>(LoadContact.this,android.R.layout.simple_list_item_multiple_choice, listLoadContacts));
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
    }
}
