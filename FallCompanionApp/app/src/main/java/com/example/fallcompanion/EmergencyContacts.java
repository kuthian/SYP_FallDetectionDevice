package com.example.fallcompanion;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EmergencyContacts extends AppCompatActivity {

    static public SharedPreferences prefs;

    private EditText ContactNumber1;
    private EditText ContactNumber2;
    private EditText ContactNumber3;
    private EditText ContactNumber4;

    private EditText ContactName1;
    private EditText ContactName2;
    private EditText ContactName3;
    private EditText ContactName4;

    private String SavedContactNumber1;
    private String SavedContactNumber2;
    private String SavedContactNumber3;
    private String SavedContactNumber4;

    private String SavedContactName1;
    private String SavedContactName2;
    private String SavedContactName3;
    private String SavedContactName4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        ContactNumber1 = (EditText) findViewById(R.id.contactNumber1);
        ContactNumber2 = (EditText) findViewById(R.id.contactNumber2);
        ContactNumber3 = (EditText) findViewById(R.id.contactNumber3);
        ContactNumber4 = (EditText) findViewById(R.id.contactNumber4);

        ContactName1 = (EditText) findViewById(R.id.contactName1);
        ContactName2 = (EditText) findViewById(R.id.contactName2);
        ContactName3 = (EditText) findViewById(R.id.contactName3);
        ContactName4 = (EditText) findViewById(R.id.contactName4);

        prefs = this.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);

        SavedContactNumber1 = "com.example.app.savedcontactnumber1";
        SavedContactNumber2 = "com.example.app.savedcontactnumber2";
        SavedContactNumber3 = "com.example.app.savedcontactnumber3";
        SavedContactNumber4 = "com.example.app.savedcontactnumber4";

        SavedContactName1 = "com.example.app.savedcontactname1";
        SavedContactName2 = "com.example.app.savedcontactname2";
        SavedContactName3 = "com.example.app.savedcontactname3";
        SavedContactName4 = "com.example.app.savedcontactname4";

        SetContactText();
    }

    public void SaveContacts(View view)
    {
        SaveContactValuesToPrefs();
        ShowSavedToast();
    }

    private void SetContactText()
    {
        ContactNumber1.setText(prefs.getString(SavedContactNumber1, "-"));
        ContactNumber2.setText(prefs.getString(SavedContactNumber2, "-"));
        ContactNumber3.setText(prefs.getString(SavedContactNumber3, "-"));
        ContactNumber4.setText(prefs.getString(SavedContactNumber4, "-"));

        ContactName1.setText(prefs.getString(SavedContactName1, "Contact Name #1"));
        ContactName2.setText(prefs.getString(SavedContactName2, "Contact Name #2"));
        ContactName3.setText(prefs.getString(SavedContactName3, "Contact Name #3"));
        ContactName4.setText(prefs.getString(SavedContactName4, "Contact Name #4"));
    }

    private void ShowSavedToast()
    {
        String SaveText = "Saved";
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, SaveText, duration);
        toast.show();
    }

    private void SaveContactValuesToPrefs()
    {
        String SaveContactNumber1 = ContactNumber1.getText().toString();
        String SaveContactNumber2 = ContactNumber2.getText().toString();
        String SaveContactNumber3 = ContactNumber3.getText().toString();
        String SaveContactNumber4 = ContactNumber4.getText().toString();

        prefs.edit().putString(SavedContactNumber1,SaveContactNumber1).apply();
        prefs.edit().putString(SavedContactNumber2,SaveContactNumber2).apply();
        prefs.edit().putString(SavedContactNumber3,SaveContactNumber3).apply();
        prefs.edit().putString(SavedContactNumber4,SaveContactNumber4).apply();

        String SaveContactName1 = ContactName1.getText().toString();
        String SaveContactName2 = ContactName2.getText().toString();
        String SaveContactName3 = ContactName3.getText().toString();
        String SaveContactName4 = ContactName4.getText().toString();

        prefs.edit().putString(SavedContactName1,SaveContactName1).apply();
        prefs.edit().putString(SavedContactName2,SaveContactName2).apply();
        prefs.edit().putString(SavedContactName3,SaveContactName3).apply();
        prefs.edit().putString(SavedContactName4,SaveContactName4).apply();
    }
}

