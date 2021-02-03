package com.mangerbaedis.elsalammanger.notifecation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mangerbaedis.elsalammanger.Coomen;
import com.mangerbaedis.elsalammanger.R;
import com.mangerbaedis.elsalammanger.databinding.ActivityNotificationBinding;
import com.mangerbaedis.elsalammanger.home.HomeActivity;
import com.mangerbaedis.elsalammanger.pojo.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
        if (Coomen.isNetworkOnline(this)) {
            try {
                notyRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "انقطع الاتصال بالانترنت ", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(NotificationActivity.this, HomeActivity.class));
        finish();
    }
}