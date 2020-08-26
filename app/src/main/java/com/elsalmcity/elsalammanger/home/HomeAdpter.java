package com.elsalmcity.elsalammanger.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elsalmcity.elsalammanger.Coomen;
import com.elsalmcity.elsalammanger.pojo.Catogray;
import com.elsalmcity.elsalammanger.R;
import com.elsalmcity.elsalammanger.produts.ProductsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HomeAdpter extends RecyclerView.Adapter<HomeAdpter.ViewHolderVidio> {
    private final OnItemClickListener listener;




    public interface OnItemClickListener {
        void onItemClick(String key);
    }
    private ArrayList<Catogray> list;
    private Context mcontext;
public static String key;
    public HomeAdpter(OnItemClickListener listener, ArrayList<Catogray> list, Context mcontext) {
        this.listener = listener;
        this.list = list;
        this.mcontext = mcontext;
    }


    @NonNull
    @Override
    public ViewHolderVidio onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catogray, parent, false);
        ViewHolderVidio chatViewHolder = new ViewHolderVidio(view);
        return chatViewHolder;

    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolderVidio holder, final int position) {
        holder.textView.setText(list.get(position).getName());
        Glide.with(mcontext)
                .load(list.get(position)
                        .getImage())
                .into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentMothed(ProductsActivity.class,position+"");
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mcontext)
                        .setTitle(list.get(position).getName())
                        .setMessage("هل تريد حذف " + list.get(position).getName()+ "?")
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                detlete(list.get(position).getName(),holder.getAdapterPosition());
                                detleteproduct(position+"");
                                Toast.makeText(mcontext, "تم الحذف", Toast.LENGTH_SHORT).show();


                            }
                        }).setNegativeButton("لا", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.create().show();


            }
        });

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        mcontext);

                // set title
                alertDialogBuilder.setTitle("التعديل");

                // set dialog message
                alertDialogBuilder
                        .setMessage("هل تريد تعديل هذا المنتج ؟")
                        .setCancelable(false)
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getKey(list.get(position).getName());
                                listener.onItemClick(key);
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

    private void getKey(String name){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference updatecatogry  = database.getReference().child("category");
        updatecatogry.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                          key = childSnapshot.getKey();
                            Coomen.print(key,"key");


                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



    }

    private void intentMothed(Class a, String value) {

        Intent intent = new Intent(mcontext, a);
        intent.putExtra("menuId", value);

        mcontext.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolderVidio extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        ImageView delete,update;

        public ViewHolderVidio(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_item_title);
            imageView = itemView.findViewById(R.id.imag_item);
            delete = itemView.findViewById(R.id.btn_delete);
            update = itemView.findViewById(R.id.btn_update);
        }


    }

    private void detlete(String name, final int oasstion){
        //firebase

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refProduct = database.getReference().child("category");
        Query applesQuery = refProduct.orderByChild("name").equalTo(name);
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
        list.remove(oasstion);
        notifyItemRemoved(oasstion);
        notifyItemRangeChanged(oasstion,list.size());

    }
    private void detleteproduct(String name){
        //firebase

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refProduct = database.getReference().child("prodects");
        Query applesQuery = refProduct.orderByChild("id").equalTo(name);
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

    }
}

