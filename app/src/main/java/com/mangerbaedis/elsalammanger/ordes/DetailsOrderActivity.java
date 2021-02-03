package com.mangerbaedis.elsalammanger.ordes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.mangerbaedis.elsalammanger.Coomen;
import com.mangerbaedis.elsalammanger.R;
import com.mangerbaedis.elsalammanger.databinding.ActivityDetailsBinding;
import com.mangerbaedis.elsalammanger.databinding.ActivityNotyOrderBinding;
import com.mangerbaedis.elsalammanger.home.HomeActivity;
import com.mangerbaedis.elsalammanger.pojo.Pro_Of_Product;
import com.mangerbaedis.elsalammanger.pojo.Request;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DetailsOrderActivity extends AppCompatActivity {
ActivityNotyOrderBinding binding;
Request request;
String order_id_value;
String key;
    DatabaseReference catogry;
    FirebaseDatabase databasefirbase;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_noty_order);
        databasefirbase = FirebaseDatabase.getInstance();
        catogry = databasefirbase.getReference().child("Orders");


        binding.listfoods.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        binding.listfoods.setHasFixedSize(true);
        if (getIntent()!=null){
            request=getIntent().getParcelableExtra("request");
            order_id_value = getIntent().getStringExtra("orderid");
            key = getIntent().getStringExtra("title");
            getDetailId(key);

            //set value
            if (order_id_value!=null)
            binding.orderId.setText(order_id_value);

            if (request!=null) {

                binding.orderAddress.setText("العنوان : "+request.getAddresse());
                binding.orderPhone.setText("رقم الهاتف : "+ request.getPhone());
                binding.orderTotal.setText("اجمالي السعر :" +request.getTotal());
                binding.orderComment.setText("التعليق  :" +request.getComment());
                binding.orderName.setText("اسم العميل  :" +request.getName());
                binding.orderDate.setText("وقت الطلب :" + Coomen.getDate(Long.parseLong(order_id_value)));


                AdpterOrders adpter=new AdpterOrders((ArrayList<Pro_Of_Product>)request.getFoods());
                adpter.notifyDataSetChanged();
                binding.listfoods.setAdapter(adpter);

            }



        }
    }

    private void getDetailId(final String foodid) {
        if (foodid != null) {
            catogry.child(foodid).addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   Request pro_adds = dataSnapshot.getValue(Request.class);
                    binding.orderId.setText(order_id_value);
                    binding.orderAddress.setText("العنوان : "+pro_adds.getAddresse());
                    binding.orderPhone.setText("رقم الهاتف : "+ pro_adds.getPhone());
                    binding.orderTotal.setText("اجمالي السعر :" +pro_adds.getTotal());
                    binding.orderComment.setText("التعليق  :" +pro_adds.getComment());
                    binding.orderName.setText("اسم العميل  :" +pro_adds.getName());
                    binding.orderDate.setText("وقت الطلب :" + Coomen.getDate(Long.parseLong(foodid)));
                    AdpterOrders adpter=new AdpterOrders((ArrayList<Pro_Of_Product>)pro_adds.getFoods());
                    adpter.notifyDataSetChanged();
                    binding.listfoods.setAdapter(adpter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DetailsOrderActivity.this, HomeActivity.class));
        finish();
    }
}