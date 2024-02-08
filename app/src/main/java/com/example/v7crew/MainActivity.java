package com.example.v7crew;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
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
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import www.fiscat.aeedriverlib.AEEService;
import www.fiscat.aeedriverlib.Config;
import www.fiscat.aeedriverlib.ServiceConfig;

import android.Manifest;

import java.io.FileOutputStream;
import java.io.OutputStream;

import com.fiscat.www.libhid.BuildConfig;
import com.fiscat.www.libhid.HidItem;

public class MainActivity extends AppCompatActivity {
    private final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private final String CONST_StoragePath = "www.fiscat.aeedriverlib.storage_path";
    MyServiceConnection serviceConnection = new MyServiceConnection();
    Button button;
    Button button2;
    www.fiscat.aeedriverlib.Config config = new www.fiscat.aeedriverlib.Config();
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123; // Ez csak egy példa, az érték lehet bármi
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 456;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 789;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Activity activity = this;
        Context appContext = activity.getApplicationContext();
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
//        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request write external storage permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request read external storage permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }

        button.setOnClickListener(v -> CloseService());
        button2.setOnClickListener(v -> openHID());
            initializeApp(appContext);

    }

    private void initializeApp(Context appContext)  {
        String storagePath = appContext.getFilesDir().getPath();

        config.setStoreFolder(appContext, storagePath);

        config.setAEEConnMode(Config.CONNMODE_HID);
        config.setAEEHost(Config.HIDCONN);
        config.setAEEPort("");

        try {
            OutputStream outputStream = new FileOutputStream(String.format("%s/AEEConfig.xml", appContext.getFilesDir()));
            config.writeConfig(outputStream, config.getClass());

        } catch (Exception e) {
            Log.d("MainActivity", "initializeApp: " + e.getMessage());
        }

        Intent intent = new Intent(this, AEEService.class);
        intent.putExtra(CONST_StoragePath, storagePath);

        startService(intent);

        Log.w("MainActivity", "startService() called successfully");

        boolean bindServiceResult = appContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
        if (bindServiceResult) {
            Log.i("MainActivity", "bindService() called successfully");
//            WifiPrinterCommunication wifiPrinterCommunication = new WifiPrinterCommunication("192.168.1.51",1124);
//            wifiPrinterCommunication.sendPrintData(new byte[]{0x1B, 0x40});
//            hidItem.writeBuffer(new byte[]{0x1B, 0x40}, 2, 1000);


        } else {
            Log.i("MainActivity", "bindService() called unsuccessfully");
        }
    }

    protected void CloseService() {
        Context appContext = this.getApplicationContext();
        appContext.unbindService(serviceConnection);
        Intent intent = new Intent(appContext, AEEService.class);
        appContext.stopService(intent);
    }

    protected void openHID() {
        HidItem hidItem = new HidItem(this.getApplicationContext());
        try {
            hidItem.open();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}