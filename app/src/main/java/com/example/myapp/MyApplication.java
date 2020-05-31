package com.example.myapp;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.myapp.DriveActivity;
import com.example.myapp.TinyDB;

import java.io.IOException;
import java.util.UUID;

public class MyApplication extends Application
{

    private static MyApplication sInstance;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private  BluetoothAdapter bluetoothAdapter;
    private boolean isBtConnected = false;
    String myAddress;
    ConnectBT c;
    TinyDB tinyDB,isConnected;
    public static MyApplication getApplication() {
        return sInstance;
    }

    BluetoothSocket btSocket = null ;

    public void onCreate() {
        super.onCreate();
        tinyDB = new TinyDB(this);
        isConnected = new TinyDB(this);


        sInstance = this;
    }


    public void setupBluetoothConnection() {
        // Either setup your connection here, or pass it in
        c = new ConnectBT();
        myAddress = tinyDB.getString("address");
        Toast.makeText(this, myAddress, Toast.LENGTH_SHORT).show();
        if(!myAddress.isEmpty()) c.execute();
    }
    public BluetoothSocket getCurrentBluetoothConnection()
    {
        return btSocket;
    }
    public void closeBluetoothConnection()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
                TinyDB isSwapped = new TinyDB(MyApplication.this);
                isSwapped.putBoolean("isSwapped",false);
                isConnected.putBoolean("isConnected",false);
            }
            catch (IOException e)
            { Toast.makeText(MyApplication.this, "Error while closing the socket", Toast.LENGTH_SHORT).show();}
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {

            Toast.makeText(MyApplication.this, "Connecting, please wait", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if ((btSocket == null) && (!isBtConnected) && !myAddress.isEmpty()) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dev = bluetoothAdapter.getRemoteDevice(myAddress);//connects to the device's address and checks if it's available
                    btSocket = dev.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    //bluetoothAdapter.cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                Toast.makeText(MyApplication.this, "Connection Failed. Is it a SPP Bluetooth? Try again.", Toast.LENGTH_SHORT).show();
                //isConnected.putBoolean("isConnected",true); while testing
            }
            else
            {
                Toast.makeText(MyApplication.this, "Connected", Toast.LENGTH_SHORT).show();
                isConnected.putBoolean("isConnected",true); //sending information that the device is connected;
                try
                {
                    btSocket.getOutputStream().write("S".toString().getBytes());
                }
                catch (IOException e)
                {
                    Toast.makeText(MyApplication.this, "Error with sending S", Toast.LENGTH_SHORT).show();

                }
                isBtConnected = true;
            }
        }
    }

}