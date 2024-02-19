package com.bluetooth.kapasjelzo.Services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.bluetooth.kapasjelzo.GattAttributes;

import java.util.List;
import java.util.UUID;

public class BluetoothService extends Service {
    private final static String TAG = BluetoothService.class.getSimpleName();
    public final static String gattConnected = "Csatlakozva";
    public final static String gattDisconnected = "Lecsatlakozva";
    public final static String servicesDiscovered = "Szolgáltatás megtalálva";
    public final static String dataAviable = "Elérhetőadat";
    public final static String extraData = "Kiegészítőinformáció";
    private static final int stateDisconnected = 0;
    private static final int stateConnecting = 1;
    private static final int stateConnected = 2;
    private int connectionState = stateDisconnected;
    private BluetoothGatt bleGatt;
    private BluetoothManager bleManager;
    private BluetoothAdapter bleAdapeter;
    private String bleAddress;
    public final static UUID UUID_Bite_Alarm = UUID.fromString(GattAttributes.Bite_Alarm_CHARACTERISTIC);

    private final BluetoothGattCallback bleCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String connectionChange;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connectionChange = gattConnected;
                connectionState = stateConnected;
                broadcast(connectionChange);
                Log.i(TAG, "Csatlakozva a GATT szerverhez.");
                Log.i(TAG, "Szolgáltatás keresése:" + bleGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectionChange = gattDisconnected;
                connectionState = stateDisconnected;
                Log.i(TAG, "Lecsatalkozva a GATT szerverről.");
                broadcast(connectionChange);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcast(servicesDiscovered);
            } else {
                Log.w(TAG, "Hiba:" + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcast(dataAviable, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            broadcast(dataAviable, characteristic);
        }

    };

    private void broadcast(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcast(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        byte[] data = characteristic.getValue();

        if (data != null && data.length > 0) {
            intent.putExtra(extraData, new String(data));
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public boolean initialize() {

        if (bleManager == null) {
            bleManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (bleManager == null) {
                Log.e(TAG, "Nem elérhető a BluetoothManager.");
                return false;
            }
        }

        bleAdapeter = bleManager.getAdapter();
        if (bleAdapeter == null) {
            Log.e(TAG, "Nem elérhető a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    @SuppressLint("MissingPermission")
    public boolean connect(final String address) {
        if (bleAdapeter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter nem elérhető vagy rossz cím.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (bleAddress != null && address.equals(bleAddress)
                && bleGatt != null) {
            Log.d(TAG, "Kísérlet az eszköz újrcsatlakozásához.");
            if (bleGatt.connect()) {
                connectionState = stateConnecting;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = bleAdapeter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Az eszköz nem található. Nem lehet csatlakozni.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        bleGatt = device.connectGatt(this, true, bleCallback);
        Log.d(TAG, "Új csatlakozás megprobálása.");
        bleAddress = address;
        connectionState = stateConnecting;
        return true;
    }

    @SuppressLint("MissingPermission")
    public void disconnect() {
        if (bleAdapeter == null || bleGatt == null) {

            Log.w(TAG, "BluetoothAdapter nem elérhető");
            return;
        }

        bleGatt.disconnect();
    }

    @SuppressLint("MissingPermission")
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (bleAdapeter == null || bleGatt == null) {
            Log.w(TAG, "BluetoothAdapter nem elérhető");
            return;
        }
        bleGatt.readCharacteristic(characteristic);
    }

    @SuppressLint("MissingPermission")
    public boolean writeCharacteristic (BluetoothGattCharacteristic characteristic){
        if (bleAdapeter == null || bleGatt == null) {
            Log.d(TAG, "BluetoothAdapter nem elérhető");
            return false;
        }
        bleGatt.writeCharacteristic(characteristic);

        return true;
    }

    @SuppressLint("MissingPermission")
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (bleAdapeter == null || bleGatt == null) {
            Log.d(TAG, "BluetoothAdapter nem elérhető");
            return;
        }

        bleGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
       if (UUID_Bite_Alarm.equals(characteristic.getUuid())&&enabled) {
           Log.d("myTag","itt vagyok");
           BluetoothGattDescriptor  descriptor = characteristic.getDescriptor(
                    UUID.fromString(GattAttributes.Bite_Alarm_Descriptor_UUID));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bleGatt.writeDescriptor(descriptor);
        }

    }
    public List<BluetoothGattService> getSupportedGattServices() {
        if (bleGatt == null){
          Log.e("Myerror","blegatt is null");
          return null;
        }

        return bleGatt.getServices();
    }
    
    @SuppressLint("MissingPermission")
    public void close() {
        if (bleGatt == null) {
            return;
        }
        bleGatt.close();
        bleGatt = null;
    }
}