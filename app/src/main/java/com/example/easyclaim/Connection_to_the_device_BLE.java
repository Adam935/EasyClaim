package com.example.easyclaim;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.example.easyclaim.ble.BleExtensions;
import com.example.easyclaim.ble.ScanResultAdapter;
import com.example.easyclaim.ble.UUIDs;
import com.example.easyclaim.database.DataRecorder;
import com.example.easyclaim.database.HexToAsciiConverter;
import com.example.easyclaim.util.AppPermission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


public class Connection_to_the_device_BLE extends AppCompatActivity {

    // Creation of a DataRecorder object which will be used to save the data to a file
    private DataRecorder dataRecorder;
    private AppPermission appPermission;
    String Data_form_the_device = "data_for_easyclaim.txt";
    private ActivityResultLauncher<Intent> enableBluetoothLauncher;

    private Handler handler = new Handler();
    private int counter = 0; // Compteur pour suivre le nombre de lectures effectuées
    private ScanSettings scanSettings;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private Button scan_button;
    private RecyclerView scan_results_recycler_view;
    private ScanResultAdapter scanResultAdapter;

    private final List<ScanResult> scanResults = new ArrayList<>();
    // Déclarez une HashMap pour stocker les adresses MAC des périphériques déjà découverts
    private final HashMap<String, ScanResult> discoveredDevices = new HashMap<>();

    // Déclarez un ensemble pour stocker les valeurs uniques des caractéristiques
    private Set<String> uniqueValues = new HashSet<>();
    int transport = BluetoothDevice.TRANSPORT_LE; // Utilisation du transport BLE
    int phy = BluetoothDevice.PHY_LE_1M; // Utilisation du PHY LE à 1M

    private BluetoothGatt bluetoothGatt;

