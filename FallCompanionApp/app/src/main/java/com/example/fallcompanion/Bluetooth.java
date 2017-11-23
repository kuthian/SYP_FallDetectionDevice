package com.example.fallcompanion;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.example.fallcompanion.client.ClientActivity;
import com.example.fallcompanion.databinding.ActivityBluetoothBinding;

public class Bluetooth extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityBluetoothBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_bluetooth);

        binding.launchClientButton.setOnClickListener(v -> startActivity(new Intent(Bluetooth.this,
                ClientActivity.class)));
    }
}

