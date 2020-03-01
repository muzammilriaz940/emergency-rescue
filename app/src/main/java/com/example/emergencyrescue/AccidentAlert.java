package com.example.emergencyrescue;

import android.os.Bundle;
import android.view.View;

public class AccidentAlert extends MainActivity implements
        View.OnClickListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createDynamicView(R.layout.activity_accident_alert, R.id.nav_home);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
    }
}
