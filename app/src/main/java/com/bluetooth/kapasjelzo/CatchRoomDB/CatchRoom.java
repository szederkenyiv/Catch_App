package com.bluetooth.kapasjelzo.CatchRoomDB;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "catches")
public class CatchRoom {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String temperature;
    private String pressure;

    private String date;

    private String kilogramm;

    private String image;




    public CatchRoom(String temperature, String pressure,String date, String kilogramm,String image) {
        this.temperature = temperature;
        this.pressure = pressure;
        this.date=date;
        this.kilogramm=kilogramm;
        this.image=image;

    }


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getPressure() {
        return pressure;
    }

    public String getDate() {
        return date;
    }

    public String getKilogramm() {
        return kilogramm;
    }

    public String getImage() {
        return image;
    }
}
