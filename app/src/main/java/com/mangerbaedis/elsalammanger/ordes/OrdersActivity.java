package com.mangerbaedis.elsalammanger.ordes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mangerbaedis.elsalammanger.Coomen;
import com.mangerbaedis.elsalammanger.R;
import com.mangerbaedis.elsalammanger.databinding.ActivityOrdersBinding;
import com.mangerbaedis.elsalammanger.home.HomeActivity;
import com.mangerbaedis.elsalammanger.pojo.Request;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class OrdersActivity extends AppCompatActivity {
    private DatabaseReference request;
    RecyclerView.LayoutManager layoutManager;
    private FirebaseDatabase database;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> firbase;
    ActivityOrdersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_orders);
        database = FirebaseDatabase.getInstance();
        request = database.getReference("Orders");

        //recycler View
        // recler_view = findViewById(R.id.listorder);
        layoutManager = new LinearLayoutManager(this);
        binding.listorder.setHasFixedSize(true);
        binding.listorder.setLayoutManager(layoutManager);
        LoadOrder();
    }

    private void LoadOrder() {

        Query query = FirebaseDatabase.getInstance().getReference().child("Orders");
        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(query, Request.class)
                .build();


        firbase = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {


            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(OrdersActivity.this).inflate(R.layout.order_layout, parent, false);
                return new OrderViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final OrderViewHolder menuViewHolder, final int i, @NonNull final Request category) {
                menuViewHolder.txtorderid.setText(firbase.getRef(i).getKey());
                // menuViewHolder.txtorderstatus.setText(Common.contercodstatus(category.getStatues()));
                menuViewHolder.txtordesphon.setText("رقم الهاتف : " + category.getPhone());
                menuViewHolder.txtorderaddress.setText("العنوان : " + category.getAddresse());
                menuViewHolder.txtDate.setText("وقت الطلب  : " + Coomen.getDate(Long.parseLong(firbase.getRef(i).getKey())));


                menuViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent oorderdetails = new Intent(OrdersActivity.this, DetailsOrderActivity.class);

                        oorderdetails.putExtra("orderid", firbase.getRef(i).getKey());
                        oorderdetails.putExtra("request", category);
                        startActivity(oorderdetails);
                    }
                });
                menuViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(OrdersActivity.this)
                                .setTitle(category.getName())
                                .setMessage("هل تريد حذف " + category.getName() + "?")
                                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            detlete(menuViewHolder.getAdapterPosition());
                                            Toast.makeText(OrdersActivity.this, "تم الحذف", Toast.LENGTH_SHORT).show();

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


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
        };
        binding.listorder.setAdapter(firbase);
        //  firbase.notifyDataSetChanged();
        firbase.startListening();


    }

    private void detlete(int name) {
        Query applesQuery = firbase.getRef(name);
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

        ;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(OrdersActivity.this, HomeActivity.class));
        finish();
    }
}