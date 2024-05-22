package com.bluetooth.kapasjelzo.CatchRoomDB;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class CatchViewModel extends AndroidViewModel {
    public CatchRepository repository;
    public LiveData<List<CatchRoom>> allCatches;

    public CatchViewModel(Application application) {
        super(application);
        repository=new CatchRepository(application);
        allCatches=repository.getAllCatches();
    }
    public void insert(CatchRoom catchRoom){
        repository.insert(catchRoom);
    }
    public void update(CatchRoom catchRoom){
        repository.update(catchRoom);
    }
    public void delete(CatchRoom catchRoom){
        repository.delete(catchRoom);
    }
    public LiveData<List<CatchRoom>> getAllCatches() {
        return allCatches;
    }
}
