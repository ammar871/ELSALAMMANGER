package com.elsalmcity.elsalammanger.addProducts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.elsalmcity.elsalammanger.Coomen;
import com.elsalmcity.elsalammanger.R;
import com.elsalmcity.elsalammanger.databinding.ActivityAddProdctsBinding;
import com.elsalmcity.elsalammanger.databinding.ActivityUpdateBinding;
import com.elsalmcity.elsalammanger.home.HomeActivity;
import com.elsalmcity.elsalammanger.pojo.Product;
import com.elsalmcity.elsalammanger.produts.AdpterImages;
import com.elsalmcity.elsalammanger.produts.ProductsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class UpdateActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    ActivityUpdateBinding binding;
    ArrayList<Uri> imagesList = new ArrayList<Uri>();
    ArrayList<String> addImages = new ArrayList<>();
    private Uri imageUri;
    private int uploadCount = 0;
    private DatabaseReference catogry;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    Product product;
    AdpterImages adpterImages;
    String getId;
    int currentImageSelected;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_update);


        pd = new ProgressDialog(UpdateActivity.this);
        if (getIntent() != null) {


            product = getIntent().getParcelableExtra("productUpdate");
            if (product != null) {
                binding.eddesc.setText(product.getDesc());
                binding.ednameProdcut.setText(product.getName());
                binding.edaddress.setText(product.getAddress());
                binding.edshortDesc.setText(product.getDescshort());
                binding.edpage.setText(product.getPageFace());
                binding.edPhonenumber.setText(product.getPhoneNumber());
            }

        }

        binding.imageAddList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        binding.imageAddList.setHasFixedSize(true);
        //firebase
        database = FirebaseDatabase.getInstance();
        catogry = database.getReference().child("prodects");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        binding.linerSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagesList.clear();
                ChooseImage();
            }
        });

        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Upload();
            }
        });

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendLink();
            }
        });

    }

    private void ChooseImage() {
        Intent glary = new Intent(Intent.ACTION_GET_CONTENT);
        glary.setType("image/*");
        glary.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(glary, "Selecet pictur"), PICK_IMAGE);


    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data
                != null && data.getClipData() != null) {
            int countClipeData = data.getClipData().getItemCount();

            currentImageSelected = 0;
            while (currentImageSelected < countClipeData) {
                imageUri = data.getClipData().getItemAt(currentImageSelected).getUri();
                imagesList.add(imageUri);

                currentImageSelected = currentImageSelected + 1;
            }
            adpterImages = new AdpterImages(imagesList, this);
            binding.imageAddList.setAdapter(adpterImages);
            binding.tvCount.setText("You Have Selected  " + imagesList.size() + "images");

        } else {
            Toast.makeText(this, "Please Select Multiple Image ", Toast.LENGTH_SHORT).show();
        }
    }

    private void Upload() {

        pd.setMessage("Upload...");
        pd.show();
        final StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("ImageFolder");
        for (uploadCount = 0; uploadCount < imagesList.size(); uploadCount++) {
            Uri Image = imagesList.get(uploadCount);
            final StorageReference imagename = ImageFolder.child("image/" + Image.getLastPathSegment());

            imagename.putFile(imagesList.get(uploadCount)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();
                    Toast.makeText(UpdateActivity.this, "upload...", Toast.LENGTH_SHORT).show();
                    imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String url = String.valueOf(uri);
                            addImages.add(url);


                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });


        }


    }

    private void SendLink() {
        pd.show();
        Product product = new Product(ProductsActivity.getMenuId, binding.ednameProdcut.getText().toString()
                , binding.eddesc.getText().toString(),
                binding.edaddress.getText().toString(),
                binding.edshortDesc.getText().toString(),
                binding.edpage.getText().toString(),
                binding.edPhonenumber.getText().toString(),
                addImages);
        catogry.child(binding.ednameProdcut.getText().toString())
                .setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(UpdateActivity.this, "Success", Toast.LENGTH_SHORT).show();
                binding.ednameProdcut.setText("");
                binding.edaddress.setText("");
                binding.edaddress.setText("");
                binding.edshortDesc.setText("");
                binding.edPhonenumber.setText("");
                binding.edpage.setText("");

                startActivity(new Intent(UpdateActivity.this, HomeActivity.class));
                finish();

            }
        });


    }
}