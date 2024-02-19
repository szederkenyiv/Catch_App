package com.bluetooth.kapasjelzo.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluetooth.kapasjelzo.CatchSQLite.CatchAdapter;
import com.bluetooth.kapasjelzo.CatchSQLite.CatchRoom;
import com.bluetooth.kapasjelzo.CatchSQLite.CatchViewModel;
import com.bluetooth.kapasjelzo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;


public class CatchesFragment extends Fragment {
    private RecyclerView recyclerView;
    private CatchViewModel catchViewModel;
    private FloatingActionButton button;
    protected Dialog dialogFragment;
    protected EditDialog editDialog;


    protected Dialog getDialogFragment() {
        return dialogFragment;
    }
    protected EditDialog getEditDialog(){return editDialog;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED))
        {
            requestPermissions( new String[]{Manifest.permission.CAMERA}, 1);;
        }
        final CatchAdapter adapter=new CatchAdapter(getActivity().getApplicationContext());

        View view= inflater.inflate(R.layout.recyclerview, container, false);
        button=view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment= new Dialog();
                dialogFragment.setCancelable(false);
                dialogFragment.show(getActivity().getSupportFragmentManager(),Dialog.TAG);

            }
        });
        recyclerView=view.findViewById(R.id.recyleview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
        catchViewModel=new ViewModelProvider(requireActivity()).get(CatchViewModel.class);
        catchViewModel.getAllCatches().observe(requireActivity(), new Observer<List<CatchRoom>>() {
            @Override
            public void onChanged(List<CatchRoom> catchRooms) {
                adapter.setCatchRoomList(catchRooms);

            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                catchViewModel.delete(adapter.getCatchAt(viewHolder.getAdapterPosition()));
            }
        }).attachToRecyclerView(recyclerView);
        adapter.setOnItemHoldListener(new CatchAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(CatchRoom catchRoom) {
                editDialog= new EditDialog(catchRoom);
                editDialog.show(getActivity().getSupportFragmentManager(),Dialog.TAG);

            }
        });

        return view;
    }
    public CatchViewModel getCatchViewModel(){
        return catchViewModel;
    }
}