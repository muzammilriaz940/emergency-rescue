package com.example.emergencyrescue;

import android.os.Bundle;
import android.view.View;

public class NavigateToAccident extends MainActivity implements
        View.OnClickListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createDynamicView(R.layout.activity_nav_to_accident, R.id.nav_home);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
    }
}
