package com.example.fallcompanion;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.app.NotificationCompat.Builder;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN";

    private FusedLocationProviderClient mFusedLocationClient;
    private SharedPreferences prefs;
    private Ringtone defaultRingtone;

    //SharedPreferences Strings
    private String SavedContactNumber1 = "com.example.app.savedcontactnumber1";

    private String SavedContactNumber2 = "com.example.app.savedcontactnumber2";

    private String SavedContactNumber3 = "com.example.app.savedcontactnumber3";

    private String SavedContactNumber4 = "com.example.app.savedcontactnumber4";

    private String SavedSeekBarValue = "com.example.app.savedseekbarvalue";

    private String SavedOnOrOFf = "com.example.app.savedonoroff";

    //Phone number strings
    private String SavedPhoneNumber1;
    private String SavedPhoneNumber2;
    private String SavedPhoneNumber3;
    private String SavedPhoneNumber4;

    private Boolean DefaultSavedOnOrOff = false;
    private String DefaultCountdownEventTime = "30";
    private String longitude = "0";
    private String latitude = "0";
    private String FallTime = "0";
    private int CountdownEventTime;
    private Boolean OnOrOffState;

    Vibrator smsVib;

    CountDownTimer Timer;
    Timer timer;

    private TextView TimerView;
    public Button EventButton;
    private Button CancelEventButton;

    //Bluetooth


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        prefs = this.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);

        CountdownEventTime = Integer.parseInt(prefs.getString(SavedSeekBarValue, DefaultCountdownEventTime));
        OnOrOffState = prefs.getBoolean(SavedOnOrOFf, DefaultSavedOnOrOff);

        TimerView = (TextView) findViewById(R.id.TimerView);
        EventButton = (Button) findViewById(R.id.EventButton);
        CancelEventButton = (Button) findViewById(R.id.CancelButton);

        if (!OnOrOffState) {
            EventButton.setEnabled(false);
            EventButton.setClickable(false);
        } else {
            EventButton.setEnabled(true);
            EventButton.setClickable(true);
        }

        CancelEventButton.setEnabled(false);
        CancelEventButton.setClickable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        CountdownEventTime = Integer.parseInt(prefs.getString(SavedSeekBarValue, DefaultCountdownEventTime));
        OnOrOffState = prefs.getBoolean(SavedOnOrOFf, DefaultSavedOnOrOff);

        if (!OnOrOffState) {
            EventButton.setEnabled(false);
            EventButton.setClickable(false);
        } else {
            EventButton.setEnabled(true);
            EventButton.setClickable(true);
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            finish();
        }

        Log.d(TAG, "onResumeTest");
    }

    protected void TriggerEventCountdown(View view) {
        Log.d(TAG, "TriggerEventCountdown: Begin");

        if (EventButton.isEnabled() && OnOrOffState) {
            EventButton.setEnabled(false);
            EventButton.setClickable(false);

            CancelEventButton.setEnabled(true);
            CancelEventButton.setClickable(true);
            try {
                GetDate();
                GetPhoneNumbers();
                CreateNotification();

                StartVibrate(CountdownEventTime + 1);
                StartTimer(CountdownEventTime + 1);
            } catch (Exception e) {
                Log.d(TAG, "An Unexpected Error Occurred",e);
                Utils.ShowToast(getApplicationContext(),"An Unexpected Error Occurred");
            }
        }

        Log.d(TAG, "TriggerEventCountdown: End");
    }

    public void StartEventTasks() {
        StartAlarm();
        SendGpsSms();
    }

    public void SendSimpleSMS(String PhoneNumber, String LocationText) throws InterruptedException {
        Log.d(TAG, "SendSimpleSms: Begin");
        if (PhoneNumber != null) {
            if (PhoneNumber.length() == 10) {
                android.telephony.SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage("+1 " + PhoneNumber, null, LocationText, null, null);
                Log.d(TAG, "SendSimpleSms: Text sent to " + PhoneNumber);
                Utils.ShowToast(getApplicationContext(), "Text sent to " + PhoneNumber);
            } else {
                Log.d(TAG, "No Message Sent to:" + PhoneNumber);
            }

        } else {
            Log.d(TAG, "SendSimpleSms: No Message Sent");
        }

        Log.d(TAG, "SendSimpleSms: End");
    }

    public void SendGpsSms() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                latitude = Double.toString(location.getLatitude());
                                longitude = Double.toString(location.getLatitude());
                                //ShowToast("Fix: " + latitude + "," + longitude);
                            } else {
                                latitude = "Zonk";
                                longitude = "Zonk";
                            }
                        }
                    });
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {

                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                latitude = Double.toString(location.getLatitude());
                                longitude = Double.toString(location.getLongitude());
                                if (latitude != "0" && longitude != "0") {
                                    try {
                                        String FallText = "A fall has been detected at https://maps.google.com/?q=+" + latitude + "," + longitude + " . The fall occurred on " + FallTime;
                                        SendSimpleSMS(SavedPhoneNumber1, FallText);
                                        SendSimpleSMS(SavedPhoneNumber2, FallText);
                                        SendSimpleSMS(SavedPhoneNumber3, FallText);
                                        SendSimpleSMS(SavedPhoneNumber4, FallText);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                latitude = "Zonk";
                                longitude = "Zonk";
                            }
                        }
                    });
        }
    }

    public void GetDate() {
        Date CurrentTime = Calendar.getInstance().getTime();
        FallTime = CurrentTime.toString();
    }

    private void GetPhoneNumbers() {
        SavedPhoneNumber1 = prefs.getString(SavedContactNumber1, "-");
        SavedPhoneNumber2 = prefs.getString(SavedContactNumber2, "-");
        SavedPhoneNumber3 = prefs.getString(SavedContactNumber3, "-");
        SavedPhoneNumber4 = prefs.getString(SavedContactNumber4, "-");
    }

    public void CreateNotification() {
        Log.d(TAG, "CreateNotification: Begin");

        NotificationCompat.Builder mBuilder =
                (Builder) new Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("Fall Detection Device")
                        .setContentText("Fall Detected");

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

        Log.d(TAG, "CreateNotification: End");
    }

    public void StartAlarm() {
        Log.d(TAG, "StartAlarm: Begin");
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        defaultRingtone = RingtoneManager.getRingtone(getApplicationContext(), Settings.System.DEFAULT_RINGTONE_URI);

        defaultRingtone.play();
/*        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.start();*/
        Log.d(TAG, "StartAlarm: End");
    }

    public void EndAlarm() {
        Log.d(TAG, "EndAlarm: Begin");
        if (defaultRingtone != null) {
            if (defaultRingtone.isPlaying()) {
                Log.d(TAG, "EndAlarm: Stopping Alarm");
                defaultRingtone.stop();

            }
        }
/*        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.start();*/
        Log.d(TAG, "EndAlarm: End");
    }


    public void StartVibrate(int VibrateDuration) {
        Log.d(TAG, "StartVibrate: Begin, Vibrating for " + VibrateDuration + " Seconds.");
        smsVib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        smsVib.vibrate(new long[]{0, 500, 110, 500, 110, 450, 110, 200, 110, 170, 40, 450, 110, 200, 110, 170, 40, 500}, 10);

        //smsVib.vibrate(15000);
        Log.d(TAG, "StartVibrate: End");
    }

    public void EndVibrate() {
        Log.d(TAG, "EndVibrate: Begin");
        if (smsVib != null) {
            Log.d(TAG, "EndVibrate: Vibrate Ended");
            smsVib.cancel();
            smsVib = null;
        }
        Log.d(TAG, "EndVibrate: End");
    }

    public void StartTimer(int TimerTime) {
        Log.d(TAG, "StartTimer: Begin");

        Timer = new CountDownTimer(TimerTime * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                TimerView.setText("Event Timer: " + millisUntilFinished / 1000);
            }

            public void onFinish() {

                Utils.ShowToast(getApplicationContext(),"Alarm Raised");
                StartEventTasks();
                TimerView.setText("");
            }
        }.start();

        Log.d(TAG, "StartTimer: End");
    }

    public void EndTimer() {
        Log.d(TAG, "EndTimer: Begin");
        if (Timer != null) {
            Timer.cancel();
            timer = null;
            Utils.ShowToast(getApplicationContext(),"Event Cancelled");
            TimerView.setText("");
        }
        Log.d(TAG, "EndTimer: End");
    }

    public void Cancel(View view) {
        Log.d(TAG, "Cancel: Begin");
        //StopTimerTask();
        try {
            EndTimer();
            EndVibrate();
            EndAlarm();
            Utils.ShowToast(getApplicationContext(),"Event Cancelled");
        } catch (Exception e) {
            Utils.ShowToast(getApplicationContext(),"Error During Canceling");
        }

        EventButton.setClickable(true);
        EventButton.setEnabled(true);

        if (EventButton.isEnabled() && EventButton.isClickable()) {
            CancelEventButton.setEnabled(false);
            CancelEventButton.setClickable(false);
        }

        Log.d(TAG, "Cancel: End");
    }

    public void OpenBluetooth(View view) {

        Log.d(TAG, "OpenBluetooth: Begin");
        Intent intent = new Intent(this, Bluetooth.class);
        startActivity(intent);
        Log.d(TAG, "OpenBluetooth: End");
    }

    public void OpenEmergencyContacts(View view) {
        Log.d(TAG, "OpenEmergencyContacts: Begin");
        Intent intent = new Intent(this, EmergencyContacts.class);
        startActivity(intent);
        Log.d(TAG, "OpenEmergencyContacts: End");
    }

    public void OpenFallData(View view) {
        Log.d(TAG, "OpenFallData: Begin");
        Intent intent = new Intent(this, FallData.class);
        startActivity(intent);
        Log.d(TAG, "OpenFallData: End");
    }

    public void OpenAlertSettings(View view) {
        Log.d(TAG, "OpenAlertSettings: Begin");
        Intent intent = new Intent(this, AlertSettings.class);
        startActivity(intent);
        Log.d(TAG, "OpenAlertSettings: End");
    }




}


