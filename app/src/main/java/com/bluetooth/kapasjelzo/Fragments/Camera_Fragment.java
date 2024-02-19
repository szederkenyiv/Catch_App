package com.bluetooth.kapasjelzo.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

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
import androidx.camera.core.internal.utils.SizeUtil;
import androidx.camera.core.resolutionselector.AspectRatioStrategy;
import androidx.camera.core.resolutionselector.ResolutionSelector;
import androidx.camera.core.resolutionselector.ResolutionStrategy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.Quality;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.bluetooth.kapasjelzo.Activitys.ControlActivity;
import com.bluetooth.kapasjelzo.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Camera_Fragment extends AppCompatDialogFragment {
    private AppCompatImageView back;
    private AppCompatImageView capture;
    private PreviewView camera2;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ProcessCameraProvider cameraProvider;
    private ControlActivity controlActivity;
    private CatchesFragment catchesFragment;
    private String newPicture="";

    private int REQUEST_CODE_PERMISSIONS = 10; //arbitrary number, can be changed accordingly
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE"};
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controlActivity= (ControlActivity) getActivity();
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.fragment_camera_,null);
        capture=view.findViewById(R.id.capturebutton);
        camera2=view.findViewById(R.id.camera2);
        back=view.findViewById(R.id.back_button);
        if(allPermissionsGranted()){
            startCamera(); //start camera if permission has been granted by user
        } else{
            controlActivity.requestPermissions( new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
        }


    }


    private boolean allPermissionsGranted(){
        //check if req permissions have been granted
        for(String permission : REQUIRED_PERMISSIONS){

            if(ContextCompat.checkSelfPermission(controlActivity, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }





    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(controlActivity);
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.fragment_camera_,null);
        cameraProviderFuture=ProcessCameraProvider.getInstance(getActivity().getApplicationContext());
        builder.setView(view);
        capture=view.findViewById(R.id.capturebutton);
        camera2=view.findViewById(R.id.camera2);
        back=view.findViewById(R.id.back_button);
        catchesFragment= (CatchesFragment) getActivity().getSupportFragmentManager().getFragments().get(1);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
                if(!newPicture.equals("")){
                    if(catchesFragment.getDialogFragment()!=null){
                        catchesFragment.getDialogFragment().setImageToStore(newPicture);
                        catchesFragment.getDialogFragment().getImageView().setImageURI(Uri.parse(newPicture));
                        catchesFragment.getDialogFragment().getDialog().show();
                    }
                    if(catchesFragment.getEditDialog()!=null){
                        catchesFragment.getEditDialog().setImageToStore(newPicture);
                        catchesFragment.getEditDialog().getImageView().setImageURI(Uri.parse(newPicture));
                        catchesFragment.getEditDialog().getDialog().show();
                    }
                }



            }
        });
        return builder.create();
    }

    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)

                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

         @SuppressLint("RestrictedApi") ResolutionSelector DEFAULT_RESOLUTION_SELECTOR =
                new ResolutionSelector.Builder().setAspectRatioStrategy(
                                AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY).setResolutionStrategy(
                                new ResolutionStrategy(SizeUtil.RESOLUTION_1440P,
                                        ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER))
                        .build();

        final ImageCapture imageCapture = builder
                .setTargetRotation(getActivity().getWindowManager().getDefaultDisplay().getRotation())
                .setResolutionSelector(DEFAULT_RESOLUTION_SELECTOR)
                .build();


        preview.setSurfaceProvider(camera2.getSurfaceProvider());
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis, imageCapture);


        capture.setOnClickListener(v -> {
            animationCapture();
            SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.UK);
            //File file = new File(getBatchDirectoryName(), mDateFormat.format(new Date())+ ".jpg");
            File dir=new File( Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM), "Camera");
            if(!dir.exists()||!dir.isDirectory()){
                dir.mkdirs();
            }
            ContentValues contentValues=new ContentValues();
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/Fish");
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,mDateFormat.format(new Date())+".jpeg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg");
            imageCapture.takePicture(
                    new ImageCapture.OutputFileOptions.Builder(
                            controlActivity.getContentResolver(),
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValues
                    ).build(),
                    controlActivity.getMainExecutor(),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            Toast.makeText(getActivity(),"Saving...",Toast.LENGTH_SHORT).show();
                            newPicture=outputFileResults.getSavedUri().toString();
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            Toast.makeText(getActivity(),"Error: "+exception.getMessage(),Toast.LENGTH_SHORT).show();


                        }
                    });
           /* File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM+"/Fish"),"IMG_"+mDateFormat.format(new Date())+ ".jpg");
            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
            imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback () {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            scanFile(file.getAbsolutePath());
                            newPicture=Uri.fromFile(file).toString();
                            Log.d("myfucking",Uri.fromFile(file).toString());

                            Toast.makeText(getActivity(), "Image Saved successfully", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                @Override
                public void onError(@NonNull ImageCaptureException error) {
                    Log.d("myfucking","nem sikerült");
                    error.printStackTrace();

                }
            });*/
        });




    }
    public void animationCapture(){
        ScaleAnimation scaleAnimation=new ScaleAnimation(
                1f,1.2f,1f,1.2f
                ,Animation.RELATIVE_TO_SELF,0.5f
                ,Animation.RELATIVE_TO_SELF,0.5f
        );
        scaleAnimation.setDuration(100);

        capture.startAnimation(scaleAnimation);
    }
    private void scanFile(String path) {

        MediaScannerConnection.scanFile(controlActivity,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }
    public String getBatchDirectoryName() {

        String app_folder_path = "";
        app_folder_path = Environment.getExternalStorageDirectory().toString() + "/images";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {

        }

        return app_folder_path;
    }
}