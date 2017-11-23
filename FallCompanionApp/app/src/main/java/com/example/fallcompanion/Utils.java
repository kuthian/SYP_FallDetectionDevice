package com.example.fallcompanion;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Mickey on 2017-11-18.
 */

public class Utils {

    private static final String TAG = "UTILS";

    public static void ShowToast(Context ApplicationContext, String ToastMessage) {
        Log.d(TAG, "ShowToast: Begin: " + ToastMessage);

        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(ApplicationContext, ToastMessage, duration);
        toast.show();

        Log.d(TAG, "ShowToast: End");
    }



}
