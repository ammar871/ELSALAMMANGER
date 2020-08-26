package com.elsalmcity.elsalammanger.news;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.elsalmcity.elsalammanger.R;
import com.elsalmcity.elsalammanger.notifecation.AdpterNotifecation;
import com.elsalmcity.elsalammanger.notifecation.Data;
import com.elsalmcity.elsalammanger.pojo.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class AdpterNews  extends RecyclerView.Adapter<AdpterNews.ViewHolderVidio> {

    private ArrayList<Data> list;
    private Context mcontext;


    public AdpterNews(ArrayList<Data> list, Context mcontext) {

        this.list = list;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public ViewHolderVidio onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        ViewHolderVidio chatViewHolder = new ViewHolderVidio(view);
        return chatViewHolder;

    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolderVidio holder, final int position) {
        holder.name.setText(list.get(position).getTitle());
        holder.desc.setText(list.get(position).getBody());

        Glide.with(mcontext)
                .load(list.get(position).getImage())
                .into(holder.imageView);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mcontext)
                        .setTitle(list.get(position).getTitle())
                        .setMessage("هل تريد حذف " + list.get(position).getTitle()+ "?")
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                detlete(list.get(position).getTitle(),holder.getAdapterPosition());
                                Toast.makeText(mcontext, "تم الحذف", Toast.LENGTH_SHORT).show();


                            }
                        }).setNegativeButton("لا", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.create().show();

                return true;
            }
        });


    }




    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolderVidio extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name, desc;
        ImageView delete,update;


        public ViewHolderVidio(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.tv_title);
            desc = itemView.findViewById(R.id.tv_body);

        }


    }
    private void detlete(String name, final int oasstion){
        //firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refProduct = database.getReference().child("News");
        Query applesQuery = refProduct.orderByChild("title").equalTo(name);
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


}
