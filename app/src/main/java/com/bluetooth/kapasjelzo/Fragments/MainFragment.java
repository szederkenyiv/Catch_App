package com.bluetooth.kapasjelzo.Fragments;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.bluetooth.kapasjelzo.Activitys.ControlActivity;
import com.bluetooth.kapasjelzo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainFragment extends Fragment {
    private SwitchCompat switch2;

    private TextView dataField;
    private TextView reelDataField;
    private TextView pressureDataField;
    private TextView connectionState;
    private ControlActivity controlActivity;
    private static final String setAlarmCharacteristics="c560e5c8-a1fd-4d0e-8fe4-33b39fd28e8a";
    private final String ON="1";
    private final String OFF="0";
    private FloatingActionButton setting_button;
    final Handler handler=new Handler();
    public static String TAG="changing LEDCOLOR";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        controlActivity= (ControlActivity) getActivity();
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_main,container,false);
        setting_button=view.findViewById(R.id.setting);
        setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsDialog settings_Dialog=new settingsDialog();
                settings_Dialog.show(getActivity().getSupportFragmentManager(),TAG);

            }
        });
        switch2=view.findViewById(R.id.bitech);
        switch2.setOnClickListener(clickListener);
        dataField=view.findViewById(R.id.data_value);
        reelDataField=view.findViewById(R.id.Reeldata_value);
        reelDataField.setVisibility(View.INVISIBLE);
        pressureDataField=view.findViewById(R.id.legnyomas);
        connectionState=view.findViewById(R.id.connection_state);
        return view;

    }
    private final View.OnClickListener clickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            controlActivity= (ControlActivity) getActivity();

            if(!switch2.isChecked()&&switch2.isPressed())
            {
                controlActivity.writeCharacteristic(OFF,setAlarmCharacteristics);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            controlActivity.notifyCharacteristic(false);

                        }
                    },500);





                Toast.makeText(MainFragment.this.getContext(),"Kapásjelző Kikapcsolva!",Toast.LENGTH_SHORT).show();

            }
            if (switch2.isChecked()&&switch2.isPressed())
            {
                controlActivity.writeCharacteristic(ON,setAlarmCharacteristics);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        controlActivity.notifyCharacteristic(true);

                    }
                },500);



                Toast.makeText(MainFragment.this.getContext(),"Kapásjelző Bekapcsolva!",Toast.LENGTH_SHORT).show();
            }
        }
    };

public void updateDataField(String value){
    this.dataField.setText(value);
}
public void animationReelDataField(){
    AlphaAnimation animation= new AlphaAnimation(1.0f,0.0f);
    animation.setStartOffset(600);
    animation.setDuration(400);
    animation.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            MainFragment.this.reelDataField.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            MainFragment.this.reelDataField.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    });
    MainFragment.this.reelDataField.setAnimation(animation);
    MainFragment.this.reelDataField.startAnimation(animation);
}
public void updatePressureDataField(String value){
    this.pressureDataField.setText(value);
}
public void updateConnectionState(int value){
    this.connectionState.setText(value);
}
public void clearText(){
    this.dataField.setText(R.string.temperature_nodata);
    this.pressureDataField.setText(R.string.pressure_nodata);
    this.switch2.setChecked(false);
}
public boolean switch2State(){
    return this.switch2.isChecked();
}

}