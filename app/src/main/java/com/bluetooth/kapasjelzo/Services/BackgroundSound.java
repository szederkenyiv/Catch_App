package com.bluetooth.kapasjelzo.Services;

import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.IBinder;

import com.bluetooth.kapasjelzo.Services.BluetoothService;

import java.util.ArrayList;

public class BackgroundSound extends Service {
    private BluetoothService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    public IBinder onBind(Intent intent) {
        return null;
    }

}
