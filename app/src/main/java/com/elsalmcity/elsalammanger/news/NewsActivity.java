package com.elsalmcity.elsalammanger.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.elsalmcity.elsalammanger.R;
import com.elsalmcity.elsalammanger.databinding.ActivityNewsBinding;
import com.elsalmcity.elsalammanger.databinding.ActivityNotificationBinding;
import com.elsalmcity.elsalammanger.home.HomeActivity;
import com.elsalmcity.elsalammanger.notifecation.AdpterNotifecation;
import com.elsalmcity.elsalammanger.notifecation.Data;
import com.elsalmcity.elsalammanger.notifecation.NotificationActivity;
import com.elsalmcity.elsalammanger.pojo.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class NewsActivity extends AppCompatActivity {
ActivityNewsBinding binding;

    ArrayList<Data> list;
    AdpterNews adpterNotifecation;
    DatabaseReference notyRef;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news);

        binding.fap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewsActivity.this,AddNewsActivity.class));
                finish();
            }
        });
        list = new ArrayList<>();
        binding.rcList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.rcList.setHasFixedSize(true);

        //firebase
        database = FirebaseDatabase.getInstance();
        notyRef = database.getReference().child("News");
        loadDataIntentnt();


    }
    private void loadDataIntentnt() {
        notyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.d("Tagstorag", "onSuccess :" + dataSnapshot1.child("name").getValue());
                    Data blog = dataSnapshot1.getValue(Data.class);


                    list.add(blog);
                    Collections.reverse(list);

                }
                adpterNotifecation = new AdpterNews(list, NewsActivity.this);
                binding.rcList.setAdapter(adpterNotifecation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(NewsActivity.this, HomeActivity.class));
        finish();
    }}