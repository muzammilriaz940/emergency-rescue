package com.example.emergencyrescue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class Auth extends MainActivity implements
        View.OnClickListener {

    private DatabaseReference mDatabase;
    FirebaseUser user = mAuth.getCurrentUser();
    private EditText authPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflate your activity layout here!
        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_auth, null, false);
        drawer.addView(contentView, 0);
        navigationView.setCheckedItem(R.id.nav_profile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Auth Activity", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();
        authPassword = findViewById(R.id.authPassword);
    }

    private boolean validateForm() {
        boolean valid = true;

        String name = authPassword.getText().toString();
        if (TextUtils.isEmpty(name)) {
            authPassword.setError("Required.");
            valid = false;
        } else {
            authPassword.setError(null);
        }
        return valid;
    }

    private void reAuthenticateUser(final String password) {
        if (!validateForm()) {
            return;
        }

        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail().toString(), password); // Current Login Credentials \\
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(Auth.this, Profile.class);
                            intent.putExtra("profilePassword", password);
                            startActivity(intent);
                        } else {
                            hideKeyboardFrom(Auth.this);
                            Toast.makeText(Auth.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.authBtn) {
            reAuthenticateUser(authPassword.getText().toString());
        }
    }
}
