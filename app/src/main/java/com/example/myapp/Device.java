package com.example.myapp;

public class Device {
    public String deviceName;
    public int isConnected;

    /*public Device(String deviceName) {
        this.deviceName = deviceName;
    }
    */

    public Device(String deviceName,int isConnected) {
        this.deviceName = deviceName;
        this.isConnected = isConnected;
    }
}
