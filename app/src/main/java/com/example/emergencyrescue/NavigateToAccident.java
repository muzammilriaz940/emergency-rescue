package com.example.emergencyrescue;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.emergencyrescue.directionhelpers.FetchURL;
import com.example.emergencyrescue.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class NavigateToAccident extends MainActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private GoogleMap mMap;
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createDynamicView(R.layout.activity_nav_to_accident, R.id.nav_home);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(getIntent().getStringExtra("pickUpRequestUserId")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                String userMobile = Objects.requireNonNull(dataSnapshot.child("mobile").getValue()).toString();
                String userBloodGroup = Objects.requireNonNull(dataSnapshot.child("bloodGroup").getValue()).toString();

                TextView victimInformationBox = findViewById(R.id.victimInformationBox);
                String information = "<b>Name</b>: "+userName;
                information += "<br><b>Phone:</b> "+userMobile;
                information += "<br><b>Blood Group:</b> "+userBloodGroup;
                information += "<br><b>Accident Address:</b> "+getIntent().getStringExtra("pickUpAddress");
                victimInformationBox.setText(Html.fromHtml(information));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(MainActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        double pickUpLat = getIntent().getDoubleExtra("pickUpLat", 0);
        double pickUpLong = getIntent().getDoubleExtra("pickUpLong", 0);

        place1 = new MarkerOptions().position(new LatLng(24.884373, 67.164633)).title("Your Location");
        place2 = new MarkerOptions().position(new LatLng(pickUpLat, pickUpLong)).title("Victim Location");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.accidentMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        new FetchURL(NavigateToAccident.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("mylog", "Added Markers");
        mMap.addMarker(place1);
        mMap.addMarker(place2);
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}
