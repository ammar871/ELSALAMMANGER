package com.mangerbaedis.elsalammanger.pagesMarkets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mangerbaedis.elsalammanger.Coomen;
import com.mangerbaedis.elsalammanger.R;
import com.mangerbaedis.elsalammanger.addProducts.AddPro_cato_Activity;
import com.mangerbaedis.elsalammanger.addProducts.bunners.Adds_BunnersActivity;
import com.mangerbaedis.elsalammanger.addProducts.bunners.AdpterAdds;
import com.mangerbaedis.elsalammanger.databinding.ActivityAddaFagalaBinding;
import com.mangerbaedis.elsalammanger.databinding.ActivityAddsBunnersBinding;
import com.mangerbaedis.elsalammanger.home.HomeActivity;
import com.mangerbaedis.elsalammanger.pojo.Pro_Of_Product;

import java.util.ArrayList;
import java.util.Collections;

public class AddaFagalaActivity extends AppCompatActivity {

    ActivityAddaFagalaBinding binding;
    DatabaseReference adds;
    FirebaseDatabase database;
    ArrayList<Pro_Of_Product> list;
    Adpter_Adds_Fagala adpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_adda_fagala);
        database = FirebaseDatabase.getInstance();
        adds = database.getReference().child("اعلانات");
        list = new ArrayList<>();

        binding.rcList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.rcList.setHasFixedSize(true);

        loadDataIntentnt();

    }

    private void loadDataIntentnt() {

        if (Coomen.isNetworkOnline(this)) {
            try {
                adds.orderByChild("nameOfMarket")
                        .equalTo("هايبر الفجالة").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Log.d("Tagstorag", "onSuccess :" + dataSnapshot1.child("name").getValue());
                            Pro_Of_Product blog = dataSnapshot1.getValue(Pro_Of_Product.class);
                            list.add(blog);
                            Collections.reverse(list);
                        }

                        adpter = new Adpter_Adds_Fagala(list, AddaFagalaActivity.this);
                        binding.rcList.setAdapter(adpter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {


                    }

                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            Toast.makeText(this, " انقطع الاتصال بالانترنت ", Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddaFagalaActivity.this, PageFagalaActivity.class));
        finish();
    }
}