    // Create a queue to store the UUIDs of the characteristics to be read
    LinkedList<UUID> readQueue;
    private boolean isScanning = false;
    private boolean isReading = true;
    @Override
    protected void onStop() {
        super.onStop();
        if (bluetoothGatt != null) {
            // Check if the necessary Bluetooth permissions are granted
            while (!appPermission.checkBluetoothPermission()) {
                appPermission.requestBluetoothPermission();
            }
            if (appPermission.checkBluetoothPermission()) {
                bluetoothGatt.close();
                bluetoothGatt = null;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_to_the_device_ble);

        // Initialize AppPermission
        appPermission = new AppPermission(this);
        // Initialize DataRecorder
        dataRecorder = new DataRecorder(this);

        // Request to enable location settings
        appPermission.requestLocationSettings();

        // Request runtime permissions
        appPermission.requestRelevantRuntimePermissions();

        if (!appPermission.checkLocationPermission()) {
            appPermission.requestLocationPermission();
        }
        enableBluetoothLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // The user enabled Bluetooth
                        startBleScan(); // Start scanning for Bluetooth devices
                    } else {
                        // The user did not enable Bluetooth
                        Toast.makeText(this, "Bluetooth is required for this app to work", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Récupérer le bouton de numérisation
        scan_button = findViewById(R.id.scan_button);
        scan_results_recycler_view = findViewById(R.id.scan_results_recycler_view);




        scan_button.setOnClickListener(v -> {
            if (isScanning) {
                stopBleScan();
            } else {
                requestBluetoothPermission();
            }
        });


        // Initialisation de l'adaptateur
        setupAdapter();
        setupRecyclerView();


        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (bluetoothAdapter != null) {
            bleScanner = getBleScanner();
        }

        scanSettings = createScanSettings();

    }

    // Dans la méthode setupAdapter() de votre MainActivity
    private void setupAdapter() {
        scanResultAdapter = new ScanResultAdapter(scanResults, result -> {
            // User has tapped on a scan result
            if (isScanning) {
                stopBleScan();
            }
            BluetoothDevice device = result.getDevice();
            Log.w("ScanResultAdapter", "Connecting to " + device.getAddress());

            if (appPermission.checkBluetoothConnectPermission()) {
                // Connect to GATT Server hosted by this device
                bluetoothGatt = device.connectGatt(Connection_to_the_device_BLE.this, false, gattCallback, transport, phy);
            } else {
                appPermission.requestBluetoothConnectPermission();
            }
        });
    }
    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        scan_results_recycler_view.setLayoutManager(layoutManager);
        scan_results_recycler_view.setAdapter(scanResultAdapter);
        scan_results_recycler_view.setNestedScrollingEnabled(false);

        RecyclerView.ItemAnimator itemAnimator = scan_results_recycler_view.getItemAnimator();
        if (itemAnimator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }
    }


    public void startBleScan() {
        // Vider la liste des périphériques découverts
        discoveredDevices.clear();
        scanResults.clear();
        Log.d("WTFFFFF", "UUIDS A LIRE --> "+readQueue);
        if(readQueue != null){readQueue.clear();}
        // Initialisation of the queue of characteristics to be read
        readQueue = UUIDs.initializeCharacteristicQueue();

        Log.d("WTFFFFF", "UUIDS A LIRE --> "+readQueue);

        Log.d("errora", "Starting BLE scan...");
        if (!appPermission.hasRequiredRuntimePermissions()) {
            appPermission.requestRelevantRuntimePermissions();
        } else {
            runOnUiThread(() -> {
                scanResults.clear();
                if (scanResultAdapter != null) {
                    scanResultAdapter.notifyDataSetChanged();
                }
                Log.d("errora", "test thread ble scan");
            });
            // Check if Bluetooth is enabled
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                if (appPermission.checkBluetoothPermission()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    enableBluetoothLauncher.launch(enableBtIntent);
                } else {
                    appPermission.requestBluetoothPermission();
                }
                return;
            }
            // Check if device supports BLE
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Log.d("errora", "BLE not supported");
                Toast.makeText(this, "BLE not supported", Toast.LENGTH_SHORT).show();
                return;
            }
            // Check if BluetoothAdapter and BluetoothLeScanner are initialized
            if (bleScanner == null) {
                Log.d("errora", "Initializing BluetoothLeScanner...");
                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                if (bluetoothManager != null) {
                    bluetoothAdapter = bluetoothManager.getAdapter();
                    if (bluetoothAdapter != null) {
                        bleScanner = bluetoothAdapter.getBluetoothLeScanner();
                    }
                }
                Log.d("errora", "BluetoothLeScanner initialized.");
            }
            // Check if BluetoothLeScanner is not null before starting the scan
            if (bleScanner != null) {
                Log.d("errora", "Starting BLE scan... after check");
                bleScanner.startScan(null, scanSettings, scanCallback);
                isScanning = true;
                setIsScanning(true);
            } else {
                Log.e("MainActivity", "Unable to initialize BluetoothLeScanner.");
            }
        }
    }


    private void stopBleScan() {
        Log.d("errora", "Stopping BLE scan...");
        if (appPermission.checkBluetoothPermission()) {
            bleScanner.stopScan(scanCallback);

        } else {
            appPermission.requestBluetoothPermission();
        }
        setIsScanning(false);
    }

    private void setIsScanning(boolean value) {
        isScanning = value;
        runOnUiThread(() -> scan_button.setText(value ? "Stop Scan" : "Start Scan"));
    }





    private ScanSettings createScanSettings() {
        return new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
    }

    private BluetoothLeScanner getBleScanner() {
        return bluetoothAdapter.getBluetoothLeScanner();
    }

    // Ajoutez cette méthode à votre classe pour enregistrer les valeurs uniques dans le fichier
    private void saveUniqueValuesToFile() {
        for (String value : uniqueValues) {
            // Récupérer les données de la caractéristique
            // Appeler la méthode pour enregistrer les données dans un fichier
            dataRecorder.saveDataToFile(Data_form_the_device, value);
        }
        // After saving the data, finish the current activity and start Report_page1 activity
        finish();
        Intent intent = new Intent(Connection_to_the_device_BLE.this, Report_page1.class);
        startActivity(intent);
    }

    // Ajoutez ce Runnable pour arrêter la lecture après 5 secondes
    private Runnable stopReadingRunnable = new Runnable() {
        @Override
        public void run() {
            isReading = false;
        }
    };

    /**
     * Espace Callback
     */

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result != null && result.getDevice() != null) {
                BluetoothDevice device = result.getDevice();
                String deviceAddress = device.getAddress();

                // Check if the device has already been discovered
                if (!discoveredDevices.containsKey(deviceAddress)) {
                    // The device has not been discovered yet, add it to the results list
                    discoveredDevices.put(deviceAddress, result);
                    scanResults.add(result);
                    if (scanResultAdapter != null) {
                        int newPosition = scanResults.size() - 1;
                        scanResultAdapter.notifyItemInserted(newPosition);
                    }
                    // Créez le comparateur
                    Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
                        @Override
                        public int compare(ScanResult result1, ScanResult result2) {
                            String name1 = "Unnamed";
                            String name2 = "Unnamed";

                            // Check if the necessary Bluetooth permissions are granted
                            if (appPermission.checkBluetoothPermission()) {
                                if (result1.getDevice().getName() != null) {
                                    name1 = result1.getDevice().getName();
                                }
                                if (result2.getDevice().getName() != null) {
                                    name2 = result2.getDevice().getName();
                                }
                            } else {
                                appPermission.requestBluetoothPermission();
                            }

                            // Mettez "Unnamed" en dernier
                            if (name1.equals("Unnamed")) return 1;
                            if (name2.equals("Unnamed")) return -1;

                            // Comparez les dBm, mettez le plus élevé en premier
                            return Integer.compare(result2.getRssi(), result1.getRssi());
                        }
                    };

                    // Triez la liste des résultats de scan
                    scanResults.sort(comparator);

                    // Notifiez l'adaptateur que les données ont changé
                    scanResultAdapter.notifyDataSetChanged();
                } else {
                    // The device has already been discovered, do nothing
                }


            } else {
                Log.e("MainActivity", "ScanResult or BluetoothDevice is null.");
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("ScanCallback", "onScanFailed: code " + errorCode);
        }
    };



    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String deviceAddress = gatt.getDevice().getAddress();

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.w("BluetoothGattCallback", "Successfully connected to " + deviceAddress);
                    // Clear the data recorder
                    dataRecorder.clearData(Data_form_the_device);

                    // Discover services
                    bluetoothGatt = gatt;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (appPermission.checkBluetoothPermission()) {
                                bluetoothGatt.discoverServices();
                            } else {
                                appPermission.requestBluetoothPermission();
                            }
                        }
                    });
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.w("BluetoothGattCallback", "Successfully disconnected from " + deviceAddress);
                    gatt.close();
                }
            } else {
                Log.w("BluetoothGattCallback", "Error " + status + " encountered for " + deviceAddress + "! Disconnecting...");
                gatt.close();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.w("BluetoothGattCallback", "Discovered " + services.size() + " services for " + gatt.getDevice().getAddress());
            BleExtensions.printGattTable(gatt); // Show the discovered services and characteristics in the log


            // Appeler la méthode readNextCharacteristic pour lire la première caractéristique et lancez la lecture des autres caractéristiques.
            if (appPermission.checkBluetoothPermission()) {
                handler.postDelayed(stopReadingRunnable, 2000);
                gatt.readCharacteristic(BleExtensions.findCharacteristic(gatt, UUIDs.OBJECT_NAME));
            } else {
                appPermission.requestBluetoothPermission();
            }

        }
        @Override
        // Utiliser la table de correspondance dans la fonction onCharacteristicRead
        // pour afficher les valeurs de caractéristiques lues
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            String uuid = characteristic.getUuid().toString();
            String characteristicName = UUIDs.characteristicNames.get(characteristic.getUuid()); // Utilisez directement la table de correspondance

            if (characteristicName != null) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    byte[] value = characteristic.getValue();
                    if (value != null && value.length > 0) {
                        String hexValue = HexToAsciiConverter.toHexString(value);
                        Log.d("BluetoothGattCallback", "Read characteristic " + characteristicName + ":\n" + hexValue);

                        // Convertir la valeur hexadécimale en ASCII
                        String asciiValue = HexToAsciiConverter.hexToAscii(hexValue);

                        // Ajoutez la valeur à l'ensemble des valeurs uniques
                        uniqueValues.add(asciiValue);


                        /***************
                        // Récupérer les données de la caractéristique
                        String data = characteristicName +", Valeur : " + asciiValue;
                        // Appeler la méthode pour enregistrer les données dans un fichier
                        dataRecorder.saveDataToFile(Data_form_the_device,data);
                         ***************/

                    } else {
                        Log.e("BluetoothGattCallback", "Empty characteristic value for " + characteristicName + "!");
                    }
                } else if (status == BluetoothGatt.GATT_READ_NOT_PERMITTED) {
                    Log.e("BluetoothGattCallback", "Read not permitted for " + characteristicName + "!");
                } else {
                    Log.e("BluetoothGattCallback", "Characteristic read failed for " + characteristicName + ", error: " + status);
                }
            } else {
                Log.e("BluetoothGattCallback", "Unknown characteristic with UUID: " + uuid);
            }

            Log.d("DataRecorder", "Valeurs uniques : " + uniqueValues);
            Log.d("DataRecorder", String.valueOf(isReading));
            if (isReading) {
                readNextCharacteristic(gatt);
            }
            else{saveUniqueValuesToFile();}

            /***************
            // After reading a characteristic, remove it from the queue and read the next one
            //readQueue.poll();
            //readNextCharacteristic(gatt);
             ***************/
        }
        private void readNextCharacteristic(BluetoothGatt gatt) {
            if (!readQueue.isEmpty() && isReading) {
                UUID charUuid = readQueue.peek();
                BluetoothGattCharacteristic characteristic = BleExtensions.findCharacteristic(gatt, charUuid);
                if (characteristic != null && BleExtensions.isReadable(characteristic)) {
                    if (appPermission.checkBluetoothPermission()) {
                        gatt.readCharacteristic(characteristic);
                    } else {
                        appPermission.requestBluetoothPermission();
                    }
                } else {
                    /***************
                    //readQueue.poll(); // Remove the characteristic from the queue
                     ***************/
                    readNextCharacteristic(gatt); // Try to read the next characteristic
                }
            }
        }

    };

    /**
     * External thing ...
     */
    private void requestBluetoothPermission() {
        if (!appPermission.checkBluetoothPermission()) {
            appPermission.requestBluetoothPermission();
        } else {
            startBleScan();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Pass the result to the AppPermission class
        appPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
