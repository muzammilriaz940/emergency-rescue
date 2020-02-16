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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Objects;

public class CommonActivity extends AppCompatActivity
        implements SensorEventListener {

    @VisibleForTesting
    public ProgressDialog mProgressDialog;
    private static final String TAG = "G-Force";
    
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

    @IgnoreExtraProperties
    class User {

        public String name,mobile, userType,bloodGroup;

         User(String name, String mobile, String userType, String bloodGroup) {
            this.name = name;
            this.mobile = mobile;
            this.userType = userType;
            this.bloodGroup = bloodGroup;
        }
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

            String gfString = "G-Force : " + gForceValue;

            if(gForceValue > 4) {
                Log.i(TAG, gfString);
                Toast.makeText(this, gfString, Toast.LENGTH_SHORT).show();
            }
        }
    }
}