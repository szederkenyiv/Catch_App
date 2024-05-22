package com.bluetooth.kapasjelzo.CatchRoomDB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = CatchRoom.class, version = 1)
public abstract class CatchDatabase extends RoomDatabase {
    private static CatchDatabase instance;

    public abstract CatchDao catchDao();

    public static synchronized CatchDatabase getInstance(Context context){
        if(instance==null){
            instance= Room.databaseBuilder(context.getApplicationContext(),
                    CatchDatabase.class,"catch_database")
                    .fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
