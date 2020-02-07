package com.example.emergencyrescue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import com.google.android.material.snackbar.Snackbar;

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
