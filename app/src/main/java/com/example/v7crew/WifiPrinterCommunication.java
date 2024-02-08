package com.example.v7crew;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class WifiPrinterCommunication {
    private Socket wifiSocket;
    private OutputStream wifiOutputStream;

    public WifiPrinterCommunication(String printerIpAddress, int printerPort) {
        new ConnectTask().execute(printerIpAddress, String.valueOf(printerPort));
    }

    public void sendPrintData(byte[] data) {
        if (wifiOutputStream != null && wifiSocket.isConnected()) {
            try {
                wifiOutputStream.write(data);
                wifiOutputStream.flush();
                Log.w("WifiPrinterCommunication", "Data sent");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.w("WifiPrinterCommunication", "Socket is not connected");
        }
    }

    public void closeConnection() {
        if (wifiSocket != null) {
            try {
                wifiSocket.close();
                System.out.println("WiFi socket closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                wifiSocket = new Socket(params[0], Integer.parseInt(params[1]));
                wifiOutputStream = wifiSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
