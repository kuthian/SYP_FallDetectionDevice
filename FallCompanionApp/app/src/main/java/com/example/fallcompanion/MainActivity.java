package com.example.fallcompanion;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.app.NotificationCompat.Builder;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


public class MainActivity extends AppCompatActivity {

    public String longitude = "0";
    public String latitude = "0";
    public String FallTime = "0";
    private FusedLocationProviderClient mFusedLocationClient;
    public String SavedPhoneNumber1;
    public String SavedPhoneNumber2;
    public String SavedPhoneNumber3;
    public String SavedPhoneNumber4;

    SharedPreferences prefs;
    public String SavedContactNumber1;
    public String SavedContactNumber2;
    public String SavedContactNumber3;
    public String SavedContactNumber4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.notification_icon);
*/
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        prefs = this.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);

        SavedContactNumber1 = "com.example.app.savedcontactnumber1";
        SavedContactNumber2 = "com.example.app.savedcontactnumber2";
        SavedContactNumber3 = "com.example.app.savedcontactnumber3";
        SavedContactNumber4 = "com.example.app.savedcontactnumber4";
    }

    protected void SendSMS(View view)
    {
        GetDate();
        GetPhoneNumbers();
        SendGpsSms();
        CreateNotification();
        StartAlarm();
        Vibrate(1000);

    }
    private void SendFeedbackSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        android.telephony.SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }
    public void SendSimpleSMS(String PhoneNumber, String LocationText) throws InterruptedException
    {
        if (PhoneNumber != "-" || PhoneNumber != null || PhoneNumber != " "|| PhoneNumber != "")
        {
            android.telephony.SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage("+1 " + PhoneNumber, null, LocationText, null, null);
            ShowToast("Text sent to " + PhoneNumber);
        }
        else
        {
            ShowToast("No Message Sent");
        }
    }
    public void SendGpsSms()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null)
                            {
                                latitude = Double.toString(location.getLatitude());
                                longitude = Double.toString(location.getLatitude());
                                //ShowToast("Fix: " + latitude + "," + longitude);
                            }
                            else
                            {
                                latitude = "Zonk";
                                longitude = "Zonk";
                            }
                        }
                    });
        }
        else
        {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {

                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null)
                            {
                                latitude = Double.toString(location.getLatitude());
                                longitude = Double.toString(location.getLongitude());
                                if (latitude != "0" && longitude != "0")
                                {
                                    try
                                    {
                                        String FallText = "A fall has been detected at https://maps.google.com/?q=+" + latitude + "," + longitude + " . The fall occurred on " + FallTime;
                                        SendSimpleSMS(SavedPhoneNumber1, FallText);
                                        SendSimpleSMS(SavedPhoneNumber2, FallText);
                                        SendSimpleSMS(SavedPhoneNumber3, FallText);
                                        SendSimpleSMS(SavedPhoneNumber4, FallText);
                                    }
                                    catch (InterruptedException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            else
                            {
                                latitude = "Zonk";
                                longitude = "Zonk";
                            }
                        }
                    });
        }
    }
    public void GetDate()
    {
        Date CurrentTime = Calendar.getInstance().getTime();
        FallTime = CurrentTime.toString();
    }
    private void GetPhoneNumbers()
    {
        SavedPhoneNumber1 = prefs.getString(SavedContactNumber1, "-");
        SavedPhoneNumber2 = prefs.getString(SavedContactNumber2, "-");
        SavedPhoneNumber3 = prefs.getString(SavedContactNumber3, "-");
        SavedPhoneNumber4 = prefs.getString(SavedContactNumber4, "-");
    }

    public void CreateNotification()
    {
        NotificationCompat.Builder mBuilder =
                (Builder) new Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");


        Intent resultIntent = new Intent(this, MainActivity.class);
// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        // Sets an ID for the notification
        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    public void StartAlarm()
    {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.start();
    }


    public void ShowToast(String ToastMessage)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, ToastMessage, duration);
        toast.show();
    }
    public void Vibrate(int VibrateDuration)
    {
        Vibrator smsVib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        smsVib.vibrate(VibrateDuration);
    }
    public void open_bluetooth(View view)
    {
        Intent intent = new Intent(this, Bluetooth.class);
        startActivity(intent);
    }
    public void open_emergency_contacts(View view)
    {
        Intent intent = new Intent(this, EmergencyContacts.class);
        startActivity(intent);
    }
    public void open_fall_data(View view)
    {
        Intent intent = new Intent(this, FallData.class);
        startActivity(intent);
    }
    public void open_alert_settings(View view)
    {
        Intent intent = new Intent(this, AlertSettings.class);
        startActivity(intent);
    }


}
