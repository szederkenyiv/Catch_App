/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bluetooth.kapasjelzo.Activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.bluetooth.kapasjelzo.CatchRoomDB.CatchRoom;
import com.bluetooth.kapasjelzo.CatchRoomDB.CatchViewModel;
import com.bluetooth.kapasjelzo.Fragments.CatchesFragment;
import com.bluetooth.kapasjelzo.Fragments.Dialog;
import com.bluetooth.kapasjelzo.Fragments.EditDialog;
import com.bluetooth.kapasjelzo.Fragments.FragmentAdapter;
import com.bluetooth.kapasjelzo.Fragments.MainFragment;
import com.bluetooth.kapasjelzo.GattAttributes;
import com.bluetooth.kapasjelzo.R;
import com.bluetooth.kapasjelzo.Services.AlarmService;
import com.bluetooth.kapasjelzo.Services.BluetoothService;
import com.google.android.material.tabs.TabLayout;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ControlActivity extends AppCompatActivity implements Dialog.CatchDialogListener, EditDialog.CatchDialogListener {
    public static final String EXTRAS_DEVICE_NAME = "Eszköz név";
    public static final String EXTRAS_DEVICE_ADDRESS = "Eszköz cím";
    private final static String TAG = "myTags";
    public String getWeatherCharacteristics() {
        return weatherCharacteristics;
    }
    private  String weatherCharacteristics="c6daa4c4-e942-450d-b2df-872d918278cc";
    private static final String alarmCharacteristics = "ca73b3ba-39f6-4ab3-91ae-186dc9577d99";
    private Executor executor = Executors.newSingleThreadExecutor();
    private String deviceAddress;
    private SoundPool soundPool;
    private String celsius;
    private  String strPressure;
    private int sound;
    private static final DecimalFormat df= new DecimalFormat("0.00");
    private BluetoothService bleService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> gattCharacteristics = new ArrayList<>();
    private boolean isConnected = false;
    private BluetoothGattCharacteristic notifyCharacteristic;
    private MainFragment mainFragment;
    private ViewPager2 viewPager2;
    private boolean startup=true;
    private final Handler handler=new Handler();

    private boolean end_of_write=false;

    public boolean isEnd_of_write() {
        return end_of_write;
    }

    // Code to manage Service lifecycle.
    /**
     *
     */
    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bleService = ((BluetoothService.LocalBinder) service).getService();
            if (!bleService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            bleService.connect(deviceAddress);
        }


        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bleService = null;
        }
    };
    /**
     *
     */
    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            mainFragment= (MainFragment) getSupportFragmentManager().getFragments().get(0);
            final String action = intent.getAction();
            if (BluetoothService.gattConnected.equals(action)) {
                isConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothService.gattDisconnected.equals(action)) {
                isConnected = false;
               updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothService.servicesDiscovered.equals(action)) {
                if(bleService==null){
                    Log.d(TAG,"bleService is null");
                }
                else {
                    getGattServices(bleService.getSupportedGattServices());
                    Log.d(TAG,"bleService is not null");
                    if(startup){
                        readCharacteristic(weatherCharacteristics);
                        handler.post(runnable);
                        startup=false;
                    }

                }

            } else if (BluetoothService.dataAviable.equals(action)) {
               // data=intent.getStringExtra(BluetoothService.extraData);
                displayData(intent.getStringExtra(BluetoothService.extraData));
            }
        }
    };

    /**
     *
     * @param check
     */

    public void notifyCharacteristic(boolean check) {
        if (gattCharacteristics != null) {
            for (int i = 0; i < gattCharacteristics.size(); i++) {
                for (int j = 0; j < gattCharacteristics.get(i).size(); j++) {
                    if (gattCharacteristics.get(i).get(j).getUuid().toString().equals(alarmCharacteristics)) {
                        final BluetoothGattCharacteristic characteristic = gattCharacteristics.get(i).get(j);
                        if (check) {

                            if (notifyCharacteristic != null) {
                                bleService.setCharacteristicNotification(notifyCharacteristic, false);
                                notifyCharacteristic = null;
                            }
                            if ((BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                                notifyCharacteristic = characteristic;
                                Log.d("myTag",characteristic.getUuid().toString());
                                bleService.setCharacteristicNotification(characteristic, true);
                            }
                        }
                        else {
                                if (notifyCharacteristic != null) {
                                    bleService.setCharacteristicNotification(
                                            notifyCharacteristic, false);
                                    notifyCharacteristic = null;
                                }
                        }

                    }
                }

            }
        }
    }

    private final Runnable runnable=new Runnable() {
        @Override
        public void run() {
            readCharacteristic(getWeatherCharacteristics());
            handler.postDelayed(runnable,300000);
        }
    };
    public void readCharacteristic(String current_UUID){

        if (gattCharacteristics != null ) {

            for (int i = 0; i < gattCharacteristics.size(); i++) {
                for (int j = 0; j < gattCharacteristics.get(i).size(); j++) {
                    if (gattCharacteristics.get(i).get(j).getUuid().toString().equals(current_UUID)) {
                        final BluetoothGattCharacteristic characteristic =
                                gattCharacteristics.get(i).get(j);
                         bleService.readCharacteristic(characteristic);
                    }
                }
            }
        }
    }
    public void writeCharacteristic(String value,String charachteristic){
        if (gattCharacteristics != null ) {

            for (int i = 0; i < gattCharacteristics.size(); i++) {
                for (int j = 0; j < gattCharacteristics.get(i).size(); j++) {
                    if (gattCharacteristics.get(i).get(j).getUuid().toString().equals(charachteristic)) {
                        final BluetoothGattCharacteristic characteristic =
                                gattCharacteristics.get(i).get(j);
                        characteristic.setValue(value);
                        bleService.writeCharacteristic(characteristic);

                    }
                }
            }
        }

    }

    @Override
    public void editText(String kilogramm,String imageToStore,CatchRoom catchRoom) {
        Log.d(TAG,kilogramm);
        if(catchRoom.getId()==-1){
            Toast.makeText(this,"Nem lehet frissíteni!",Toast.LENGTH_SHORT).show();
        }
        else {

            CatchesFragment catchesFragment1 = (CatchesFragment) getSupportFragmentManager().getFragments().get(1);
            CatchViewModel catchViewModel = catchesFragment1.getCatchViewModel();
            if ((imageToStore == null || imageToStore.equals(catchRoom.getImage())) && (kilogramm.equals(catchRoom.getKilogramm()) || kilogramm.equals(" kg"))) {
                Toast.makeText(this, "Nincs új adat!", Toast.LENGTH_SHORT).show();
            } else if (imageToStore!=null&&kilogramm.equals(catchRoom.getKilogramm())) {
                CatchRoom room = new CatchRoom(catchRoom.getTemperature(), catchRoom.getPressure(), catchRoom.getDate(), catchRoom.getKilogramm(),imageToStore);
                room.setId(catchRoom.getId());
                catchViewModel.update(room);
                Toast.makeText(this, "Kép frissítve!", Toast.LENGTH_SHORT).show();
            } else if (imageToStore == null||imageToStore.equals(catchRoom.getImage())) {
                CatchRoom room = new CatchRoom(catchRoom.getTemperature(), catchRoom.getPressure(), catchRoom.getDate(), kilogramm, catchRoom.getImage());
                room.setId(catchRoom.getId());
                catchViewModel.update(room);
                Toast.makeText(this, "Kilogramm frissítve!", Toast.LENGTH_SHORT).show();
            }
            else {
                CatchRoom room = new CatchRoom(catchRoom.getTemperature(), catchRoom.getPressure(), catchRoom.getDate(), kilogramm, imageToStore);
                room.setId(catchRoom.getId());
                catchViewModel.update(room);
                Toast.makeText(this, "Kapás frissítve!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void applyText(String kilogramm, String imageToStore) {
            String temperature = getCelsius();
            String pressure = getPressure();
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
            String roomDate = dateFormat.format(date);
            CatchesFragment catchesFragment1 = (CatchesFragment) getSupportFragmentManager().getFragments().get(1);
            CatchRoom catchRoom = new CatchRoom(temperature, pressure, roomDate, kilogramm, imageToStore);
            CatchViewModel catchViewModel = catchesFragment1.getCatchViewModel();
            catchViewModel.insert(catchRoom);

    }

  @Override
    public void setCamera(Uri imagePath,ImageView camera) {



    }



    private void clearUI() {

        mainFragment.clearText();
        startup=true;

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                ||(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED))
        {
            requestPermissions( new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
        setContentView(R.layout.control_actvity_layout);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        viewPager2=findViewById(R.id.pager);
        viewPager2.setAdapter(new FragmentAdapter(this));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
           @Override
           public void onTabSelected(TabLayout.Tab tab) {
               viewPager2.setCurrentItem(tab.getPosition());
           }
           @Override
           public void onTabUnselected(TabLayout.Tab tab) {

           }
           @Override
           public void onTabReselected(TabLayout.Tab tab) {
           }
       });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
        final Intent intent = getIntent();
        String deviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        deviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        Intent gattServiceIntent = new Intent(this, BluetoothService.class);
        bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);
        getSupportActionBar().setTitle(deviceName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1fb4d9")));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.bar));
        AudioAttributes audioAttributes=new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_ALARM).build();
        soundPool=new SoundPool.Builder().setMaxStreams(2).setAudioAttributes(audioAttributes).build();
        sound=soundPool.load(this,R.raw.alarm1,0);
        Toast.makeText(this,"A hőfok és légnyomás 5 percenként automatikusan frissül!",Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
        if (bleService != null) {
            final boolean result = bleService.connect(deviceAddress);
            Log.d("Mytag", "Connect request result=" + result);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        bleService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.services, menu);
        if (isConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_connect){
            bleService.connect(deviceAddress);
            return true;
        }
        if(item.getItemId()==R.id.menu_disconnect){
            bleService.disconnect();
            return true;
        }
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(() -> mainFragment.updateConnectionState(resourceId));
    }

    public String getCelsius(){
        return celsius;
    }
    public String getPressure(){
        return strPressure;
    }

    private void displayData(String data) {
        if(data.contains("&")) {
            String[] part = data.split("&");
            celsius = part[0] + " \u2103";
            double pessure = Double.parseDouble(part[1]) * 10;
            strPressure = df.format(pessure);
            strPressure = strPressure + " hPa";
            mainFragment.updateDataField(celsius);
            mainFragment.updatePressureDataField(strPressure);
        }
        else {
            mainFragment.animationReelDataField();
            if (data.equals("true") && mainFragment.switch2State()) {
                Intent myService2 = new Intent(ControlActivity.this, AlarmService.class);
                startService(myService2);
                soundPool.stop(sound);
            }
        }
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void getGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null){
            Log.d(TAG,"gattServicies is null");
            return;
        }
        String uuid;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<>();
        gattCharacteristics = new ArrayList<>();
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<>();
            uuid = gattService.getUuid().toString();
            if (uuid.equals(GattAttributes.Bite_Alarm_Service)) {
                String LIST_NAME = "NAME";
                currentServiceData.put(
                        LIST_NAME, GattAttributes.lookup(uuid, unknownServiceString));
                String LIST_UUID = "UUID";
                currentServiceData.put(LIST_UUID, uuid);
                gattServiceData.add(currentServiceData);
                ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                        new ArrayList<>();
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();
                ArrayList<BluetoothGattCharacteristic> charas =
                        new ArrayList<>();
                // Loops through available Characteristics.
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    charas.add(gattCharacteristic);
                    HashMap<String, String> currentCharaData = new HashMap<>();
                    uuid = gattCharacteristic.getUuid().toString();
                    currentCharaData.put(
                            LIST_NAME, GattAttributes.lookup(uuid, unknownCharaString));
                    currentCharaData.put(LIST_UUID, uuid);
                    gattCharacteristicGroupData.add(currentCharaData);
                }
                this.gattCharacteristics.add(charas);
                gattCharacteristicData.add(gattCharacteristicGroupData);
            }
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothService.gattConnected);
        intentFilter.addAction(BluetoothService.gattDisconnected);
        intentFilter.addAction(BluetoothService.servicesDiscovered);
        intentFilter.addAction(BluetoothService.dataAviable);
        return intentFilter;
    }
}
