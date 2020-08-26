package com.elsalmcity.elsalammanger.details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.elsalmcity.elsalammanger.R;
import com.elsalmcity.elsalammanger.databinding.ActivityDetailsBinding;
import com.elsalmcity.elsalammanger.pojo.Product;
import com.elsalmcity.elsalammanger.produts.AdpterImageDetail;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
ActivityDetailsBinding binding;
Product product;
AdpterImageDetail adpterImageDetail;
ArrayList<String>listImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        binding.listImages.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));
        binding.listImages.setHasFixedSize(true);
listImage=new ArrayList<>();
        if (getIntent()!=null){
            product=getIntent().getParcelableExtra("product");

        }
       if (product != null){
           binding.tvName.setText(product.getName());
           binding.tvDesc.setText(product.getDesc());
           Glide.with(this).load(product.getUris().get(0))
                   .into(binding.imageView);

           int size=product.getUris().size();
           for (int i=0;i<size;i++){
               String image= product.getUris().get(i);
               listImage.add(image);
               Log.d("Tagstorag", "onSuccess: " +image);

           }
           adpterImageDetail=new AdpterImageDetail(listImage,DetailsActivity.this);
           binding.listImages.setAdapter(adpterImageDetail);
       }


    }
}