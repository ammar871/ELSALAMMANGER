package com.mangerbaedis.elsalammanger.addProducts;

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

import com.mangerbaedis.elsalammanger.Coomen;
import com.mangerbaedis.elsalammanger.R;
import com.mangerbaedis.elsalammanger.databinding.ActivityOffersBinding;
import com.mangerbaedis.elsalammanger.databinding.ActivityProductsBinding;
import com.mangerbaedis.elsalammanger.pojo.Pro_Of_Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class OffersActivity extends AppCompatActivity {
    public static String getMenuId;
    String catoname;
    private DatabaseReference catogry;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    AdpterPro_cato adpterProduct;
    private StorageReference storageReference;
    ActivityOffersBinding binding;
    ArrayList<Pro_Of_Product> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_offers);
        //recycler
        binding.rcList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.rcList.setHasFixedSize(true);


        list = new ArrayList<>();
        if (getIntent() != null) {
            getMenuId = getIntent().getStringExtra("menuId");
            catoname = getIntent().getStringExtra("name");
            binding.menuid.setText(catoname);


        }
        binding.fap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(OffersActivity.this, AddPro_cato_Activity.class);
                intent.putExtra("id", getMenuId);
                startActivity(intent);
            }
        });
        loadDataIntentnt();


    }

        private void loadDataIntentnt() {
        if (Coomen.isNetworkOnline(this)) {
            try {
                FirebaseDatabase.getInstance().getReference().child("منتجات").orderByChild("idcatogray")
                        .equalTo(getMenuId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Log.d("Tagstorag", "onSuccess: " + dataSnapshot1.child("name").getValue());
                            Pro_Of_Product blog = dataSnapshot1.getValue(Pro_Of_Product.class);


                            list.add(blog);


                        }
                        adpterProduct = new AdpterPro_cato(list, OffersActivity.this);
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