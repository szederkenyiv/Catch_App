package com.bluetooth.kapasjelzo.CatchRoomDB;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluetooth.kapasjelzo.R;

import java.util.ArrayList;
import java.util.List;

public class CatchAdapter extends RecyclerView.Adapter<CatchAdapter.CatchHolder> {
    private List<CatchRoom> catchRoomList = new ArrayList<>();
    private OnItemLongClickListener listener;

    private Context context;
  public CatchAdapter(Context context){
      this.context=context;
  }

    @NonNull
    @Override
    public CatchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_catches, parent, false);
        return new CatchHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CatchHolder holder, int position) {
        CatchRoom catchRoom = catchRoomList.get(position);
        holder.textViewTemp.setText(catchRoom.getTemperature());
        holder.textViewPressure.setText(catchRoom.getPressure());
        holder.textViewDate.setText(catchRoom.getDate());
        holder.textViewKilogramm.setText(catchRoom.getKilogramm());
        if (catchRoom.getImage()!=null){
            holder.imageView.setImageURI(Uri.parse(catchRoom.getImage()));
        }
        else {
            holder.imageView.setImageResource(R.drawable.no_image);
        }





    }

    @Override
    public int getItemCount() {
        return catchRoomList.size();
    }

    public void setCatchRoomList(List<CatchRoom> catchRooms) {
        this.catchRoomList = catchRooms;
        notifyDataSetChanged();
    }

    public CatchRoom getCatchAt(int position) {
        return catchRoomList.get(position);
    }

    class CatchHolder extends RecyclerView.ViewHolder {
        private TextView textViewTemp;
        private TextView textViewPressure;

        private TextView textViewDate;

        private TextView textViewKilogramm;
        private ImageView imageView;

        public CatchHolder(@NonNull View itemView) {
            super(itemView);
            textViewTemp = itemView.findViewById(R.id.text_view_temp);
            textViewPressure = itemView.findViewById(R.id.text_view_pressure);
            textViewDate = itemView.findViewById(R.id.Date);
            textViewKilogramm = itemView.findViewById(R.id.Kilogramm);
            imageView=itemView.findViewById(R.id.catch_image);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemLongClick(catchRoomList.get(position));
                    }
                    return true;
                }
            });

        }


    }

    public interface OnItemLongClickListener {
        void onItemLongClick(CatchRoom catchRoom);
    }

    public void setOnItemHoldListener(OnItemLongClickListener listener) {
        this.listener = listener;

    }
}
