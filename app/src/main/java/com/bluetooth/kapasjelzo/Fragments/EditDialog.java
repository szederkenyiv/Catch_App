package com.bluetooth.kapasjelzo.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.fragment.app.DialogFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bluetooth.kapasjelzo.CatchSQLite.CatchRoom;
import com.bluetooth.kapasjelzo.R;
public class EditDialog extends AppCompatDialogFragment {
    private EditText editTextKilogramm;
    private CatchDialogListener listener;
    private CatchRoom catchRoom;
    private Uri imagePath;
    private String imageToStore;
    private AppCompatImageView imageView;
    private AppCompatImageView cameraView;
    public void setImageToStore(String imageToStore) {
        this.imageToStore = imageToStore;
    }
    public AppCompatImageView getImageView() {
        return imageView;
    }
    EditDialog(CatchRoom catchRoom){
        this.catchRoom=catchRoom;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.fragment_edit_dialog, null);
        cameraView=view.findViewById(R.id.editcamera);
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.fragment_edit_dialog, null);
        builder.setView(view).setTitle("Kapás módosítása")
                .setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                }).setPositiveButton("Módosítás", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String kilogramm=editTextKilogramm.getText().toString()+" kg";
                        listener.editText(kilogramm,imageToStore,catchRoom);
                    }
                });
        imageView=view.findViewById(R.id.picture);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        editTextKilogramm=view.findViewById(R.id.add_kilogramm);
        cameraView=view.findViewById(R.id.editcamera);
        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().hide();
                Camera_Fragment camera_fragment=new Camera_Fragment();
                camera_fragment.setCancelable(false);
                camera_fragment.show(getActivity().getSupportFragmentManager(),Dialog.TAG);


            }
        });
        return builder.create();
    }
    public static String TAG="update record";



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
                        imagePath = result.getData().getData();
                        imageView.setImageURI(imagePath);
                        imageToStore=imagePath.toString();
                    }
                }
            }
    );
    public interface CatchDialogListener{
        void editText(String kilogramm,String imageToStore,CatchRoom catchRoom);

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