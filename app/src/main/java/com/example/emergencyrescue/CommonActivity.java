package com.example.emergencyrescue;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class CommonActivity extends AppCompatActivity
        implements SensorEventListener {

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {
            sensorManager.registerListener(this,sensorManager.getDefaultSensor
                    (Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    
    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            if(message == null || message.equals("")){
                mProgressDialog.setMessage(getString(R.string.Loading));
            }else{
                mProgressDialog.setMessage(message);
            }
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public static void hideKeyboardFrom(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    public void clearForm(ViewGroup group) {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText)view).setText("");
            }

            if(view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0))
                clearForm((ViewGroup)view);
        }
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm != null ? cm.getActiveNetworkInfo() : null;

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            return (mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting());
        } else
            return false;
    }

    public AlertDialog.Builder buildDialog(Context c, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });
        return builder;
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // onAccuracyChanged
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            float xVal = event.values[0];
            double xValSquare = Math.pow(xVal, 2);
            float yVal = event.values[1];
            double yValSquare = Math.pow(yVal, 2);
            float zVal = event.values[2];
            double zValSquare = Math.pow(zVal, 2);

            double a = Math.sqrt(xValSquare + yValSquare + zValSquare);
            double gForceValue = (a / 9.81);

            if(gForceValue > 4) {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("WARNING (Accident Detected)!")
                        .setMessage("Please 'Cancel' to abort sending emergency notification.")
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendEmergencyNotification();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    private static final int AUTO_DISMISS_MILLIS = 15000;
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        final Button defaultButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        final CharSequence negativeButtonText = defaultButton.getText();
                        new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                defaultButton.setText(String.format(
                                        Locale.getDefault(), "%s (%d)",
                                        negativeButtonText,
                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1 //add one so it never displays zero
                                ));
                            }
                            @Override
                            public void onFinish() {
                                if (((AlertDialog) dialog).isShowing()) {
                                    dialog.dismiss();
                                    sendEmergencyNotification();
                                }
                            }
                        }.start();
                    }
                });
                dialog.show();
            }
        }
    }

    public void sendEmergencyNotification(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        if (user != null) {
            try {
                mDatabase.child("PickUpRequest").child(user.getUid()).child("g").setValue("test");
                mDatabase.child("PickUpRequest").child(user.getUid()).child("l").child("0").setValue("test");
                mDatabase.child("PickUpRequest").child(user.getUid()).child("l").child("1").setValue("test");
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


    @IgnoreExtraProperties
    public class Emergency_Contacts {

        public String contactName;
        public String contactNumber;

        public Emergency_Contacts() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Emergency_Contacts(String contactName, String contactNumber) {

            this.contactName = contactName;
            this.contactNumber = contactNumber;
        }
    }
}