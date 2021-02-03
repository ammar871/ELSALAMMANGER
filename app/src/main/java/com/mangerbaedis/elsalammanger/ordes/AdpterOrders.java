package com.mangerbaedis.elsalammanger.ordes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mangerbaedis.elsalammanger.R;
import com.mangerbaedis.elsalammanger.pojo.Pro_Of_Product;

import java.util.ArrayList;

public class AdpterOrders extends RecyclerView.Adapter<AdpterOrders.MyViewHoldre> {


    ArrayList<Pro_Of_Product> myOrder;
    Context mcontext;

    public ArrayList<Pro_Of_Product> getMyOrder() {
        return myOrder;
    }

    public void setMyOrder(ArrayList<Pro_Of_Product> myOrder) {
        this.myOrder = myOrder;
    }

    public AdpterOrders(ArrayList<Pro_Of_Product> names) {
        this.myOrder = names;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public MyViewHoldre onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_layout, parent, false);
        MyViewHoldre chatViewHolder = new MyViewHoldre(view);
        return chatViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoldre holder, final int position) {
        Pro_Of_Product orders = myOrder.get(position);
        holder.name.setText(String.format("اسم المنتج  :%s", orders.getName()));
        holder.quantity.setText(String.format("الكمية  :%s", orders.getNumberQuntity()));

        holder.price.setText(String.format("سعر المنتج  :%s", orders.getPrice()));
        holder.size.setText(String.format("اسم المحل    :%s", orders.getNameOfMarket()));
        holder.discount.setText(String.format("تفاصيل المنتج : %s",orders.getDescShort()));


    }

    @Override
    public int getItemCount() {
        return myOrder.size();
    }

    public class MyViewHoldre extends RecyclerView.ViewHolder {

        public TextView name, quantity, price, discount, size, color;

        public MyViewHoldre(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.prodecut_name);
            quantity = itemView.findViewById(R.id.product_quantity);
            price = itemView.findViewById(R.id.product_price);
            size = itemView.findViewById(R.id.product_size);

            discount = itemView.findViewById(R.id.product_discount);

        }


    }
}
