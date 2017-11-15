package com.example.fallcompanion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class AlertSettings extends AppCompatActivity {

//    public SeekBar CountdownSeekBar;
    private Button SaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_settings);

        final TextView SeekBarValue = (TextView) findViewById(R.id.SeekBarValue);
        final SeekBar CountdownSeekBar = (SeekBar) findViewById(R.id.CountdownSeekBar);
        SaveButton = (Button) findViewById(R.id.SaveButton);


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

/*        CountdownSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()

    {
        @Override
        public void onProgressChanged (SeekBar CountdownSeekbar ,int progress,
        boolean fromUser){
        // TODO Auto-generated method stub
        SeekBarValue.setText(String.valueOf(progress));
    }*/
}
