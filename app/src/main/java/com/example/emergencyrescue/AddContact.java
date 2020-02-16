package com.example.emergencyrescue;

import android.os.Bundle;
import android.view.View;

public class AddContact extends MainActivity implements
        View.OnClickListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createDynamicView(R.layout.activity_add_contact, R.id.nav_addContact);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
    }
}
