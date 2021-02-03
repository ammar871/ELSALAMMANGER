package com.mangerbaedis.elsalammanger.addProducts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.mangerbaedis.elsalammanger.R;
import com.mangerbaedis.elsalammanger.databinding.ActivityAddProCatoBinding;
import com.mangerbaedis.elsalammanger.home.HomeActivity;
import com.mangerbaedis.elsalammanger.notifecation.ApiServes;
import com.mangerbaedis.elsalammanger.notifecation.Claint;
import com.mangerbaedis.elsalammanger.notifecation.Data;
import com.mangerbaedis.elsalammanger.notifecation.MyResponse;
import com.mangerbaedis.elsalammanger.notifecation.SendNotifyActivity;
import com.mangerbaedis.elsalammanger.notifecation.Sender;
import com.mangerbaedis.elsalammanger.pojo.Pro_Of_Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPro_cato_Activity extends AppCompatActivity implements OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
        , LocationListener {
    private static final int PICK_IMAGE = 1;
    ActivityAddProCatoBinding binding;
    Uri saveuri;
    String imageUri;
    String tvadds;
    DatabaseReference catogry,adds;
    FirebaseStorage storage;
    Pro_Of_Product product;
String idCatogray;
    ApiServes mservies;
    ProgressDialog pd;
    private StorageReference storageReference;


    //location
    //location
    private Location mlastlocation;
    private GoogleApiClient mGooglapiclent;
    private LocationRequest mlocationrequest;

    private final static int UPDATE_INTERVAL = 5000;

    private final static int FASTEST_INTERVAL = 3000;
    private final static int DISPLAYCEMENT = 10;
    private final static int LOCATION_PER_REQUEST = 9999;
    private final static int PLAY_SER_REQUEST = 9997;
    private double latitude;
    private double longtude;
    private static String nameAdrees;
boolean delavariy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_pro_cato_);



        //primssion location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION

                    }, LOCATION_PER_REQUEST);
        } else {
            if (Checkplayservic()) {

                buildgoogleapiclaint();
                creatlocationrequest();
            }

        }

getSwicheDelivary();

        mservies = Claint.getClient().create(ApiServes.class);
        if (getIntent() != null) {

            product = getIntent().getParcelableExtra("updatePro");
            tvadds=getIntent().getStringExtra("bunners");
            idCatogray=getIntent().getStringExtra("id");
            if (product != null) {
                binding.ednameProdcut.setText(product.getName());
                binding.edshortDesc.setText(product.getDescShort());
                binding.edDesc.setText(product.getDescription());
                binding.edPrice.setText(product.getPrice());
                binding.edLocationDelar.setText(product.getLocationOfMarket());
                binding.edNameDelar.setText(product.getNameOfMarket());
                binding.edPhonDelar.setText(product.getPhoneOfMarket());




            }
        }


        pd = new ProgressDialog(AddPro_cato_Activity.this);

        //FIREBASE
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        catogry = database.getReference().child("منتجات");
        adds = database.getReference().child("اعلانات");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        binding.imageShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImage();
            }
        });

        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValue()) {
                    UploadImage();
                }

            }
        });
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendLink(product);
            }
        });


