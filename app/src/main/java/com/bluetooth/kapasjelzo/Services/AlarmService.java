package com.bluetooth.kapasjelzo.Services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.bluetooth.kapasjelzo.R;

public class AlarmService extends Service {
    private static final String TAG = "BackgroundSound";
    MediaPlayer player;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        player = MediaPlayer.create(this, R.raw.alarm1);
        player.setVolume(100, 100);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");

        Log.i(TAG, "onStartCommand() Start!...");
        player.start();

        return Service.START_STICKY;
    }
}
