package com.elsalmcity.elsalammanger.produts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.elsalmcity.elsalammanger.addProducts.AddProdctsActivity;
import com.elsalmcity.elsalammanger.databinding.ActivityAddProdctsBinding;
import com.elsalmcity.elsalammanger.databinding.ActivityProductsBinding;
import com.elsalmcity.elsalammanger.pojo.Catogray;
import com.elsalmcity.elsalammanger.R;
import com.elsalmcity.elsalammanger.pojo.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProductsActivity extends AppCompatActivity {
 public static String getMenuId;
    String catoname;
    private DatabaseReference catogry;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    AdpterProduct adpterProduct;
    private StorageReference storageReference;
    ActivityProductsBinding binding;
   ArrayList<Uri> uris;
    ArrayList<Product> list;
    Catogray newcatogrt;
    private final int PICK_IMAGE = 71;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_products);

        if (getIntent()!=null){
            getMenuId=getIntent().getStringExtra("menuId");
            catoname=getIntent().getStringExtra("name");
            binding.menuid.setText(catoname);



        }

list=new ArrayList<>();
        binding.rcList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        binding.rcList.setHasFixedSize(true);
        //firebase
        database = FirebaseDatabase.getInstance();
        catogry = database.getReference().child("prodects");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        loadDataIntentnt(catogry);

        binding.fap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent=new Intent(ProductsActivity.this, AddProdctsActivity.class);
            intent.putExtra("id",getMenuId);
            startActivity(intent);
            }
        });
    }

    private void loadDataIntentnt(DatabaseReference ref) {

        FirebaseDatabase.getInstance().getReference().child("prodects").orderByChild("id").equalTo(getMenuId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.d("Tagstorag", "onSuccess: " + dataSnapshot1.child("name").getValue());
                    Product blog = dataSnapshot1.getValue(Product.class);


                    list.add(blog);



                }
                adpterProduct=new AdpterProduct(list,ProductsActivity.this);
                binding.rcList.setAdapter(adpterProduct);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
}}
