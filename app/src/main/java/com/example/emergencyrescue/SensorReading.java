package com.example.emergencyrescue;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.view.Menu;
import android.widget.TextView;

public class SensorReading extends MainActivity implements
        View.OnClickListener, SensorEventListener  {

    TextView x, y, z, gf;
    String sx, sy, sz, gfString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createDynamicView(R.layout.activity_sensor_reading, R.id.nav_sensor);
        x = findViewById (R.id.XValue);
        y = findViewById (R.id.YValue);
        z = findViewById (R.id.ZValue);
        gf = findViewById (R.id.gfValue);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensorManager.registerListener(this, sensorManager.getDefaultSensor
                (Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            float xVal = event.values[0];
            double xValSquare = Math.pow(xVal, 2);
            float yVal = event.values[1];
            double yValSquare = Math.pow(yVal, 2);
            float zVal = event.values[2];
            double zValSquare = Math.pow(zVal, 2);

            double a = Math.sqrt(xValSquare + yValSquare + zValSquare);
            double gForceValue = (a / 9.81);

            sx = "X Value : <font color = '#800080'> " + xVal + "</font>";
            sy = "Y Value : <font color = '#800080'> " + yVal + "</font>";
            sz = "Z Value : <font color = '#800080'> " + zVal + "</font>";
            gfString = "G Force : <font color = '#800080'> " + gForceValue + "</font>";

            x.setText(Html.fromHtml(sx));
            y.setText(Html.fromHtml(sy));
            z.setText(Html.fromHtml(sz));
            if(gForceValue > 4) {
                gf.setText(Html.fromHtml(gfString));
            }
        }
    }
}
