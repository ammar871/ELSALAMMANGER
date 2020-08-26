package com.elsalmcity.elsalammanger.news;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import com.android.volley.toolbox.Volley;
import com.elsalmcity.elsalammanger.R;
import com.elsalmcity.elsalammanger.databinding.ActivityAddNewsBinding;

import com.elsalmcity.elsalammanger.home.HomeActivity;
import com.elsalmcity.elsalammanger.notifecation.ApiServes;
import com.elsalmcity.elsalammanger.notifecation.Claint;
import com.elsalmcity.elsalammanger.notifecation.Data;
import com.elsalmcity.elsalammanger.notifecation.MyResponse;

import com.elsalmcity.elsalammanger.notifecation.Sender;
import com.elsalmcity.elsalammanger.notifecation.Token;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewsActivity extends AppCompatActivity {
ActivityAddNewsBinding binding;
    private static final int PICK_IMAGE = 1;
    Data datanoty;
    String uri;
    ApiServes mservies;
    Uri saveuri;
    DatabaseReference catogry;
    FirebaseStorage storage;

    private StorageReference storageReference;
    private RequestQueue mRequestQue;
    private String URL = "https://fcm.googleapis.com/fcm/send";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_news);

        mRequestQue = Volley.newRequestQueue(this);
        mservies = Claint.getClient().create(ApiServes.class);
        //firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        catogry = database.getReference().child("News");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValue()) {
                    UploadImage();
                }

            }
        });
        binding.imagNotiy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImage();
            }
        });
    }
    private boolean isValue() {
        if (binding.edTitle.getText().toString().isEmpty()
                && binding.edTitle.getText().toString().equals("")) {
            binding.edTitle.setError("اكتب العنوان");
            return false;

        } else if (binding.edBody.getText().toString().isEmpty()
                && binding.edBody.getText().toString().equals("")) {
            binding.edBody.setError("اكتب الرسالة ");
            return false;

        } else if (saveuri == null) {
            Toast.makeText(this, "اختار الصورة", Toast.LENGTH_SHORT).show();
            return false;

        } else {
            return true;
        }
    }
    private void ChooseImage() {
        Intent glary = new Intent(Intent.ACTION_GET_CONTENT);
        glary.setType("image/*");
        glary.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(glary, "Selecet pictur"), PICK_IMAGE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data
                != null && data.getData() != null) {
            saveuri = data.getData();
            binding.imagNotiy.setImageURI(saveuri);

        }
    }
    private void UploadImage() {
        if (saveuri != null) {
            final ProgressDialog pd = new ProgressDialog(AddNewsActivity.this);
            pd.setMessage("Upload...");
            pd.show();
            String ImageName = UUID.randomUUID().toString();

            final StorageReference imagefolder = storageReference.child("images/" + ImageName);
            imagefolder.putFile(saveuri).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Toast.makeText(AddNewsActivity.this, "upload...", Toast.LENGTH_SHORT).show();
                            imagefolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    datanoty=new Data(binding.edBody.getText().toString(),
                                            binding.edTitle.getText().toString(),
                                            uri.toString(),"");
                                    SendRetrofitNotifection(binding.edBody.getText().toString(),
                                            binding.edTitle.getText().toString(),
                                            uri.toString());



                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(AddNewsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    pd.setMessage("upload" + progress + "");
                }
            });
        }
    }

    private void SendRetrofitNotifection(final String title, final String body, final String uri) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference().child("Tokens");
        final Query data = tokens.orderByChild("isservecToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot1 : dataSnapshot.getChildren()) {
                    Token servertoken = postSnapshot1.getValue(Token.class);
                    Data notifacation = new Data( title,body, uri,"com.com.elsalmcity.elsalamcity.notifecation_NEWS_NOTIFICATION");
                    assert servertoken != null;
                    Sender sender = new Sender(servertoken.getToken(), notifacation);
                    mservies.sendNotifectoin(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                Toast.makeText(AddNewsActivity.this, "تم الارسال ", Toast.LENGTH_SHORT).show();
                                catogry.child(binding.edTitle.getText().toString())
                                        .setValue(datanoty);
                                startActivity(new Intent(AddNewsActivity.this, AddNewsActivity.class));
                                finish();


                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
                startActivity(new Intent(AddNewsActivity.this, HomeActivity.class));
        finish();
    }
}