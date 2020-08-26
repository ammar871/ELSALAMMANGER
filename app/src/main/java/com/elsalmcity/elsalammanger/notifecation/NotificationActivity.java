package com.elsalmcity.elsalammanger.notifecation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elsalmcity.elsalammanger.R;
import com.elsalmcity.elsalammanger.databinding.ActivityNotificationBinding;
import com.elsalmcity.elsalammanger.home.HomeActivity;
import com.elsalmcity.elsalammanger.pojo.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class NotificationActivity extends AppCompatActivity {
    ActivityNotificationBinding binding;
    ArrayList<Product> list;
    AdpterNotifecation adpterNotifecation;
    DatabaseReference notyRef;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        list = new ArrayList<>();
        binding.listNoty.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.listNoty.setHasFixedSize(true);

        //firebase
        database = FirebaseDatabase.getInstance();
        notyRef = database.getReference().child("Notifecations");

        binding.imagback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NotificationActivity.this, HomeActivity.class));
                finish();
            }
        });
        loadDataIntentnt();

    }

    private void loadDataIntentnt() {
        notyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.d("Tagstorag", "onSuccess :" + dataSnapshot1.child("name").getValue());
                    Product blog = dataSnapshot1.getValue(Product.class);


                    list.add(blog);

                }
                adpterNotifecation = new AdpterNotifecation(list, NotificationActivity.this);
                binding.listNoty.setAdapter(adpterNotifecation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(NotificationActivity.this, HomeActivity.class));
        finish();
    }
}