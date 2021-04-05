package com.example.terminarzg;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private ArrayList<RecycleItem> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
        void onAddClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textView1;
        public TextView textView2;
        public ImageView deleteImage;
        public ImageView addImage;


        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);
            deleteImage = itemView.findViewById(R.id.delete);
            addImage = itemView.findViewById(R.id.add);


            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });

            addImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onAddClick(position);
                        }
                    }
                }
            });


        }


    }

    public Adapter(ArrayList<RecycleItem> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item,parent,false);
        ViewHolder vh = new ViewHolder(v, listener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecycleItem cItem = list.get(position);

        holder.imageView.setImageResource(cItem.getImgRes());
        holder.textView1.setText(cItem.getText1());
        holder.textView2.setText(cItem.getText2());
        holder.deleteImage.setImageResource(cItem.getImgDelRes());
        holder.addImage.setImageResource(cItem.getImgAddRes());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
