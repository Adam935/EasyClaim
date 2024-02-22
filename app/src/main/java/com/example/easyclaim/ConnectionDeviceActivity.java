package com.example.easyclaim;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class ConnectionDeviceActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> pairedDevicesAdapter;
    private ArrayAdapter<String> scannedDevicesAdapter;
    private ArrayList<BluetoothDevice> scannedDevicesList;
    private ArrayList<BluetoothDevice> pairedDevicesList; // List of BluetoothDevice

    private ListView pairedListView;
    private ListView scannedListView;
    private Button startScanButton;
    private Button stopScanButton;
    private BTPermission btPermission;
    private static final int REQUEST_ENABLE_BT = 1;

    private final BroadcastReceiver bondStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d("ConnectionDeviceActivity", "onReceive: called. for change in bond state.");
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    if (btPermission.checkBluetoothPermission() && btPermission.checkBluetoothConnectPermission()) {
                        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                            // Device has been paired
                            Log.d("ConnectionDeviceActivity", "Device paired: " + device.getName());
                        } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                            // Device has been unpaired
                            Log.d("ConnectionDeviceActivity", "Device unpaired: " + device.getName());
                        } else if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                            // Pairing in progress
                            Log.d("ConnectionDeviceActivity", "Pairing in progress: " + device.getName());
                        }
                    } else {
                        Log.d("ConnectionDeviceActivity", "Bluetooth permissions not granted");
                    }
                }
            }
        }
    };

    private final BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d("ConnectionDeviceActivity", "onReceive: called. for change in bluetooth state.");
            assert action != null;
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                Log.d("ConnectionDeviceActivity", "onReceive: Bluetooth state changed.");
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON) {
                    displayPairedDevices();
                }
            }
        }
    };

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d("ConnectionDeviceActivity", "onReceive: called.");
            String action = intent.getAction();
            if (btPermission.checkBluetoothPermission()) {
                Log.d("ConnectionDeviceActivity", "onReceive: Bluetooth permissions granted.");
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    Log.d("ConnectionDeviceActivity", "onReceive: ACTION_FOUND.");
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null) {
                        Log.d("ConnectionDeviceActivity", "Device found: " + device.getName() + " - " + device.getAddress());
                        if (!scannedDevicesList.contains(device)) {
                            scannedDevicesList.add(device);
                            scannedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                            scannedDevicesAdapter.notifyDataSetChanged(); // Notifiez l'adaptateur des changements
                        }
                    }
                    else{
                        Log.d("ConnectionDeviceActivity", "No device found");
                        Toast.makeText(getApplicationContext(), "No device found", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                btPermission.requestBluetoothPermission();
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_device_activity);
        btPermission = new BTPermission(this);
        pairedListView = findViewById(R.id.paired_devices_list);
        scannedListView = findViewById(R.id.scanned_devices_list);
        pairedDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        scannedDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        scannedDevicesList = new ArrayList<>();
        pairedDevicesList = new ArrayList<>(); // Initialize pairedDevicesList here
        pairedListView.setAdapter(pairedDevicesAdapter);
        scannedListView.setAdapter(scannedDevicesAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        startScanButton = findViewById(R.id.start_scan_button);
        stopScanButton = findViewById(R.id.stop_scan_button);
        startScanButton.setOnClickListener(v -> {
            if (btPermission.checkBluetoothScanPermission()) {
                startScan();
            } else {
                btPermission.requestBluetoothScanPermission();
            }
        });
        stopScanButton.setOnClickListener(v -> stopScan());

        // Setup the list view item click listeners for connecting to a device
        setupListViewClickListener_for_connection_device();
        // Setup the list view item click listeners for bonding a device
        setupListViewClickListener_for_bonding();

        Log.d("ConnectionDeviceActivity", "testtttt: called.");
        // Register the bondStateReceiver
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(bondStateReceiver, filter2);
        if (bluetoothAdapter == null) {

            Toast.makeText(getApplicationContext(), "Device doesn't support Bluetooth", Toast.LENGTH_SHORT).show();

        } else {

            // Check if Bluetooth is enabled
            if (!bluetoothAdapter.isEnabled()) {
                // Bluetooth is not enabled, request to enable it
                Log.d("ConnectionDeviceActivity", "Bluetooth is not enabled, request to enable it");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(bluetoothStateReceiver, filter);

            } else {
                // Bluetooth is enabled, proceed with your operations
                Log.d("ConnectionDeviceActivity", "Bluetooth is enabled, proceed with your operations");
                registerReceiver(); // Register the Bluetooth receiver once permissions are granted
                if (btPermission.checkBluetoothConnectPermission()) {
                    Log.d("ConnectionDeviceActivity", "Bluetooth connect permission granted");
                    displayPairedDevices();
                } else {
                    btPermission.requestBluetoothConnectPermission();
                }
            }
        }
    }

    private void displayPairedDevices() {
        Log.d("ConnectionDeviceActivity", "displayPairedDevices: Displaying paired devices.");
        if (btPermission.checkBluetoothPermission()) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                    pairedDevicesList.add(device);
                    scannedDevicesList.add(device);
                }
            } else {
                Toast.makeText(getApplicationContext(), "No paired devices found", Toast.LENGTH_SHORT).show();
            }
        } else {
            btPermission.requestBluetoothPermission();
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, filter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        btPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BTPermission.REQUEST_BLUETOOTH_CONNECT_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Bluetooth connect permission was granted, start the scan
                startScan();
            } else {
                // Bluetooth connect permission was denied. Handle the failure.
                Toast.makeText(getApplicationContext(), "Bluetooth connect permission was denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == BTPermission.REQUEST_BLUETOOTH_SCAN_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Bluetooth scan permission was granted, start the scan
                startScan();
            } else {
                // Bluetooth scan permission was denied. Handle the failure.
                Toast.makeText(getApplicationContext(), "Bluetooth scan permission was denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void startScan() {
        Log.d("ConnectionDeviceActivity", "startScan: Attempting to start scan...");
        if (btPermission.checkBluetoothPermission()) {
            Log.d("ConnectionDeviceActivity", "startScan: Bluetooth permissions granted.");
            if (!btPermission.checkLocationPermission()) {
                Log.d("ConnectionDeviceActivity", "startScan: Location permissions not granted. Requesting permissions...");
                btPermission.requestLocationPermission();
            } else {
                Log.d("ConnectionDeviceActivity", "startScan: Location permissions granted.");
                if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                    Log.d("ConnectionDeviceActivity", "startScan: Bluetooth is enabled. Starting discovery...");
                    if (bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                        Log.d("ConnectionDeviceActivity", "startScan: Cancelling ongoing discovery...");
                    }
                    scannedDevicesAdapter.clear();
                    scannedDevicesList.clear();
                    boolean discoveryStarted = bluetoothAdapter.startDiscovery();
                    Log.d("ConnectionDeviceActivity", "startScan: Discovery started: " + discoveryStarted);

                    // Affichage d'un Toast pour informer que la découverte a commencé
                    Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("ConnectionDeviceActivity", "startScan: Bluetooth is not enabled or supported.");
                    // Bluetooth is not enabled or supported
                    // Handle this case if needed
                    Toast.makeText(getApplicationContext(), "Bluetooth is not enabled or supported", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Log.d("ConnectionDeviceActivity", "startScan: Bluetooth permissions not granted. Requesting permissions...");
            btPermission.requestBluetoothPermission();
        }
    }

    private void stopScan() {
        if (btPermission.checkBluetoothPermission()) {
            if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
                Toast.makeText(getApplicationContext(), "Scan stopped", Toast.LENGTH_SHORT).show();
            }
        } else {
            btPermission.requestBluetoothPermission();
        }
    }

    private void setupListViewClickListener_for_bonding() {
        scannedListView.setOnItemClickListener((parent, view, position, id) -> {
            if (btPermission.checkBluetoothPermission() && btPermission.checkLocationPermission() && btPermission.checkBluetoothConnectPermission()) {
                if (scannedDevicesList != null && !scannedDevicesList.isEmpty() && position < scannedDevicesList.size()) {
                    BluetoothDevice device = scannedDevicesList.get((int) position); // Get the BluetoothDevice
                    if (device != null) {
                        String deviceName = device.getName(); // Retrieve the device name
                        Log.d("ConnectionDeviceActivity", "Clicked on device: " + deviceName);
                        Log.d("ConnectionDeviceActivity", "Clicked on device bond getBondState: " + device.getBondState());
                        Log.d("ConnectionDeviceActivity", "Clicked on device: " + device.getAddress());
                        // Pair the device
                        new Thread(() -> {
                            try {
                                Log.d("ConnectionDeviceActivity", "testbefore paring");
                                device.createBond();
                                Log.d("ConnectionDeviceActivity", "testafter paring");
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Pairing with " + deviceName, Toast.LENGTH_SHORT).show());
                            } catch (Exception e) {
                                Log.e("ConnectionDeviceActivity", "Error pairing device", e);
                            }
                        }).start();
                    }
                } else {
                    Log.d("ConnectionDeviceActivity", "Clicked position is out of bounds of the pairedDevicesList");
                }
            } else {
                btPermission.requestBluetoothPermission();
                btPermission.requestLocationPermission();
                btPermission.requestBluetoothConnectPermission();
            }
        });
    }


    private void setupListViewClickListener_for_connection_device() {
        pairedListView.setOnItemClickListener((parent, view, position, id) -> {
            if (btPermission.checkBluetoothPermission() && btPermission.checkLocationPermission() && btPermission.checkBluetoothConnectPermission()) {
                if (pairedDevicesList != null && !pairedDevicesList.isEmpty() && position < pairedDevicesList.size()) {
                    BluetoothDevice device = pairedDevicesList.get((int) position); // Get the BluetoothDevice
                    if (device != null) {
                        String deviceName = device.getName(); // Retrieve the device name
                        Log.d("ConnectionDeviceActivity", "Clicked on device: " + deviceName);
                        Log.d("ConnectionDeviceActivity", "Clicked on device bond getBondState: " + device.getBondState());
                        Log.d("ConnectionDeviceActivity", "Clicked on device: " + device.getAddress());

                        // Connect to the device
                        new Thread(() -> {
                            try {
                                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Standard SerialPortService ID
                                try (BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid)) {
                                    socket.connect();
                                    Log.d("ConnectionDeviceActivity", "Device connected: " + deviceName);
                                }
                            } catch (Exception e) {
                                Log.e("ConnectionDeviceActivity", "Error connecting to device", e);
                            }
                        }).start();
                    }
                } else {
                    Log.d("ConnectionDeviceActivity", "Clicked position is out of bounds of the pairedDevicesList");
                }
            } else {
                btPermission.requestBluetoothPermission();
                btPermission.requestLocationPermission();
                btPermission.requestBluetoothConnectPermission();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);
        unregisterReceiver(bluetoothStateReceiver);
        unregisterReceiver(bondStateReceiver); // Unregister the bondStateReceiver
    }
}