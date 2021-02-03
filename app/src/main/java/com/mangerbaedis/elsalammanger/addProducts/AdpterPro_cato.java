package com.mangerbaedis.elsalammanger.addProducts;

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
import com.mangerbaedis.elsalammanger.R;
import com.mangerbaedis.elsalammanger.pojo.Pro_Of_Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import static androidx.constraintlayout.widget.Constraints.TAG;
public class AdpterPro_cato extends RecyclerView.Adapter<AdpterPro_cato.ViewHolderVidio> {


private ArrayList<Pro_Of_Product> list;
private Context mcontext;
public static String key;

public AdpterPro_cato(ArrayList<Pro_Of_Product> list, Context mcontext) {

        this.list = list;
        this.mcontext = mcontext;
        }


@NonNull
@Override
public ViewHolderVidio onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pro, parent, false);
        ViewHolderVidio chatViewHolder = new ViewHolderVidio(view);
        return chatViewHolder;

        }


@Override
public void onBindViewHolder(@NonNull final ViewHolderVidio holder, final int position) {
        holder.text_name.setText(list.get(position).getName());
        holder.tv_desc.setText(list.get(position).getDescShort());
        holder.tv_price.setText(list.get(position).getPrice());
        holder.tv_nameMarket.setText(list.get(position).getNameOfMarket());
        Glide.with(mcontext)
        .load(list.get(position)
        .getImage())
        .into(holder.imageView);

        holder.delete.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext)
        .setTitle(list.get(position).getName())
        .setMessage("هل تريد حذف " + list.get(position).getName() + "?")
        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int which) {

        detlete(list.get(position).getName(), holder.getAdapterPosition());

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
        update(position);
        detlete(list.get(position).getName(), holder.getAdapterPosition());

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






@Override
public int getItemCount() {
        return list.size();
        }

public class ViewHolderVidio extends RecyclerView.ViewHolder {
    TextView text_name, tv_desc, tv_price,tv_nameMarket;
    ImageView imageView;
    ImageView delete, update;

    public ViewHolderVidio(@NonNull View itemView) {
        super(itemView);

        tv_price = itemView.findViewById(R.id.item_pricep);
        tv_desc = itemView.findViewById(R.id.tv_descp);
        tv_nameMarket = itemView.findViewById(R.id.item_Nmarket);
        text_name = itemView.findViewById(R.id.tv_namep);
        imageView = itemView.findViewById(R.id.imageViewp);
        delete = itemView.findViewById(R.id.btn_deletep);
        update = itemView.findViewById(R.id.btn_updatep);
    }


}

    private void detlete(String name, final int oasstion) {
        //firebase

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refProduct = database.getReference().child("منتجات");
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
        list.remove(oasstion);
        notifyItemRemoved(oasstion);
        notifyItemRangeChanged(oasstion, list.size());

    }

    private void update(int passtion) {
        Intent intent = new Intent(mcontext, AddPro_cato_Activity.class);
        intent.putExtra("updatePro", list.get(passtion));
        intent.putExtra("id", list.get(passtion).getIdcatogray());

        mcontext.startActivity(intent);


    }


}