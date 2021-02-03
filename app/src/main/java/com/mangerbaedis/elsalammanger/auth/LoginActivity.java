package com.mangerbaedis.elsalammanger.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mangerbaedis.elsalammanger.Coomen;
import com.mangerbaedis.elsalammanger.R;
import com.mangerbaedis.elsalammanger.databinding.ActivityLoginBinding;
import com.mangerbaedis.elsalammanger.home.HomeActivity;


public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    Coomen coomen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        coomen = new Coomen(this);


        binding.buttonSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

    }

    private void login() {
        if (isvalue()) {
            if (binding.editTextemail.getText().toString().equalsIgnoreCase("Hussain@manger.com")
                    && binding.editPassword.getText().toString().equalsIgnoreCase("H20202021")) {
                if (binding.remmber.isChecked()) {
                    coomen.saveData("okLogin");
                }

                startActivity(new Intent(this, HomeActivity.class));
                finish();


            }


//            else if (binding.editTextemail.getText().toString().equalsIgnoreCase("adminf@manger.com")
//                    && binding.editPassword.getText().toString().equalsIgnoreCase("H20202021")){
//                if (binding.remmber.isChecked()) {
//                    coomen.saveData("okLogin");
//                }
//                startActivity(new Intent(this, PageFagalaActivity.class));
//                finish();
//
//
           }else {
                Toast.makeText(this, "االبيانات غير صحيحة", Toast.LENGTH_SHORT).show();
            }


        }


    boolean isvalue() {
        if (binding.editTextemail.getText().toString().isEmpty()) {
            binding.editTextemail.setError("من فضلك أدخل الايميل ");
            return false;
        } else if (binding.editPassword.getText().toString().isEmpty()) {

            binding.editPassword.setError("من فضلك أدخل الرقم السري ");
            return false;

        } else {
            return true;
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (coomen.loadData().get(Coomen.KEY_USER_FNAME).equalsIgnoreCase("okLogin")){
            startActivity(new Intent(this, HomeActivity.class));
            finish();


        }
    }
}