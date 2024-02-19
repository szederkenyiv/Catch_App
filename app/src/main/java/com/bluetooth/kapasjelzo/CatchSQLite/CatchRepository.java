package com.bluetooth.kapasjelzo.CatchSQLite;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CatchRepository {
    private CatchDao catchDao;
    private LiveData<List<CatchRoom>> allCatches;

    public CatchRepository(Application application){
        CatchDatabase database=CatchDatabase.getInstance(application);
        catchDao= database.catchDao();
        allCatches=catchDao.getAllCatches();
    }
    public void insert(CatchRoom catchRoom){
        ExecutorService executorService=Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                catchDao.insert(catchRoom);
            }
        });
    }
    public void update(CatchRoom catchRoom){
        ExecutorService executorService=Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                catchDao.update(catchRoom);
            }
        });
    }
    public void delete(CatchRoom catchRoom){
        ExecutorService executorService=Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                catchDao.delete(catchRoom);
            }
        });
    }
    public LiveData<List<CatchRoom>> getAllCatches(){
        return allCatches;
    }


}
