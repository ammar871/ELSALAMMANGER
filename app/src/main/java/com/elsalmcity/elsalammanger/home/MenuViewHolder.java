package com.elsalmcity.elsalammanger.home;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elsalmcity.elsalammanger.R;

public class MenuViewHolder extends RecyclerView.ViewHolder  {

    TextView textView;
    ImageView imageView;
    ImageView delete,update;

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.tv_item_title);
        imageView = itemView.findViewById(R.id.imag_item);
        delete = itemView.findViewById(R.id.btn_delete);
        update = itemView.findViewById(R.id.btn_update);
    }}