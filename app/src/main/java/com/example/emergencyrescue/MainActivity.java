package com.example.emergencyrescue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends CommonActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    FloatingActionButton fab;
    NavigationView navigationView;

    public FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    FirebaseUser user = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!isConnected(this)) {
            buildDialog(this).show();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        fab = (FloatingActionButton) findViewById(R.id.fab);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        String userId = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("name").getValue().toString();
                String userMobile = dataSnapshot.child("mobile").getValue().toString();
                String userType = dataSnapshot.child("userType").getValue().toString();
                String userBloodGroup = dataSnapshot.child("bloodGroup").getValue().toString();

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);

                TextView navUserName = (TextView) headerView.findViewById(R.id.navUserName);
                navUserName.setText(userName);

                TextView navUserEmail = (TextView) headerView.findViewById(R.id.navUserEmail);
                navUserEmail.setText(user.getEmail());

                final EditText profileName = (EditText) findViewById(R.id.profileName);
                if(profileName != null) {
                    profileName.setText(userName.toString());
                }

                final EditText profileEmail = (EditText) findViewById(R.id.profileEmail);
                if(profileEmail != null) {
                    profileEmail.setText(user.getEmail().toString());
                }

                final EditText profileUserType = (EditText) findViewById(R.id.profileUserType);
                if(profileUserType != null) {
                    profileUserType.setText(userType.toString());
                }

                final EditText profileMobile = (EditText) findViewById(R.id.profileMobile);
                if(profileMobile != null) {
                    profileMobile.setText(userMobile.toString());
                }

                final EditText profileBloodGroup = (EditText) findViewById(R.id.profileBloodGroup);
                if(profileBloodGroup != null) {
                    profileBloodGroup.setText(userBloodGroup.toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(MainActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void createDynamicView(int layOutId, int navId) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(layOutId, null, false);
        LinearLayout contentFrame;
        contentFrame = (LinearLayout) findViewById(R.id.content_frame);
        contentFrame.addView(contentView);
        navigationView.setCheckedItem(navId);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                startAnimatedActivity(new Intent(getApplicationContext(), Home.class));
                break;
            case R.id.nav_history:
                break;
            case R.id.nav_addContact:
                startAnimatedActivity(new Intent(getApplicationContext(), AddContact.class));
                break;
            case R.id.nav_profile:
                startAnimatedActivity(new Intent(getApplicationContext(), Auth.class));
                break;
            case R.id.nav_signOut:
                showProgressDialog("Signing Out");
                startAnimatedActivity(new Intent(getApplicationContext(), SignOut.class));
                break;
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void startAnimatedActivity(Intent intent) {
        startActivity(intent);
        //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
