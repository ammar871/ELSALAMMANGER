package com.mangerbaedis.elsalammanger.addProducts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mangerbaedis.elsalammanger.Coomen;
import com.mangerbaedis.elsalammanger.R;
import com.mangerbaedis.elsalammanger.databinding.ActivityProCatoBinding;
import com.mangerbaedis.elsalammanger.home.HomeActivity;
import com.mangerbaedis.elsalammanger.home.MenuViewHolder;
import com.mangerbaedis.elsalammanger.pojo.Catogray;
import com.mangerbaedis.elsalammanger.pojo.Pro_Of_Product;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import java.util.ArrayList;
import java.util.UUID;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Pro_Cato_Activity extends AppCompatActivity {

    ActivityProCatoBinding binding;
    ArrayList<Pro_Of_Product> list;
    AdpterPro_cato adpterProduct;

    EditText name;
    ImageView imag_selecte;
    Button btnupload;
    private DatabaseReference catogry;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    ProgressDialog pd;
    Uri saveuri;

    Catogray newcatogrt;
    private final int PICK_IMAGE = 71;
    public static String key;
    Coomen coomen;
    FirebaseRecyclerAdapter<Catogray, MenuViewHolder> firbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_pro__cato_);
        list = new ArrayList<>();
        binding.listProduct.setLayoutManager(new GridLayoutManager(this, 2));
        binding.listProduct.setHasFixedSize(true);

        list = new ArrayList<>();

        coomen = new Coomen(this);
        //firebase
        database = FirebaseDatabase.getInstance();
        catogry = database.getReference().child("أقسام المنتجات");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        binding.faphom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Showdialog();
            }
        });

        Load();
//        loadDataIntentnt();

    }
    private void Load() {
        if (Coomen.isNetworkOnline(this)) {
            try {
                binding.listProduct.setLayoutManager(new GridLayoutManager(this, 2));
                binding.listProduct.setHasFixedSize(true);
                Query query = FirebaseDatabase.getInstance().getReference().child("أقسام المنتجات");


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
                        Glide.with(Pro_Cato_Activity.this)
                                .load(categoryb.getImage())
                                .into(menuViewHolder.imageView);
                        menuViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                intentMothed(OffersActivity.class, key, categoryb.getName());
                            }
                        });
                        menuViewHolder.delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Pro_Cato_Activity.this)
                                        .setTitle(categoryb.getName())
                                        .setMessage("هل تريد حذف " + categoryb.getName() + "?")
                                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                detlete(categoryb.getName());
                                                detleteproduct(key);
                                                notifyItemRemoved(menuViewHolder.getAdapterPosition());
                                                notifyItemRangeChanged(menuViewHolder.getAdapterPosition(), list.size());
                                                Toast.makeText(Pro_Cato_Activity.this, "تم الحذف", Toast.LENGTH_SHORT).show();


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
                                        Pro_Cato_Activity.this);

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
                binding.listProduct.setAdapter(firbase);
                firbase.startListening();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "انقطع الاتصال بالانترنت ", Toast.LENGTH_SHORT).show();
        }


    }
//    private void loadDataIntentnt() {
//        if (Coomen.isNetworkOnline(this)) {
//            try {
//                FirebaseDatabase.getInstance().getReference().child("منتجات").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                            Log.d("Tagstorag", "onSuccess: " + dataSnapshot1.child("name").getValue());
//                            Pro_Of_Product blog = dataSnapshot1.getValue(Pro_Of_Product.class);
//
//
//                            list.add(blog);
//
//
//                        }
//                        adpterProduct = new AdpterPro_cato(list, Pro_Cato_Activity.this);
//                        binding.listProduct.setAdapter(adpterProduct);
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        } else {
//            Toast.makeText(this, "انقطع الاتصال بالانترنت ", Toast.LENGTH_SHORT).show();
//        }
//
//
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Pro_Cato_Activity.this, HomeActivity.class));
        finish();
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
                            Toast.makeText(Pro_Cato_Activity.this, "فشلت العملية ", Toast.LENGTH_SHORT).show();
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
    private void detlete(String name) {
        try {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference refProduct = database.getReference().child("أقسام المنتجات");
            Query applesQuery = refProduct.orderByChild("name").equalTo(name);
            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                     String mImageUr= appleSnapshot.child("image").getValue().toString();

                        StorageReference photoRef = storage.getReferenceFromUrl(mImageUr);
                        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // File deleted successfully
                                Log.d(TAG, "onSuccess: deleted file");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Uh-oh, an error occurred!
                                Log.d(TAG, "onFailure: did not delete file");
                            }
                        });

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
            DatabaseReference refProduct = database.getReference().child("منتجات");
            Query applesQuery = refProduct.orderByChild("idcatogray").equalTo(name);
            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                        String mImageUr= appleSnapshot.child("image").getValue().toString();

//                        StorageReference photoRef = storage.getReferenceFromUrl(mImageUr);
//                        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                // File deleted successfully
//                                Log.d(TAG, "onSuccess: deleted file");
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception exception) {
//                                // Uh-oh, an error occurred!
//                                Log.d(TAG, "onFailure: did not delete file");
//                            }
//                        });
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

                        catogry.push().setValue(newcatogrt);
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
                pd = new ProgressDialog(Pro_Cato_Activity.this);
                pd.setMessage("Upload...");
                pd.show();
                String ImageName = UUID.randomUUID().toString();

                final StorageReference imagefolder = storageReference.child("images/" + ImageName);
                imagefolder.putFile(saveuri).
                        addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                pd.dismiss();
                                Toast.makeText(Pro_Cato_Activity.this, "upload...", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Pro_Cato_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

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

}