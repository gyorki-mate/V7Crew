package com.example.v7crew;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import www.fiscat.aeedriverlib.AEEService;

public class MyServiceConnection implements ServiceConnection {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        AEEService.AEEBinder binder = (AEEService.AEEBinder) service;
        // A szolgáltatás csatlakozott, ebben a metódusban végzed el a szükséges műveleteket
        Log.i("MyServiceConnection", "onServiceConnected() started");
        // Például elmentheted a szolgáltatás hivatkozást, hogy kommunikálni tudj vele

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        // A szolgáltatás kapcsolata megszakadt
    }
}
