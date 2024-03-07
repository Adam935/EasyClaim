package com.example.easyclaim.ble;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

public class UUIDs {
    // UUID of the characteristics to be read from the device
    public static final UUID OBJECT_NAME = UUID.fromString("00002abe-0000-1000-8000-00805f9b34fb");
    public static final UUID BATTERY_LEVEL_CHAR = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    public static final UUID BATTERY_STATUS_CHAR = UUID.fromString("00002bea-0000-1000-8000-00805f9b34fb");
    public static final UUID TESTNAME_SPECIAL_ONE = UUID.fromString("00005678-0000-1000-8000-00805f9b34fb");

    public static LinkedList<UUID> initializeCharacteristicQueue() {
        LinkedList<UUID> readQueue = new LinkedList<>();
        readQueue.add(OBJECT_NAME);
        /*
        readQueue.add(BATTERY_LEVEL_CHAR);
        readQueue.add(BATTERY_STATUS_CHAR);
        readQueue.add(TESTNAME_SPECIAL_ONE);
        */
        return readQueue;
    }


    // Table of the names of the characteristics to be displayed in the app UI
    public static final HashMap<UUID, String> characteristicNames = new HashMap<>();

    static {
        // Add the names of the characteristics to the table
        characteristicNames.put(OBJECT_NAME, "Object Name: ");
        characteristicNames.put(BATTERY_LEVEL_CHAR, "Battery Health Information");
        characteristicNames.put(BATTERY_STATUS_CHAR, "Battery Health Status");
        characteristicNames.put(TESTNAME_SPECIAL_ONE, "Test Name Special One");
    }
}
