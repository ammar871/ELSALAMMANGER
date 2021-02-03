package com.mangerbaedis.elsalammanger.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mangerbaedis.elsalammanger.Coomen;
import com.mangerbaedis.elsalammanger.addProducts.Pro_Cato_Activity;
import com.mangerbaedis.elsalammanger.addProducts.bunners.Adds_BunnersActivity;
import com.mangerbaedis.elsalammanger.news.NewsActivity;
import com.mangerbaedis.elsalammanger.notifecation.NotificationActivity;
import com.mangerbaedis.elsalammanger.notifecation.SendNotifyActivity;
import com.mangerbaedis.elsalammanger.ordes.OrdersActivity;
import com.mangerbaedis.elsalammanger.pojo.Catogray;
import com.mangerbaedis.elsalammanger.R;
import com.mangerbaedis.elsalammanger.databinding.ActivityHomeBinding;
import com.mangerbaedis.elsalammanger.produts.ProductsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.UUID;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HomeActivity extends AppCompatActivity implements HomeAdpter.OnItemClickListener, View.OnClickListener {
    ActivityHomeBinding binding;
    HomeAdpter adpter;
    EditText name;
    ImageView imag_selecte;
    Button btnupload;
    private DatabaseReference catogry;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    ProgressDialog pd;
    Uri saveuri;
    ArrayList<Catogray> list;
    Catogray newcatogrt;
    private final int PICK_IMAGE = 71;
    public static String key;
    Coomen coomen;
    TextView tv_home, tv_addProduct, tv_notifecation, tv_news, tv_logout, tv_page_pro,tv_adds,tv_orders,tv_addsd;
    FirebaseRecyclerAdapter<Catogray, MenuViewHolder> firbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        FirebaseMessaging.getInstance().subscribeToTopic("mangeBardis").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

        list = new ArrayList<>();

        coomen = new Coomen(this);
        //firebase
        database = FirebaseDatabase.getInstance();
        catogry = database.getReference().child("category");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        binding.toggles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.draw.openDrawer(Gravity.RIGHT);
            }
        });
        Load();

        binding.faphom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Showdialog();
            }
        });

        iniItViewMenu();
        createLocationRequest();


    }

    private void Load() {
        if (Coomen.isNetworkOnline(this)) {
            try {
                binding.rcList.setLayoutManager(new GridLayoutManager(this, 2));
                binding.rcList.setHasFixedSize(true);
                Query query = FirebaseDatabase.getInstance().getReference().child("category");


                FirebaseRecyclerOptions<Catogray> options = new FirebaseRecyclerOptions.Builder<Catogray>()
                        .setQuery(query, Catogray.class)
                        .build();

                firbase = new FirebaseRecyclerAdapter<Catogray, MenuViewHolder>(options) {


                    @NonNull
                    @Override
                    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_catogray, parent, false);

                        return new MenuViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final MenuViewHolder menuViewHolder, final int i, @NonNull final Catogray categoryb) {

                        final String key = firbase.getRef(i).getKey();
                        menuViewHolder.textView.setText(categoryb.getName());
                        Glide.with(HomeActivity.this)
                                .load(categoryb.getImage())
                                .into(menuViewHolder.imageView);
                        menuViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                intentMothed(ProductsActivity.class, key, categoryb.getName());
                            }
                        });
                        menuViewHolder.delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this)
                                        .setTitle(categoryb.getName())
                                        .setMessage("هل تريد حذف " + categoryb.getName() + "?")
                                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                detlete(categoryb.getName(), menuViewHolder.getAdapterPosition());
                                                detleteproduct(key);
                                                notifyItemRemoved(menuViewHolder.getAdapterPosition());
                                                notifyItemRangeChanged(menuViewHolder.getAdapterPosition(), list.size());
                                                Toast.makeText(HomeActivity.this, "تم الحذف", Toast.LENGTH_SHORT).show();


                                            }
                                        }).setNegativeButton("لا", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                builder.create().show();


                            }
                        });

                        menuViewHolder.update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        HomeActivity.this);

                                // set title
                                alertDialogBuilder.setTitle("التعديل");

                                // set dialog message
                                alertDialogBuilder
                                        .setMessage("هل تريد تعديل هذا المنتج ؟")
                                        .setCancelable(false)
                                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                ShowUpdatedialog(key);
