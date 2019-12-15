package com.example.emergencyrescue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends MainActivity implements
        View.OnClickListener {

    private DatabaseReference mDatabase;
    FirebaseUser user = mAuth.getCurrentUser();
    private EditText profileName;
    private EditText profileMobile;
    private EditText profileEmail;
    private EditText profileUserType;
    private EditText profileBloodGroup;
    private String profilePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflate your activity layout here!
        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_profile, null, false);
        drawer.addView(contentView, 0);
        navigationView.setCheckedItem(R.id.nav_profile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Profile Activity", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();
        profileName = findViewById(R.id.profileName);
        profileMobile = findViewById(R.id.profileMobile);
        profileEmail = findViewById(R.id.profileEmail);
        profileUserType = findViewById(R.id.profileUserType);
        profileBloodGroup = findViewById(R.id.profileBloodGroup);

        Intent intent = getIntent();
        profilePassword = intent.getStringExtra("profilePassword");
    }

    private boolean validateForm() {
        boolean valid = true;

        String name = profileName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            profileName.setError("Required.");
            valid = false;
        } else {
            profileName.setError(null);
        }

        String mobile = profileMobile.getText().toString();
        if (TextUtils.isEmpty(mobile)) {
            profileMobile.setError("Required.");
            valid = false;
        } else {
            profileMobile.setError(null);
        }

        String userType = profileUserType.getText().toString();
        if (TextUtils.isEmpty(userType)) {
            profileUserType.setError("Required.");
            valid = false;
        } else {
            profileUserType.setError(null);
        }

        String bloodGroup = profileBloodGroup.getText().toString();
        if (TextUtils.isEmpty(bloodGroup)) {
            profileBloodGroup.setError("Required.");
            valid = false;
        } else {
            profileBloodGroup.setError(null);
        }

        return valid;
    }

    private void updateProfile(String userId, String name, String mobile, String userType, String bloodGroup) {
        if (!validateForm()) {
            return;
        }
        User user = new User(name, mobile, userType, bloodGroup);
        mDatabase.child("users").child(userId).setValue(user);
        hideKeyboardFrom(Profile.this);
        View parentLayout = findViewById(R.id.profileRoot);
        Snackbar.make(parentLayout, "Profile Updated", Snackbar.LENGTH_LONG).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        }, 3000);
    }

    private void reAuthenticateUser(String userId, final String email, final String password) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), password); // Current Login Credentials \\
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //----------------Code for Changing Email Address----------\\
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Profile.this, "Email Success", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
//                        //----------------------------------------------------------\\
//                        //----------------Code for Changing Password----------\\
//                        user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(Profile.this, "Password Success", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                        //----------------------------------------------------------\\
                        } else {
                            hideKeyboardFrom(Profile.this);
                            Toast.makeText(Profile.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.profileBtn) {
            if(!TextUtils.isEmpty(profileEmail.getText().toString()) && !TextUtils.isEmpty(profilePassword.toString())){
                reAuthenticateUser(user.getUid(), profileEmail.getText().toString(), profilePassword.toString());
            }
            updateProfile(user.getUid(), profileName.getText().toString(), profileMobile.getText().toString(), profileUserType.getText().toString(), profileBloodGroup.getText().toString());
        }
    }
}
