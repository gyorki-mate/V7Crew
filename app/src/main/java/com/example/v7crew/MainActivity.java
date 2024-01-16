package com.example.v7crew;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.icu.util.Output;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import www.fiscat.aeedriverlib.AEEService;
import www.fiscat.aeedriverlib.Config;

import android.Manifest;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import com.fiscat.www.libhid.HidItem;

public class MainActivity extends AppCompatActivity {

    private final String CONST_StoragePath = "www.fiscat.aeedriverlib.storage_path";
    MyServiceConnection serviceConnection = new MyServiceConnection();
    private AEEService aeeServiceInstance;
    private HidItem hidItem;
    private AEEService.AEEBinder aeeBinder;
    Button button;
    Button button2;
    www.fiscat.aeedriverlib.Config config = new www.fiscat.aeedriverlib.Config();
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123; // Ez csak egy példa, az érték lehet bármi
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = this;
        Context appContext = activity.getApplicationContext();
        aeeServiceInstance = new AEEService();
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request write external storage permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request read external storage permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }


        button.setOnClickListener(v -> CloseService());
        button2.setOnClickListener(v -> performUsbInteraction());
        initializeApp(appContext);

    }

    private void initializeApp(Context appContext) {
        String storagePath = appContext.getFilesDir().getPath();

        config.setStoreFolder(appContext, storagePath);

        config.setAEEConnMode(Config.CONNMODE_HID);
        config.setAEEHost(Config.HIDCONN);
        config.setAEEPort("0");

        try {
            OutputStream outputStream = new FileOutputStream(String.format("%s/AEEConfig.xml",appContext.getFilesDir()));
            config.writeConfig(outputStream, config.getClass());

        } catch (Exception e) {
            Log.d("MainActivity", "initializeApp: " + e.getMessage());
        }

        Intent intent = new Intent(this, AEEService.class);
        intent.putExtra(CONST_StoragePath, storagePath);

        startService(intent);
        aeeBinder = new AEEService().new AEEBinder();

        // Call the pairing simulation method
//        simulatePairing("123456"); // Replace with the

        Log.w("MainActivity", "startService() called successfully");
        boolean bindServiceResult = appContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
        if (bindServiceResult) {
            Log.i("MainActivity", "bindService() called successfully");
//            hidItem = new HidItem(getApplicationContext());
//            try {
//                hidItem.setProductID((short) 00001);
//                hidItem.setVendorID((short) 00001);
//
//                hidItem.open();
//
//                byte[] dataToSend = "Hello, Printer!".getBytes();
//                hidItem.writeBuffer(dataToSend, 0, dataToSend.length);
//
//                ByteArrayOutputStream receivedData = new ByteArrayOutputStream();
//                int bytesRead = hidItem.readBuffer(receivedData, 1000); // 1000ms timeout
//                if (bytesRead > 0) {
//                    Log.d("YourActivity", "Received data: " + receivedData.toString());
//                } else {
//                    Log.d("YourActivity", "No data received within the timeout");
//                }
//            } catch (Exception e) {
//                Log.d("MainActivity", "initializeApp: " + e.getMessage());
            }
//
//        } else {
//            Log.i("MainActivity", "bindService() called unsuccessfully");
//        }
    }

    protected void CloseService() {
        Context appContext = this.getApplicationContext();
        appContext.unbindService(serviceConnection);
        Intent intent = new Intent(appContext, AEEService.class);
        appContext.stopService(intent);
    }

    protected void writeConfig() {
        config.setAEEConnMode(2);
        config.setAEEHost("ANYÉD");
        config.setAEEPort("26241");

        String dataText = config.toString();
        config.writeConfig(dataText);

        Log.w("MainActivity", "writeConfig() called successfully");

    }

    private void performUsbInteraction() {
        // Find USB Device
        short productId = 001;
        short vendorId = 001;

        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HidItem usbDevice = new HidItem(this);
        usbDevice.setProductID(productId);
        usbDevice.setVendorID(vendorId);

        // Check if the USB device is found
        if (HidItem.findDevice(usbManager, productId, vendorId) != null) {
            try {
                // Open the USB connection
                usbDevice.open();

                // Send data
                String dataToSend = "Hello, USB Device!";
                usbDevice.writeBuffer(dataToSend.getBytes(), 0, dataToSend.length());
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exceptions
            } finally {
                // Close the USB connection
                usbDevice.close();
            }
        } else {
            Log.e(TAG, "USB device not found.");
            // Handle accordingly
        }
    }

    private void simulatePairing(String sModeCode) {
        Context appContext = this.getApplicationContext();
        String storagePath = appContext.getFilesDir().getPath();

        try {
            // Recreate BLE socket
            if (aeeBinder.reCreateBLESocket(500)) {
                // BLE socket recreated successfully, attempt to send the pairing command
                String pairingCommand = String.format("pdbypsw\t%s", sModeCode);

                // Simulate starting the service (startListening)
                aeeBinder.startService(storagePath); // Replace with the actual storage path

                // Simulate sending the pairing command to the printer
                // cant do that lmao

                Log.d("MainActivity", "Pairing command sent: " + pairingCommand);
            } else {
                Log.e("MainActivity", "Failed to recreate BLE socket.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}