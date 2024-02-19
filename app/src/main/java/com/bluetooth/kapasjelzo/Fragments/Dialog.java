package com.bluetooth.kapasjelzo.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bluetooth.kapasjelzo.Activitys.ControlActivity;
import com.bluetooth.kapasjelzo.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import androidx.viewpager2.widget.ViewPager2;

public class Dialog extends AppCompatDialogFragment {
private EditText editTextKilogramm;



    private AppCompatImageView imageView;
    private AppCompatImageView cameraView;

private CatchDialogListener listener;
protected Uri imagePath;



    private String imageToStore;
private ControlActivity controlActivity;

private boolean ShowTime=false;

    public void setImageToStore(String imageToStore) {
        this.imageToStore = imageToStore;
    }
    public AppCompatImageView getImageView() {
        return imageView;
    }
    protected void setShowTime(boolean showTime) {
        ShowTime = showTime;
    }

    protected void setImagePath(Uri imagePath) {
        this.imagePath = imagePath;
    }

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ProcessCameraProvider cameraProvider;

private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.fragment_dialog, null);
        cameraView=view.findViewById(R.id.camera);

        controlActivity= (ControlActivity) getActivity();

    }





    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.fragment_dialog, null);

        builder.setView(view).setTitle("Új kapás feljegyzése")
                .setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        }).setPositiveButton("Hozzáadás", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String kilogramm=editTextKilogramm.getText().toString()+" kg";

                listener.setCamera(imagePath,cameraView);
                listener.applyText(kilogramm,imageToStore);
            }
        });
        imageView=view.findViewById(R.id.picture);
        imageView.setOnClickListener(v -> chooseImage());

        cameraView=view.findViewById(R.id.camera);
        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().hide();
               Camera_Fragment camera_fragment=new Camera_Fragment();
               camera_fragment.setCancelable(false);
               camera_fragment.show(getActivity().getSupportFragmentManager(),Dialog.TAG);


            }
        });



        editTextKilogramm=view.findViewById(R.id.add_kilogramm);




        return builder.create();
    }
    public static String TAG="adding new record";

public void chooseImage(){
    try {
        Intent intent=new Intent();
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setAction(Intent.ACTION_PICK);

        activityResultLauncher.launch(intent);
    }
    catch (Exception e){
        Toast.makeText(getActivity().getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
    }


}

    ActivityResultLauncher<Intent> activityResultLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()== Activity.RESULT_OK){
                    imagePath= result.getData().getData();
                    imageView.setImageURI(imagePath);
                    imageToStore=imagePath.toString();

                }

                }
            }
    );

    public interface CatchDialogListener{
        void applyText(String kilogramm, String imageToStore);
        void setCamera(Uri imagePath,ImageView camera);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener=(CatchDialogListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString()+"must implement CatchDialogListener");
        }

    }




}