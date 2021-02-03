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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mangerbaedis.elsalammanger.Coomen;
import com.mangerbaedis.elsalammanger.R;
import com.mangerbaedis.elsalammanger.addProducts.AddPro_cato_Activity;
import com.mangerbaedis.elsalammanger.addProducts.AdpterPro_cato;
import com.mangerbaedis.elsalammanger.addProducts.OffersActivity;
import com.mangerbaedis.elsalammanger.databinding.ActivityOffersBinding;
import com.mangerbaedis.elsalammanger.databinding.ActivityPageFagalaBinding;
import com.mangerbaedis.elsalammanger.pojo.Pro_Of_Product;

import java.util.ArrayList;

public class PageFagalaActivity extends AppCompatActivity {
    public static String getMenuId;
    String catoname;
    private DatabaseReference catogry;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    AdpterFagala adpterProduct;
    private StorageReference storageReference;
    ActivityPageFagalaBinding binding;
    ArrayList<Pro_Of_Product> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_page_fagala);
        //recycler
        binding.rcList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.rcList.setHasFixedSize(true);


        list = new ArrayList<>();
      binding.menuid.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(PageFagalaActivity.this, AddaFagalaActivity.class);

              startActivity(intent);
          }
      });
        binding.fap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PageFagalaActivity.this, AddProudctFagalaActivity.class);
                intent.putExtra("id", getMenuId);
                startActivity(intent);
            }
        });
        loadDataIntentnt();


    }

    private void loadDataIntentnt() {
        if (Coomen.isNetworkOnline(this)) {
            try {
                FirebaseDatabase.getInstance().getReference().child("منتجات").orderByChild("nameOfMarket")
                        .equalTo("هايبر الفجالة").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Log.d("Tagstorag", "onSuccess: " + dataSnapshot1.child("name").getValue());
                            Pro_Of_Product blog = dataSnapshot1.getValue(Pro_Of_Product.class);


                            list.add(blog);


                        }
                        adpterProduct = new AdpterFagala(list, PageFagalaActivity.this);
                        binding.rcList.setAdapter(adpterProduct);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "انقطع الاتصال بالانترنت ", Toast.LENGTH_SHORT).show();
        }


    }
}