//                                        getKey(list.get(i).getName());
                                                notifyDataSetChanged();
                                            }
                                        })
                                        .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                dialog.cancel();
                                            }
                                        });

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // show it
                                alertDialog.show();


                            }
                        });

                    }
                };
                binding.rcList.setAdapter(firbase);
                firbase.startListening();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "انقطع الاتصال بالانترنت ", Toast.LENGTH_SHORT).show();
        }


    }

    private void iniItViewMenu() {
        tv_home = findViewById(R.id.homee);
        tv_logout = findViewById(R.id.id_log_out);
        tv_page_pro = findViewById(R.id.page_products);
        tv_adds = findViewById(R.id.add_adds);


        tv_orders = findViewById(R.id.page_orders);
        tv_news = findViewById(R.id.news);

        tv_notifecation = findViewById(R.id.notify);

        tv_home.setOnClickListener(this);
        tv_page_pro.setOnClickListener(this);
        tv_news.setOnClickListener(this);
        tv_notifecation.setOnClickListener(this);
        tv_logout.setOnClickListener(this);

        tv_adds.setOnClickListener(this);
        tv_orders.setOnClickListener(this);


    }


    private void detlete(String name, final int oasstion) {
        //firebase
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference refProduct = database.getReference().child("category");
            Query applesQuery = refProduct.orderByChild("name").equalTo(name);
            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void detleteproduct(String name) {
        //firebase
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference refProduct = database.getReference().child("prodects");
            Query applesQuery = refProduct.orderByChild("id").equalTo(name);
            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void intentMothed(Class a, String value, String name) {

        Intent intent = new Intent(this, a);
        intent.putExtra("menuId", value);
        intent.putExtra("name", name);
        this.startActivity(intent);
    }


    private void ChooseImage() {
        Intent glary = new Intent(Intent.ACTION_GET_CONTENT);
        glary.setType("image/*");
        glary.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(glary, "Selecet pictur"), PICK_IMAGE);


    }

    private void Showdialog() {

        try {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.add_new_menu, null);
            dialogBuilder.setView(dialogView);

            name = dialogView.findViewById(R.id.edname);

            imag_selecte = dialogView.findViewById(R.id.imag_add);
            btnupload = dialogView.findViewById(R.id.btnupload);
            imag_selecte.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChooseImage();
                }
            });
            btnupload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    UploadImage();


                }
            });


            dialogBuilder.setTitle("Add new Category");
            dialogBuilder.setMessage("please fill full information");
            dialogBuilder.setView(dialogView);
            dialogBuilder.setIcon(R.drawable.ic_baseline_playlist_add_24);
            dialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                    if (newcatogrt != null) {

                        catogry.child(name.getText().toString()).setValue(newcatogrt);
                    }

                }
            });
            dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });

            AlertDialog b = dialogBuilder.create();
            b.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void UploadImage() {
        try {
            if (saveuri != null) {
                pd = new ProgressDialog(HomeActivity.this);
                pd.setMessage("Upload...");
                pd.show();
                String ImageName = UUID.randomUUID().toString();

                final StorageReference imagefolder = storageReference.child("images/" + ImageName);
                imagefolder.putFile(saveuri).
                        addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                pd.dismiss();
                                Toast.makeText(HomeActivity.this, "upload...", Toast.LENGTH_SHORT).show();
                                imagefolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        newcatogrt = new Catogray(uri.toString(), name.getText().toString());
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        pd.setMessage("upload" + progress + "");
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data
                != null && data.getData() != null) {
            saveuri = data.getData();
            imag_selecte.setImageURI(saveuri);
        }
    }


    @Override
    public void onItemClick(String key) {

        ShowUpdatedialog(key);
    }

    private void ShowUpdatedialog(final String key) {
        try {

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.add_new_menu, null);
            dialogBuilder.setView(dialogView);

            name = dialogView.findViewById(R.id.edname);

            imag_selecte = dialogView.findViewById(R.id.imag_add);
            btnupload = dialogView.findViewById(R.id.btnupload);
            imag_selecte.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChooseImage();
                }
            });
            btnupload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    UploadImage();


                }
            });


            dialogBuilder.setTitle("Add new Category");
            dialogBuilder.setMessage("please fill full information");
            dialogBuilder.setView(dialogView);
            dialogBuilder.setIcon(R.drawable.ic_baseline_playlist_add_24);
            dialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                    if (newcatogrt != null) {
                        if (key != null) {
                            catogry.child(key).setValue(newcatogrt);
                        } else {
                            Toast.makeText(HomeActivity.this, "فشلت العملية ", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
            });
            dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });

            AlertDialog b = dialogBuilder.create();
            b.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homee:
                startActivity(new Intent(this, SendNotifyActivity.class));
                finish();

                break;

            case R.id.news:
                startActivity(new Intent(this, NewsActivity.class));
                finish();
                break;
            case R.id.notify:
                startActivity(new Intent(this, NotificationActivity.class));
                finish();
                break;
            case R.id.id_log_out:
                coomen.logOut();
                break;

            case R.id.page_products:
                startActivity(new Intent(this, Pro_Cato_Activity.class));
                finish();
                break;
            case R.id.add_adds:
                startActivity(new Intent(this, Adds_BunnersActivity.class));
                finish();
                break;
            case R.id.page_orders:
                startActivity(new Intent(this, OrdersActivity.class));
                finish();
                break;

            default:
                break;

        }
    }

    protected void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);


// ...

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {

                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(HomeActivity.this,
                                1);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }
}