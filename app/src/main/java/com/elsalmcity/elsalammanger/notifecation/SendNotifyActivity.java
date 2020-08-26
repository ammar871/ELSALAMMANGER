package com.elsalmcity.elsalammanger.notifecation;

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

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.elsalmcity.elsalammanger.R;
import com.elsalmcity.elsalammanger.databinding.ActivitySendNotifyBinding;

import com.elsalmcity.elsalammanger.home.HomeActivity;
import com.elsalmcity.elsalammanger.pojo.Product;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotifyActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    ActivitySendNotifyBinding binding;
    String uri;
    ApiServes mservies;
    Uri saveuri;
    DatabaseReference catogry;
    FirebaseStorage storage;
    Product product;
    private StorageReference storageReference;
    private RequestQueue mRequestQue;
    private String URL = "https://fcm.googleapis.com/fcm/send";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_send_notify);
        mRequestQue = Volley.newRequestQueue(this);
        mservies = Claint.getClient().create(ApiServes.class);
        //firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        catogry = database.getReference().child("Notifecations");

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
        if (binding.ednameProdcut.getText().toString().isEmpty()
                && binding.ednameProdcut.getText().toString().equals("")) {
            binding.ednameProdcut.setError("اكتب اسم المنتج");
            return false;

        } else if (binding.edshortDesc.getText().toString().isEmpty()
                && binding.edshortDesc.getText().toString().equals("")) {
            binding.edshortDesc.setError("اكتب التفاصيل ");
            return false;

        } else if (binding.edPhonenumber.getText().toString().isEmpty()
                && binding.edPhonenumber.getText().toString().equals("")) {
            binding.edPhonenumber.setError("اكتب رقم الموبايل");
            return false;

        } else if (binding.edpage.getText().toString().isEmpty()
                && binding.edpage.getText().toString().equals("")) {
            binding.edpage.setError("اضف صفحة الرسمية");
            return false;

        } else if (binding.eddesc.getText().toString().isEmpty()
                && binding.eddesc.getText().toString().equals("")) {
            binding.eddesc.setError("اكتب التفاصيل");
            return false;

        } else if (binding.edaddress.getText().toString().isEmpty()
                && binding.edaddress.getText().toString().equals("")) {
            binding.edaddress.setError("اكتب العنوان ");
            return false;

        } else if (saveuri == null) {
            Toast.makeText(this, "اختار الصورة", Toast.LENGTH_SHORT).show();
            return false;

        } else {
            return true;
        }
    }

    private void SendRetrofitNotifection(final String title, final String body, final String uri) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference().child("Tokens");
        Query data = tokens.orderByChild("isservecToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot1 : dataSnapshot.getChildren()) {
                    Token servertoken = postSnapshot1.getValue(Token.class);
                    Data notifacation = new Data(title, body, uri,"com.com.elsalmcity.elsalamcity.notifecation_TARGET_NOTIFICATION");
                    assert servertoken != null;
                    Sender sender = new Sender(servertoken.getToken(), notifacation);
                    mservies.sendNotifectoin(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code()==200){
                                Toast.makeText(SendNotifyActivity.this, "تم الارسال ", Toast.LENGTH_SHORT).show();
                                catogry.child(binding.ednameProdcut.getText().toString())
                                        .setValue(product);
                                startActivity(new Intent(SendNotifyActivity.this, SendNotifyActivity.class));
                                finish();

                            }
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

    void sendNotifecation(final String title, final String body, final String uri){
        JSONObject json = new JSONObject();
        try {
            json.put("to", "/topics/salam");
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", title);
            notificationObj.put("body", body);
            notificationObj.put("sound", "default");
            notificationObj.put("image", uri);
            notificationObj.put("click_action", "com.com.elsalmcity.elsalamcity.notifecation_TARGET_NOTIFICATION");
            json.put("notification", notificationObj);
            JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, URL,
                    json,
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Toast.makeText(SendNotifyActivity.this, "تم الارسال", Toast.LENGTH_SHORT).show();
                            catogry.child(binding.ednameProdcut.getText().toString())
                                    .setValue(product);
                            startActivity(new Intent(SendNotifyActivity.this, SendNotifyActivity.class));
                            finish();

                        }
                    }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("MUR", "onError: " + error.networkResponse);
                }
            }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAADn5zSWA:APA91bEqHE5En1IkJrgXAYmAYwMMt3IGPEygp4x61OSb_S29KfVjIhyfPJh1YeMPmzv95ZAZv5Us4W_nnHnp4kBlXmToO6V2gdxVyo4sN-t1JPlR3sHJmKn1uWm4LkRDTLa9QfdusWfT");
                    return header;
                }
            };
            mRequestQue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
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
            final ProgressDialog pd = new ProgressDialog(SendNotifyActivity.this);
            pd.setMessage("Upload...");
            pd.show();
            String ImageName = UUID.randomUUID().toString();

            final StorageReference imagefolder = storageReference.child("images/" + ImageName);
            imagefolder.putFile(saveuri).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Toast.makeText(SendNotifyActivity.this, "upload...", Toast.LENGTH_SHORT).show();
                            imagefolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    product = new Product(binding.ednameProdcut.getText().toString()
                                            , binding.eddesc.getText().toString(), binding.edaddress.getText().toString()
                                            , binding.edshortDesc.getText().toString(),
                                            binding.edpage.getText().toString()
                                            , binding.edPhonenumber.getText().toString()
                                            , uri.toString());
                                    SendRetrofitNotifection(binding.edshortDesc.getText().toString(),
                                            binding.ednameProdcut.getText().toString(),
                                            uri.toString());



                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(SendNotifyActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SendNotifyActivity.this, HomeActivity.class));
        finish();

    }
}