package com.bluetooth.kapasjelzo.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatCallback;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.bluetooth.kapasjelzo.Activitys.ControlActivity;
import com.bluetooth.kapasjelzo.R;


public class settingsDialog extends AppCompatDialogFragment {
    private static final String setLEDColorCharacteristics="70ba83c6-6311-4790-bdc8-9e9245e79232";
    private static final String setSensivityCharacteristics="808cea06-6c56-4dfc-a2bf-2cf415a8cf57";
    private CheckBox redCheck,greenCheck,blueCheck,lowCheck,normalCheck,highCheck;
    private final String red="0,255,255";
    private final String green="255,255,0";
    private final String blue="255,0,255";
    private final String low="1.15";
    private final String normal="1.05";
    private final String high="1.0";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.fragment_setting_dialog,null);
        redCheck = view.findViewById(R.id.red_check);
        greenCheck = view.findViewById(R.id.green_check);
        blueCheck = view.findViewById(R.id.blue_check);
        lowCheck=view.findViewById(R.id.low_check);
        normalCheck=view.findViewById(R.id.normal_check);
        highCheck=view.findViewById(R.id.high_check);
        redCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(redCheck.isChecked()){
                    greenCheck.setChecked(false);
                    blueCheck.setChecked(false);
                }

            }
        });
        greenCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(greenCheck.isChecked()){
                    redCheck.setChecked(false);
                    blueCheck.setChecked(false);
                }

            }
        });
        blueCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(blueCheck.isChecked()){
                    redCheck.setChecked(false);
                    greenCheck.setChecked(false);
                }

            }
        });
        lowCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lowCheck.isChecked()){
                    normalCheck.setChecked(false);
                    highCheck.setChecked(false);
                }
            }
        });
        normalCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (normalCheck.isChecked()){
                    lowCheck.setChecked(false);
                    highCheck.setChecked(false);
                }
            }
        });
        highCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (highCheck.isChecked()){
                    normalCheck.setChecked(false);
                    lowCheck.setChecked(false);
                }
            }
        });

        builder.setView(view).setTitle("Kapásjelző beállításai:")
                .setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Mentés", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        redCheck = view.findViewById(R.id.red_check);
                        greenCheck = view.findViewById(R.id.green_check);
                        blueCheck = view.findViewById(R.id.blue_check);
                        ControlActivity controlActivity = (ControlActivity) getActivity();
                        if (controlActivity != null) {
                            if (redCheck.isChecked()) {
                                controlActivity.writeCharacteristic(red,setLEDColorCharacteristics);
                            }
                            if (greenCheck.isChecked()) {
                                controlActivity.writeCharacteristic(green,setLEDColorCharacteristics);
                            }
                            if (blueCheck.isChecked()){
                                controlActivity.writeCharacteristic(blue,setLEDColorCharacteristics);
                            }
                            if(lowCheck.isChecked()){
                                controlActivity.writeCharacteristic(low,setSensivityCharacteristics);
                            }
                            if(normalCheck.isChecked()){
                                controlActivity.writeCharacteristic(normal,setSensivityCharacteristics);
                            }
                            if(highCheck.isChecked()){
                                controlActivity.writeCharacteristic(high,setSensivityCharacteristics);
                            }


                        }
                    }
                });
        return builder.create();

    }


}
