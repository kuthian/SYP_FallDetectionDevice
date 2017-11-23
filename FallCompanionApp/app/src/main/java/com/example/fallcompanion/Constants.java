package com.example.fallcompanion;

import java.util.UUID;

public class Constants {

    public static String SERVICE_STRING = "0000FFE0-0000-1000-8000-00805F9B34FB";
    public static UUID SERVICE_UUID = UUID.fromString(SERVICE_STRING);

    public static String CHARACTERISTIC_ECHO_STRING = "0000FFE1-0000-1000-8000-00805F9B34FB";
    public static UUID CHARACTERISTIC_ECHO_UUID = UUID.fromString(CHARACTERISTIC_ECHO_STRING);

    public static final long SCAN_PERIOD = 5000;
}
