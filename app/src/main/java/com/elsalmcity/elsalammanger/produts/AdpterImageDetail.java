package com.elsalmcity.elsalammanger.produts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elsalmcity.elsalammanger.R;

import java.util.ArrayList;

public class AdpterImageDetail  extends RecyclerView.Adapter<AdpterImageDetail.ViewHolderVidio> {

    private ArrayList<String> list;
    private Context mcontext;


    public AdpterImageDetail(ArrayList<String> list, Context mcontext) {

        this.list = list;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public ViewHolderVidio onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        ViewHolderVidio chatViewHolder = new ViewHolderVidio(view);
        return chatViewHolder;

    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolderVidio holder, final int position) {

        Glide.with(mcontext)
                .load(list.get(position))
                .into(holder.imageView);




    }


    private void intentMothed(Class a, String value) {

        Intent intent = new Intent(mcontext, a);
        intent.putExtra("menuId", value);
        mcontext.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolderVidio extends RecyclerView.ViewHolder {

        ImageView imageView;


        public ViewHolderVidio(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_list);
        }


    }
}

