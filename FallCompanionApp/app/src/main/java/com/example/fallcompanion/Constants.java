package com.example.fallcompanion;

import java.util.UUID;

public class Constants {

    public static String SERVICE_STRING = "0000FFE0-0000-1000-8000-00805F9B34FB";
    public static UUID SERVICE_UUID = UUID.fromString(SERVICE_STRING);


    public static String CHARACTERISTIC_ECHO_STRING = "0000FFE1-0000-1000-8000-00805F9B34FB";
    public static UUID CHARACTERISTIC_ECHO_UUID = UUID.fromString(CHARACTERISTIC_ECHO_STRING);

    public static final long SCAN_PERIOD = 5000;

    //SharedPrefs locations
    public static String SAVED_NUMBER_LOCATION_1 = "com.example.app.savedcontactnumber1";
    public static String SAVED_NUMBER_LOCATION_2 = "com.example.app.savedcontactnumber2";
    public static String SAVED_NUMBER_LOCATION_3 = "com.example.app.savedcontactnumber3";
    public static String SAVED_NUMBER_LOCATION_4 = "com.example.app.savedcontactnumber4";

    public static String SAVED_NAME_LOCATION_1 = "com.example.app.savedcontactname1";
    public static String SAVED_NAME_LOCATION_2 = "com.example.app.savedcontactname2";
    public static String SAVED_NAME_LOCATION_3 = "com.example.app.savedcontactname3";;
    public static String SAVED_NAME_LOCATION_4 = "com.example.app.savedcontactname4";

    public static String SAVED_SEEKBAR_VALUE_LOCATION = "com.example.app.savedseekbarvalue";
    public static String SAVED_ON_OR_OFF_LOCATION = "com.example.app.savedonoroff";

}