binding.btnAddandnoty.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        SendLinkandNoty(product);
    }
});
        binding.shipToAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        try {
                            GetLocation(latitude, longtude);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        binding.edLocationDelar.setText(nameAdrees);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(AddPro_cato_Activity.this, "من فضلك قم بتشغيل الموقع ", Toast.LENGTH_SHORT).show();
                    }



                }

            }
        });
    }

    private void getSwicheDelivary() {

        binding.switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              if (isChecked){
                  delavariy=true;
              } else {
                  delavariy=false;
              }
            }
        });
    }

    private void ChooseImage() {
        Intent glary = new Intent(Intent.ACTION_GET_CONTENT);
        glary.setType("image/*");
        glary.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(glary, "Selecet pictur"), PICK_IMAGE);


    }


    private void UploadImage() {
        if (saveuri != null) {

            pd.setMessage("Upload...");
            pd.show();
            String ImageName = UUID.randomUUID().toString();

            final StorageReference imagefolder = storageReference.child("images/" + ImageName);
            imagefolder.putFile(saveuri).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Toast.makeText(AddPro_cato_Activity.this, "upload...", Toast.LENGTH_SHORT).show();
                            imagefolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override


                                public void onSuccess(Uri uri) {
                                    product = new Pro_Of_Product(binding.ednameProdcut.getText().toString()
                                            , binding.edshortDesc.getText().toString().trim()
                                            , binding.edDesc.getText().toString().trim(),
                                              binding.edPrice.getText().toString().trim()
                                            , binding.edNameDelar.getText().toString().trim(),
                                              binding.edPhonDelar.getText().toString().trim(),
                                              binding.edLocationDelar.getText().toString().trim(),
                                              uri.toString()
                                             ,idCatogray
                                    ,delavariy);


                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(AddPro_cato_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

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


    private boolean isValue() {
        if (binding.ednameProdcut.getText().toString().isEmpty()
                && binding.ednameProdcut.getText().toString().equals("")) {
            binding.ednameProdcut.setError("اكتب اسم المنتج");
            return false;

        } else if (binding.edshortDesc.getText().toString().isEmpty()
                && binding.edshortDesc.getText().toString().equals("")) {
            binding.edshortDesc.setError("اكتب وصف قصير");
            return false;

        } else if (binding.edDesc.getText().toString().isEmpty()
                && binding.edDesc.getText().toString().equals("")) {
            binding.edDesc.setError("اكتب وصف كامل");
            return false;

        }
        else if (binding.edLocationDelar.getText().toString().isEmpty()
                && binding.edLocationDelar.getText().toString().equals("")) {
            binding.edLocationDelar.setError("اكتب الموقع");
            return false;

        }
        else if (binding.edPrice.getText().toString().isEmpty()
                && binding.edPrice.getText().toString().equals("")) {
            binding.edPrice.setError("اكتب السعر");
            return false;

        } else if (binding.edNameDelar.getText().toString().isEmpty()
                && binding.edNameDelar.getText().toString().equals("")) {
            binding.edNameDelar.setError("اكتب اسم المحل");
            return false;

        }
        else if (binding.edPhonDelar.getText().toString().isEmpty()
                && binding.edPhonDelar.getText().toString().equals("")) {
            binding.edPhonDelar.setError("اكتب رقم للتواصل");
            return false;

        }

        else if (saveuri == null) {
            Toast.makeText(this, "اختار الصورة", Toast.LENGTH_SHORT).show();
            return false;

        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data
                != null && data.getData() != null) {
            saveuri = data.getData();
            binding.imageShow.setImageURI(saveuri);

        }
    }


    private void SendLink(final Pro_Of_Product pro_of_product) {
        try {
            pd.setMessage("جارى التحميل...");
            pd.show();

            if (tvadds!=null){
                adds.push()
                        .setValue(pro_of_product).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        sendRetrofitNotifection(pro_of_product.getDescShort(),pro_of_product.getName(),pro_of_product.getImage());
                        pd.dismiss();

                        binding.ednameProdcut.setText("");
                        binding.edshortDesc.setText("");
                        binding.edDesc.setText("");
                        binding.edPrice.setText("");
                        binding.edNameDelar.setText("");
                        binding.edLocationDelar.setText("");

                        Toast.makeText(AddPro_cato_Activity.this, "تمت الاضافة الى الاعلانات ", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddPro_cato_Activity.this, HomeActivity.class));
                        finish();

                    }
                });
            }else {
                catogry.push()
                        .setValue(pro_of_product).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();

                        binding.ednameProdcut.setText("");
                        binding.edshortDesc.setText("");
                        binding.edDesc.setText("");
                        binding.edPrice.setText("");
                        binding.edNameDelar.setText("");
                        binding.edLocationDelar.setText("");
                        Toast.makeText(AddPro_cato_Activity.this, "تمت الاضافة ", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddPro_cato_Activity.this, HomeActivity.class));
                        finish();

                    }
                });

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private void SendLinkandNoty(final Pro_Of_Product pro_of_product) {
        try {
            pd.setMessage("جارى التحميل...");
            pd.show();


                adds.push()
                        .setValue(pro_of_product).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        sendRetrofitNotifection(pro_of_product.getDescShort(),pro_of_product.getName(),pro_of_product.getImage());
                        pd.dismiss();

                        binding.ednameProdcut.setText("");
                        binding.edshortDesc.setText("");
                        binding.edDesc.setText("");
                        binding.edPrice.setText("");
                        binding.edNameDelar.setText("");
                        binding.edLocationDelar.setText("");



                    }
                });

                catogry.push()
                        .setValue(pro_of_product).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();

                        binding.ednameProdcut.setText("");
                        binding.edshortDesc.setText("");
                        binding.edDesc.setText("");
                        binding.edPrice.setText("");
                        binding.edNameDelar.setText("");
                        binding.edLocationDelar.setText("");
                        Toast.makeText(AddPro_cato_Activity.this, "تمت الاضافة ", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddPro_cato_Activity.this, HomeActivity.class));
                        finish();

                    }
                });




        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void sendRetrofitNotifection(final String title, final String body, final String uri) {


        Data notifacation = new Data(title, body, uri,"com.bardisammar.elsalamcity.notifecation_PROUDACT_NOTIFICATION");

        Sender sender = new Sender("/topics/bardis", notifacation);
        mservies.sendNotifectoin(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyResponse> call,@NonNull Response<MyResponse> response) {
                if (response.code()==200){
                    Toast.makeText(AddPro_cato_Activity.this, "تم الارسال ", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onFailure(@NonNull Call<MyResponse> call,@NonNull Throwable t) {

            }
        });


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            displaylocation();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startlocationupdates();
    }
    private void startlocationupdates() {

        try{

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.
                    requestLocationUpdates(mGooglapiclent, mlocationrequest, this);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private void displaylocation() throws IOException {


        try {



            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        mlastlocation = LocationServices.FusedLocationApi.getLastLocation(mGooglapiclent);
        if (mlastlocation != null) {
            latitude = mlastlocation.getLatitude();
            longtude = mlastlocation.getLongitude();
            GetLocation(latitude, longtude);

            Log.v("location", "couldn't get Location " + latitude + "\n" + longtude);


        } else {
            Log.d("location", "couldn't get Location ");

        }
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    private boolean Checkplayservic() {
        int resultcode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultcode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultcode)) {

                GooglePlayServicesUtil.getErrorDialog(resultcode, this, PLAY_SER_REQUEST).show();
            } else {
                Toast.makeText(this, "Not Support", Toast.LENGTH_SHORT).show();

            }
            return false;
        }
        return true;
    }

    protected synchronized void buildgoogleapiclaint() {
        mGooglapiclent = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGooglapiclent.connect();
    }


    private void creatlocationrequest() {

        mlocationrequest = new LocationRequest();
        mlocationrequest.setInterval(UPDATE_INTERVAL);
        mlocationrequest.setFastestInterval(FASTEST_INTERVAL);
        mlocationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationrequest.setSmallestDisplacement(DISPLAYCEMENT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LOCATION_PER_REQUEST: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    if (Checkplayservic()) {

                        buildgoogleapiclaint();
                        creatlocationrequest();

                        try {
                            displaylocation();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
                break;
            }
        }
    }
    private void GetLocation(double MyLat, double MyLong) throws IOException {
        try {

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(MyLat, MyLong, 1);
            String cityName = addresses.get(0).getAddressLine(0);
            String stateName = addresses.get(0).getAddressLine(1);
            String countryName = addresses.get(0).getAddressLine(2);
            nameAdrees = (countryName + cityName + stateName);
            Log.v("location", "couldn't get Location " + nameAdrees);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}