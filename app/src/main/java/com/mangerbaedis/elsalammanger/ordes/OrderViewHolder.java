package com.mangerbaedis.elsalammanger.ordes;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.mangerbaedis.elsalammanger.R;


public class OrderViewHolder extends RecyclerView.ViewHolder implements

        View.OnClickListener{

    public TextView txtorderid, txtDate, txtordesphon, txtorderaddress;
 //   private ItemClickLisener itemClickLisener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtorderid = itemView.findViewById(R.id.order_id);
        txtDate = itemView.findViewById(R.id.order_date);
        txtordesphon = itemView.findViewById(R.id.order_phone);
        txtorderaddress = itemView.findViewById(R.id.order_address);



    }




    @Override
    public void onClick(View v) {



    }


}
