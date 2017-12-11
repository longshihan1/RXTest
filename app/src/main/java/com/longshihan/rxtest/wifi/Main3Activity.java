package com.longshihan.rxtest.wifi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.longshihan.rxtest.LogUtils;
import com.longshihan.rxtest.R;

public class Main3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        WifiAdmin wifiAdmin = new WifiAdmin(this);
        wifiAdmin.openWifi();
        wifiAdmin.startScan();
        wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo("XXX", "XXX", 3));
    }
}
