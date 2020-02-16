package com.example.emergencyrescue;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Signup extends CommonActivity implements
        View.OnClickListener{
    private EditText signUpName;
    private EditText signUpMobile;
    private EditText signUpEmail;
    private EditText signUpPassword;
    private Spinner signUpUserType;
    private EditText signUpBloodGroup;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signUpName = findViewById(R.id.signUpName);
        signUpMobile = findViewById(R.id.signUpMobile);
        signUpEmail = findViewById(R.id.signUpEmail);
        signUpPassword = findViewById(R.id.signUpPassword);
        signUpBloodGroup = findViewById(R.id.signUpBloodGroup);
        signUpUserType = findViewById(R.id.signUpUserType);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Spinner element
        Spinner spinner = signUpUserType;

        // Spinner Drop down elements
        List<String> userType = new ArrayList<>();
        userType.add("User");
        userType.add("Responder");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userType);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }
        showProgressDialog("Signing Up");
        ///creating new user
        mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    try {
                        if (user != null) {
                            addUserDetail(user.getUid(), signUpName.getText().toString(), signUpMobile.getText().toString(), signUpUserType.getSelectedItem().toString(), signUpBloodGroup.getText().toString());
                            clearForm((ViewGroup) findViewById(R.id.signUpRoot));
                            Intent intent = new Intent(Signup.this, SignIn.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(Signup.this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        if (user != null) {
                            user.delete();
                        }
                        Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    hideKeyboardFrom(Signup.this);
                    Toast.makeText(Signup.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }

                hideProgressDialog();
            }
        });
        //////////////////
    }

    private boolean validateForm() {
        boolean valid = true;

        String name = signUpName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            signUpName.setError("Required.");
            valid = false;
        } else {
            signUpName.setError(null);
        }

        String mobile = signUpMobile.getText().toString();
        if (TextUtils.isEmpty(mobile)) {
            signUpMobile.setError("Required.");
            valid = false;
        } else {
            signUpMobile.setError(null);
        }

        String email = signUpEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            signUpEmail.setError("Required.");
            valid = false;
        } else {
            signUpEmail.setError(null);
        }

        String password = signUpPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            signUpPassword.setError("Required.");
            valid = false;
        } else {
            signUpPassword.setError(null);
        }

        String bloodGroup = signUpBloodGroup.getText().toString();
        if (TextUtils.isEmpty(bloodGroup)) {
            signUpBloodGroup.setError("Required.");
            valid = false;
        } else {
            signUpBloodGroup.setError(null);
        }

        return valid;
    }

    private void addUserDetail(String userId, String name, String mobile, String userType, String bloodGroup) {
        User user = new User(name, mobile, userType, bloodGroup);
        mDatabase.child("users").child(userId).setValue(user);
        mAuth.signOut();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signUpBtn) {
            createAccount(signUpEmail.getText().toString(), signUpPassword.getText().toString());
        } else if (i == R.id.linkSignIn) {
            Intent intent = new Intent(this, SignIn.class);
            startActivity(intent);
        }
    }
}
