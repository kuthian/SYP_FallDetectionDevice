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

public class AlertSettings extends AppCompatActivity {

    SharedPreferences prefs;
    private TextView SeekBarValue;
    private Switch OnOrOffSwitch;
    private String SavedSeekBarValue;
    private String SavedOnOrOFf;

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
        final Button SaveButton = (Button) findViewById(R.id.SaveButton);

        SeekBarValue.setText(prefs.getString(SavedSeekBarValue, "-"));
        OnOrOffSwitch.setChecked(prefs.getBoolean(SavedOnOrOFf, true));
        CountdownSeekBar.setProgress(Integer.parseInt(prefs.getString(SavedSeekBarValue, "30")));



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
                SeekBarValue.setText(String.valueOf(progress));
            }

        });
    }


    public void SaveContactValuesToPrefs(View view)
    {
        String SaveSeekBarValue = SeekBarValue.getText().toString();
        Boolean SaveOnOrOffSwitch = OnOrOffSwitch.isChecked();


        prefs.edit().putString(SavedSeekBarValue,SaveSeekBarValue).commit();
        prefs.edit().putBoolean(SavedOnOrOFf,SaveOnOrOffSwitch).commit();
    }
}
