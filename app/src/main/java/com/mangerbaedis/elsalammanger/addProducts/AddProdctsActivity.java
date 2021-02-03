package com.mangerbaedis.elsalammanger.addProducts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
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

import com.mangerbaedis.elsalammanger.R;
import com.mangerbaedis.elsalammanger.databinding.ActivityAddProdctsBinding;
import com.mangerbaedis.elsalammanger.home.HomeActivity;
import com.mangerbaedis.elsalammanger.pojo.Product;
import com.mangerbaedis.elsalammanger.produts.AdpterImages;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddProdctsActivity extends AppCompatActivity implements OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
        , LocationListener {
    private static final int PICK_IMAGE = 1;
    ActivityAddProdctsBinding binding;
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
   static boolean auploadimag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_prodcts);

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
        pd = new ProgressDialog(AddProdctsActivity.this);
            if (getIntent() != null) {
                getId = getIntent().getStringExtra("id");


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
                    if (imagesList.isEmpty()){
                        Toast.makeText(AddProdctsActivity.this, "من فضلك أضف الصور", Toast.LENGTH_SHORT).show();
                    }else {
                        Upload();
                    }

                }
            });

            binding.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isValue()){
                        if (auploadimag){
                            SendLink();
                            auploadimag=false;
                        }
                        else
                            Toast.makeText(AddProdctsActivity.this, "حمل الصور ", Toast.LENGTH_SHORT).show();
                    }

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
                        binding.edaddress.setText(nameAdrees);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(AddProdctsActivity.this, "من فضلك قم بتشغيل الموقع ", Toast.LENGTH_SHORT).show();
                    }



                }

            }
        });



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
            adpterImages=new AdpterImages(imagesList,this);
            binding.imageAddList.setAdapter(adpterImages);
            binding.tvCount.setText("You Have Selected  " + imagesList.size() + "images");

        } else {
            Toast.makeText(this, "Please Select Multiple Image ", Toast.LENGTH_SHORT).show();
        }
    }

    private void Upload() {
        auploadimag=true;
try {
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
                Toast.makeText(AddProdctsActivity.this, "upload...", Toast.LENGTH_SHORT).show();
                auploadimag=true;
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
} catch (Exception e) {
    e.printStackTrace();
}



    }


    private void SendLink() {
        try {
            pd.show();
            Product product = new Product(getId, binding.ednameProdcut.getText().toString()
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

                    binding.ednameProdcut.setText("");
                    binding.edaddress.setText("");
                    binding.edaddress.setText("");
                    binding.edshortDesc.setText("");
                    binding.edPhonenumber.setText("");
                    binding.edpage.setText("");
                    Toast.makeText(AddProdctsActivity.this, "تمت الاضافة ", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddProdctsActivity.this,HomeActivity.class));
                    finish();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }



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

        } else if (imagesList.isEmpty()) {
            Toast.makeText(this, "اختار الصورة", Toast.LENGTH_SHORT).show();
            return false;

        } else {
            return true;
        }
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


