package com.example.detail;

public class BlueItem {
    public  String Devicename;
    public  String RSSI;
    public  String content;

    public BlueItem(){};
    public BlueItem(String devicename, String RSSI, String content) {
        Devicename = devicename;
        this.RSSI = RSSI;
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
