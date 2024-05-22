package com.bluetooth.kapasjelzo.CatchRoomDB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CatchDao {
    @Insert
    void insert(CatchRoom catchRoom);
    @Update
    void update(CatchRoom catchRoom);
    @Delete
    void delete(CatchRoom catchRoom);

    @Query("SELECT * FROM catches ORDER BY id DESC")
    LiveData<List<CatchRoom>> getAllCatches();
}
