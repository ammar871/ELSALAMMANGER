package com.mangerbaedis.elsalammanger.notifecation;

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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.mangerbaedis.elsalammanger.R;

import com.mangerbaedis.elsalammanger.databinding.ActivitySendNotifyBinding;

import com.mangerbaedis.elsalammanger.home.HomeActivity;
import com.mangerbaedis.elsalammanger.pojo.Product;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

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

public class SendNotifyActivity extends AppCompatActivity implements OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
        , LocationListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_send_notify);
        mRequestQue = Volley.newRequestQueue(this);
        mservies = Claint.getClient().create(ApiServes.class);

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
                        Toast.makeText(SendNotifyActivity.this, "من فضلك قم بتشغيل الموقع ", Toast.LENGTH_SHORT).show();
                    }



                }

            }
        });

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

//        } else if (binding.edpage.getText().toString().isEmpty()
//                && binding.edpage.getText().toString().equals("")) {
//            binding.edpage.setError("اضف صفحة الرسمية");
//            return false;
//
        } else if (binding.eddesc.getText().toString().isEmpty()
                && binding.eddesc.getText().toString().equals("")) {
            binding.eddesc.setError("اكتب التفاصيل");
            return false;

        } else if (binding.edLocationDelar.getText().toString().isEmpty()
                && binding.edLocationDelar.getText().toString().equals("")) {
            binding.edLocationDelar.setError("اكتب العنوان ");
            return false;

//        }
//
        }       else if (saveuri == null) {
            Toast.makeText(this, "اختار الصورة", Toast.LENGTH_SHORT).show();
            return false;

        } else {
            return true;
        }
    }

    private void SendRetrofitNotifection(final String title, final String body, final String uri) {


        Data notifacation = new Data(title, body, uri,"com.bardisammar.elsalamcity.notifecation_TARGET_NOTIFICATION");

        Sender sender = new Sender("/topics/bardis", notifacation);
        mservies.sendNotifectoin(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code()==200){
                    Toast.makeText(SendNotifyActivity.this, "تم الارسال ", Toast.LENGTH_SHORT).show();
                    catogry.push()
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
                                            , binding.eddesc.getText().toString().trim()
                                            ,binding.edLocationDelar.getText().toString().trim()
                                            , binding.edshortDesc.getText().toString(),
                                            "no"
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            displaylocation();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startlocationupdates();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

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
