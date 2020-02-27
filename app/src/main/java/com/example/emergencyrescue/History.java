package com.example.emergencyrescue;

import android.os.Bundle;
import android.view.View;

public class History extends MainActivity implements
        View.OnClickListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createDynamicView(R.layout.activity_history, R.id.nav_history);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
    }
}
