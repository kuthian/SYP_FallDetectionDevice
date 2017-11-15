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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.app.NotificationCompat.Builder;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN";

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

    public Ringtone defaultRingtone;

    Vibrator smsVib;

    CountDownTimer Timer;
    Timer timer;
    TimerTask task;
    final Handler handler = new Handler();

    private TextView TimerView;
    private Button EventButton;
    private Button CancelEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        prefs = this.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);

        SavedContactNumber1 = "com.example.app.savedcontactnumber1";
        SavedContactNumber2 = "com.example.app.savedcontactnumber2";
        SavedContactNumber3 = "com.example.app.savedcontactnumber3";
        SavedContactNumber4 = "com.example.app.savedcontactnumber4";

        TimerView = (TextView) findViewById(R.id.TimerView);
        EventButton = (Button) findViewById(R.id.EventButton);
        CancelEventButton = (Button) findViewById(R.id.CancelButton);

        CancelEventButton.setEnabled(false);
        CancelEventButton.setClickable(false);
    }

    protected void TriggerEventCountdown(View view)
    {
        Log.d(TAG, "TriggerEventCountdown: Begin");

        if (EventButton.isEnabled())
        {
            EventButton.setEnabled(false);
            EventButton.setClickable(false);

            CancelEventButton.setEnabled(true);
            CancelEventButton.setClickable(true);

            try
            {
                GetDate();
                GetPhoneNumbers();
                CreateNotification();
                Vibrate(10000);
                StartTimer();
            }
            catch(Exception e)
            {
                ShowToast("An Unexpected Error Occurred");
            }
        }

        Log.d(TAG, "TriggerEventCountdown: End");
    }

    public void StartEventTasks()
    {
        StartAlarm();
        SendGpsSms();
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

    public void StartAlarm()
    {
        Log.d(TAG, "StartAlarm: Begin");
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        defaultRingtone = RingtoneManager.getRingtone(getApplicationContext(), Settings.System.DEFAULT_RINGTONE_URI);

        defaultRingtone.play();
/*        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.start();*/
        Log.d(TAG, "StartAlarm: End");
    }

    public void EndAlarm()
    {
        Log.d(TAG, "EndAlarm: Begin");
        if(defaultRingtone != null)
        {
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

    public void ShowToast(String ToastMessage)
    {
        Log.d(TAG, "ShowToast: Begin");

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, ToastMessage, duration);
        toast.show();

        Log.d(TAG, "ShowToast: End");
    }

    public void Vibrate(int VibrateDuration)
    {
        Log.d(TAG, "Vibrate: Begin");
        smsVib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        smsVib.vibrate(VibrateDuration);
        Log.d(TAG, "Vibrate: End");
    }

    public void EndVibrate()
    {
        if (smsVib != null)
        {
            smsVib.cancel();
            smsVib = null;
        }

    }
    public void StartTimer()
    {
        Log.d(TAG, "StartTimer: Begin");

        Timer = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                TimerView.setText("Event Timer: " + millisUntilFinished / 1000);
            }

            public void onFinish() {

                ShowToast("Alarm Raised");
                StartEventTasks();
                TimerView.setText("");
            }
        }.start();

        Log.d(TAG, "StartTimer: End");
    }
    public void EndTimer()
    {
        Log.d(TAG, "EndTimer: Begin");
        if (Timer !=null)
        {
            Timer.cancel();
            timer = null;
            ShowToast("Event Cancelled");
            TimerView.setText("");
        }
        Log.d(TAG, "StartTimer: End");
    }

    public void Cancel(View view)
    {
        Log.d(TAG, "Cancel: Begin");
        //StopTimerTask();
        try
        {
            EndTimer();
            EndVibrate();
            EndAlarm();
            ShowToast("Event Cancelled");
        }
        catch(Exception e)
        {
            ShowToast("Error During Canceling");
        }

        EventButton.setClickable(true);
        EventButton.setEnabled(true);

        if (EventButton.isEnabled() && EventButton.isClickable())
        {
            CancelEventButton.setEnabled(false);
            CancelEventButton.setClickable(false);
        }

        Log.d(TAG, "Cancel: End");
    }

    public void OpenBluetooth(View view)
    {

        Log.d(TAG, "OpenBluetooth: Begin");
        Intent intent = new Intent(this, Bluetooth.class);
        startActivity(intent);
        Log.d(TAG, "OpenBluetooth: End");
    }
    public void OpenEmergencyContacts(View view)
    {
        Log.d(TAG, "OpenEmergencyContacts: Begin");
        Intent intent = new Intent(this, EmergencyContacts.class);
        startActivity(intent);
        Log.d(TAG, "OpenEmergencyContacts: End");
    }
    public void OpenFallData(View view)
    {
        Log.d(TAG, "OpenFallData: Begin");
        Intent intent = new Intent(this, FallData.class);
        startActivity(intent);
        Log.d(TAG, "OpenFallData: End");
    }
    public void OpenAlertSettings(View view)
    {
        Log.d(TAG, "OpenAlertSettings: Begin");
        Intent intent = new Intent(this, AlertSettings.class);
        startActivity(intent);
        Log.d(TAG, "OpenAlertSettings: End");
    }


}
