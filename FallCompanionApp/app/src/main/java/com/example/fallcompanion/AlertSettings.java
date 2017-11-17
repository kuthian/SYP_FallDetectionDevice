package com.example.fallcompanion;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class AlertSettings extends AppCompatActivity {

    SharedPreferences prefs;
    private TextView SeekBarValue;
    private Switch OnOrOffSwitch;
    private String SavedSeekBarValue;
    private String SavedOnOrOFf;
    private int SeekBarInterval = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_settings);

        prefs = this.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);

        SavedSeekBarValue = "com.example.app.savedseekbarvalue";
        SavedOnOrOFf = "com.example.app.savedonoroff";

        OnOrOffSwitch = (Switch) findViewById(R.id.OnOrOffSwitch);
        SeekBarValue = (TextView) findViewById(R.id.SeekBarValue);

        final SeekBar CountdownSeekBar = (SeekBar) findViewById(R.id.CountdownSeekBar);
        final Button SaveSettingsButton = (Button) findViewById(R.id.SaveSettingsButton);

        SeekBarValue.setText(prefs.getString(SavedSeekBarValue, "-"));
        OnOrOffSwitch.setChecked(prefs.getBoolean(SavedOnOrOFf, true));
        CountdownSeekBar.setProgress(Integer.parseInt(prefs.getString(SavedSeekBarValue, "30"))/ SeekBarInterval);

        CountdownSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar CountdownSeekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar CountdownSeekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar CountdownSeekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                SeekBarValue.setText(String.valueOf(progress* SeekBarInterval));
            }

        });

        SaveSettingsButton.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v)
            {
                String SaveSeekBarValue = SeekBarValue.getText().toString();
                Boolean SaveOnOrOffSwitch = OnOrOffSwitch.isChecked();

                prefs.edit().putString(SavedSeekBarValue, SaveSeekBarValue).apply();
                prefs.edit().putBoolean(SavedOnOrOFf, SaveOnOrOffSwitch).apply();

                ShowToast("Saved");
            }
        });
    }

    public void ShowToast(String ToastMessage)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, ToastMessage, duration);
        toast.show();
    }

}
