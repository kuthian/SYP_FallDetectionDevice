package com.example.fallcompanion;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Bluetooth extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;
    private static final String TAG = "BLUETOOTH";
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView lvNewDevices;

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);
                switch(state)
                {
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "OnReceive: State On");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: State OFf");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver1: State Connected");
                        break;
                    case BluetoothAdapter.STATE_DISCONNECTED:
                        Log.d(TAG, "mBroadcastReceiver1: State Disconnected");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: State Turning Off");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: State Turning On");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);
                switch(state)
                {
                    //Device is in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability enabled");
                        break;
                    //Device is not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability enabled. Able to receive connections");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability not enabled. Not able to receive connections");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting...");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {String action = intent.getAction();

            Log.d(TAG, "mBroadcastReceiver3: Action Found");
            if (action.equals(BluetoothDevice.ACTION_FOUND))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG,"mBroadcastReceiver3: " + device.getName() + ":" + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                //lvNewDevices.setAdapter(mDeviceListAdapter);

                mBluetoothAdapter.cancelDiscovery();

                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2)
                {
                    Log.d(TAG,"Trying to pair with " + device.getName());

                    device.createBond();
                }

            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            Log.d(TAG, "mBroadcastReceiver3: Action Found");
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                int state = device.getBondState();

                switch(state)
                {
                    case BluetoothDevice.BOND_BONDED:
                        Log.d(TAG, "mBroadcastReceiver4: BOND_BONDED");
                        ShowToast("Paired with " + device.getName());
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Log.d(TAG, "mBroadcastReceiver4: BOND_BONDING");
                        ShowToast("Pairing with " + device.getName());
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Log.d(TAG, "mBroadcastReceiver4: BOND_NONE");

                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        Log.d(TAG, "BLUETOOTH: OnCreate");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //lvNewDevices = (L0istView) findViewById(R.id.)
        mBTDevices = new ArrayList<>();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        //mBluetoothAdapter.cancelDiscovery();

        try
        {
            unregisterReceiver(mBroadcastReceiver1);
            unregisterReceiver(mBroadcastReceiver2);
            unregisterReceiver(mBroadcastReceiver3);
            unregisterReceiver(mBroadcastReceiver4);
        }
        catch( Exception e)
        {

        }
    }


    public void ShowToast(String ToastMessage)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, ToastMessage, duration);
        toast.show();
    }

    public void StartBluetooth(View view)
    {
        if (mBluetoothAdapter == null)
        {
            ShowToast("Device does not support BlueTooth");
            Log.d(TAG, "StartBluetooth: Device does not support BlueTooth");
        }
        else if (!mBluetoothAdapter.isEnabled())
        {
            Log.d(TAG, "StartBluetooth: Enabling BlueTooth");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        else if (mBluetoothAdapter.isEnabled())
        {
            ShowToast("Bluetooth is Enabled");
            Log.d(TAG, "StartBluetooth: BlueTooth is enabled");
        }

/*        Log.d(TAG, "StartBluetooth: Enabling BlueTooth discoverability");
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2, intentFilter);*/

        DiscoverBluetooth();

    }

    public void DiscoverBluetooth()
    {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "DiscoverBluetooth: Cancelling discovery");
            Log.d(TAG, "DiscoverBluetooth: Restarting discovery");
            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        else
        {
            //CheckBTPermissions();
            Log.d(TAG, "DiscoverBluetooth: Starting discovery");
            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }
    public void PairBluetooth()
    {

    }

/*    private void CheckBTPermissions()
    {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0)
            {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
            }
        }
        else
        {
            Log.d(TAG, "CheckBTPerimissions: Non need to check permissions. SDK version > LOLLIPOP");
        }
    }*/
}

