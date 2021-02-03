package com.mangerbaedis.elsalammanger.notifecation;

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
import com.mangerbaedis.elsalammanger.R;
import com.mangerbaedis.elsalammanger.pojo.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class AdpterNotifecation extends RecyclerView.Adapter<AdpterNotifecation.ViewHolderVidio> {

    private ArrayList<Product> list;
    private Context mcontext;


    public AdpterNotifecation(ArrayList<Product> list, Context mcontext) {

        this.list = list;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public ViewHolderVidio onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifecation, parent, false);
        return new ViewHolderVidio(view);

    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolderVidio holder, final int position) {
        holder.name.setText(list.get(position).getName());
        holder.desc.setText(list.get(position).getDescshort());
        final Product product = list.get(position);
        Glide.with(mcontext)
                .load(list.get(position).getImage())
                .into(holder.imageView);
      holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
          @Override
          public boolean onLongClick(View view) {
              AlertDialog.Builder builder = new AlertDialog.Builder(mcontext)
                      .setTitle(product.getName())
                      .setMessage("هل تريد حذف " + product.getName()+ "?")
                      .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {

                              detlete(list.get(position).getName(),holder.getAdapterPosition());
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

    public static class ViewHolderVidio extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name, desc;
        ImageView delete,update;


        public ViewHolderVidio(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_notifec);
            name = itemView.findViewById(R.id.tv_name_noty);
            desc = itemView.findViewById(R.id.tv_desc_noty);

        }


    }
    private void detlete(String name, final int oasstion){
        //firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refProduct = database.getReference().child("Notifecations");
        Query applesQuery = refProduct.orderByChild("name").equalTo(name);
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {

                    appleSnapshot.getRef().removeValue();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
        list.remove(oasstion);
        notifyItemRemoved(oasstion);
        notifyItemRangeChanged(oasstion,list.size());

    }


}
