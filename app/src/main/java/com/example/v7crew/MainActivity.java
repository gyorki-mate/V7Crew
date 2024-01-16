package com.example.v7crew;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
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
    private final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private final String CONST_StoragePath = "www.fiscat.aeedriverlib.storage_path";
    MyServiceConnection serviceConnection = new MyServiceConnection();
    private AEEService aeeServiceInstance;
    private HidItem hidItem;
    private UsbManager usbManager;
    private AEEService.AEEBinder aeeBinder;
    Button button;
    Button button2;
    www.fiscat.aeedriverlib.Config config = new www.fiscat.aeedriverlib.Config();
    www.fiscat.aeedriverlib.AEEService service = new AEEService();
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123; // Ez csak egy példa, az érték lehet bármi
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = this;
        Context appContext = activity.getApplicationContext();
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request write external storage permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request read external storage permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        // Request USB permission dynamically
//        performUsbInteraction();


        button.setOnClickListener(v -> CloseService());
        button2.setOnClickListener(v -> performUsbInteraction());
        initializeApp(appContext);

    }

    private void initializeApp(Context appContext) {
        String storagePath = appContext.getFilesDir().getPath();

        config.setStoreFolder(appContext, storagePath);

        config.setAEEConnMode(Config.CONNMODE_LAN);
        config.setAEEHost("192.168.1.51");
        config.setAEEPort("1124");

        try {
            OutputStream outputStream = new FileOutputStream(String.format("%s/AEEConfig.xml",appContext.getFilesDir()));
            config.writeConfig(outputStream, config.getClass());

        } catch (Exception e) {
            Log.d("MainActivity", "initializeApp: " + e.getMessage());
        }

        Intent intent = new Intent(this, AEEService.class);
        intent.putExtra(CONST_StoragePath, storagePath);

        startService(intent);

        // Call the pairing simulation method
//        simulatePairing("123456"); // Replace with the

        Log.w("MainActivity", "startService() called successfully");

        // TODO: use aeeBinder to call the service methods
        boolean bindServiceResult = appContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
        if (bindServiceResult) {

            // TODO: check this shid
//            Log.i("MainActivity", "bindService() called successfully");
//            boolean recreateResult = aeeBinder.reCreateBLESocket(500);
//            if (recreateResult) {
//                // BLE socket recreated successfully
//                Log.d("MainActivity", "BLE socket recreated successfully");
//            } else {
//                // Failed to recreate BLE socket
//                Log.e("MainActivity", "Failed to recreate BLE socket.");
//            }
//            aeeBinder.startService(storagePath);

        } else {
            Log.i("MainActivity", "bindService() called unsuccessfully");
        }

        //TODO: use findDevice to find the printer from AEEService
//      UsbDevice usbDevice = HidItem.findDevice(usbManager, (short) 0x0483, (short) 0x5750);
//        if (usbDevice != null) {
//            // Request USB permission dynamically
//            requestUsbPermission(usbDevice);
//
//            try {
//                // Open the USB connection after permission is granted
//                usbManager.openDevice(usbDevice);
//                Log.w("MainActivity", "openDevice() called successfully");
//
//                // Perform USB interaction here
//                // ...
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                // Handle exceptions
//            } finally {
//                // Close the USB connection if necessary
//                // ...
//            }
//
//        } else {
//            Log.e(TAG, "USB device not found.");
//            // Handle accordingly
//        }
    }

    protected void CloseService() {
        Context appContext = this.getApplicationContext();
        appContext.unbindService(serviceConnection);
        Intent intent = new Intent(appContext, AEEService.class);
        appContext.stopService(intent);
    }


//    private void performUsbInteraction() {
//        // Find USB Device
//        short productId = 001;
//        short vendorId = 001;
//
//        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
//        HidItem usbDevice = new HidItem(this);
//        usbDevice.setProductID(productId);
//        usbDevice.setVendorID(vendorId);
//
//        // Check if the USB device is found
//        if (HidItem.findDevice(usbManager, productId, vendorId) != null) {
//            try {
//                // Open the USB connection
//                usbDevice.open();
//
//                // Send data
//                String dataToSend = "Hello, USB Device!";
//                usbDevice.writeBuffer(dataToSend.getBytes(), 0, dataToSend.length());
//            } catch (Exception e) {
//                e.printStackTrace();
//                // Handle exceptions
//            } finally {
//                // Close the USB connection
//                usbDevice.close();
//            }
//        } else {
//            Log.e(TAG, "USB device not found.");
//            // Handle accordingly
//        }
//    }

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

    private void requestUsbPermission(UsbDevice usbDevice) {
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
        usbManager.requestPermission(usbDevice, permissionIntent);
    }

    private void performUsbInteraction() {

        //TODO: aeeservice getdevice 🥶🥶🥶🥶

        // Find USB Device
        short productId = 0x0483;
        short vendorId = 0x5750;

        // Check if the USB device is found
        UsbDevice usbDevice = HidItem.findDevice(usbManager, productId, vendorId);

        if (usbDevice != null) {
            // Request USB permission dynamically
            requestUsbPermission(usbDevice);

            try {
                // Open the USB connection after permission is granted
                usbManager.openDevice(usbDevice);


                // Perform USB interaction here
                // ...

            } catch (Exception e) {
                e.printStackTrace();
                // Handle exceptions
            } finally {
                // Close the USB connection if necessary
                // ...
            }
        } else {
            Log.e(TAG, "USB device not found.");
            // Handle accordingly
        }
    }


}