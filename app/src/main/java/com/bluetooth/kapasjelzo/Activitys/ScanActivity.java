package com.bluetooth.kapasjelzo.Activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bluetooth.kapasjelzo.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScanActivity extends AppCompatActivity{
    private Handler bleHandler;
    private BluetoothAdapter bleAdapter;
    private LeDeviceListAdapter bleDeviceListAdapter;
    private boolean bleScan;
    private static final long scanPeriod = 10000;
    private static final int enableBl = 1;
    ListView blelist;




    private boolean isLocationEnabled(LocationManager locationManager){
       return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }
    private void alertDialog(){
        new AlertDialog.Builder(this).setTitle("Hely adatok bekapcsolása")
                .setMessage("Az eszköz megtalálásához kérem kapcsolja be a helyadatokat.")
                .setPositiveButton(R.string.allow, (dialog, which) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(R.string.not_allow, (dialog, which) -> finish()).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listitem_device);
        blelist=findViewById(R.id.list);
        bleDeviceListAdapter = new LeDeviceListAdapter();
        blelist.setAdapter(bleDeviceListAdapter);
      Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_devices);
      getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1fb4d9")));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.bar));

        if(!(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED))
        {
            requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        bleHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (bleAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();

        }
        blelist.setOnItemClickListener((parent, view, position, id) -> {
            final BluetoothLeScanner bluetoothLeScanner = bleAdapter.getBluetoothLeScanner();
            final BluetoothDevice device = bleDeviceListAdapter.getDevice(position);
            if (device == null) return;
            final Intent intent = new Intent(this, ControlActivity.class);

            intent.putExtra(ControlActivity.EXTRAS_DEVICE_NAME, device.getName());
            intent.putExtra(ControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
            if (bleScan) {
                bluetoothLeScanner.startScan(bleScanCallback);
                bleScan = false;
            }
            startActivity(intent);
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!bleScan) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.menu_scan){
            bleDeviceListAdapter.clear();
            scanLeDevice(true);

        }
        if(item.getItemId()==R.id.menu_stop){
            scanLeDevice(false);
        }
        return true;
    }


    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onResume() {
        super.onResume();
        LocationManager locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!bleAdapter.isEnabled()) {

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            //startActivityForResult(enableBtIntent, enableBl);
            someActivityResultLauncher.launch(enableBtIntent);
        }
        if(!isLocationEnabled(locationManager)){
            alertDialog();
        }

        // Initializes list view adapter.
        bleDeviceListAdapter = new LeDeviceListAdapter();
        blelist.setAdapter(bleDeviceListAdapter);
        scanLeDevice(true);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode()==Activity.RESULT_CANCELED){
                    finish();
                }
                ScanActivity.super.onActivityResult(enableBl,result.getResultCode(),result.getData());
            }
    );

    @Override
    public void onPause() {
        super.onPause();
        scanLeDevice(false);
        bleDeviceListAdapter.clear();
    }




    @SuppressLint("MissingPermission")
    private void scanLeDevice(final boolean enable) {
        final BluetoothLeScanner bleLeScanner = bleAdapter.getBluetoothLeScanner();
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            bleHandler.postDelayed(() -> {
                bleScan = false;
                bleLeScanner.stopScan(bleScanCallback);
                invalidateOptionsMenu();
            }, scanPeriod);

            bleScan = true;
        //    bleLeScanner.startScan(bleScanCallback);
            List<ScanFilter> filters;
            filters= new ArrayList<>();
            ScanFilter filter=new ScanFilter.Builder().setDeviceName("Kapasjelzo").build();
            ScanSettings settings= new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();
            filters.add(filter);
            bleLeScanner.startScan(filters,settings,bleScanCallback);
        } else {
            bleScan = false;
            bleLeScanner.stopScan(bleScanCallback);
        }
       invalidateOptionsMenu();
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private final ArrayList<BluetoothDevice> bleDevices;

        public LeDeviceListAdapter() {
            super();
            bleDevices = new ArrayList<>();
        }

        public void addDevice(BluetoothDevice device) {
            if (!bleDevices.contains(device)) {
                bleDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return bleDevices.get(position);
        }

        public void clear() {
            bleDevices.clear();
        }

        @Override
        public int getCount() {
            return bleDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return bleDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = View.inflate(ScanActivity.this,R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = view.findViewById(R.id.device_address);
                viewHolder.deviceName = view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = bleDevices.get(i);

            @SuppressLint("MissingPermission") final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }
    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
    // Device scan callback.
    private final ScanCallback bleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            bleDeviceListAdapter.addDevice(result.getDevice());
            bleDeviceListAdapter.notifyDataSetChanged();
            super.onScanResult(callbackType, result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };
    }


