package com.mangerbaedis.elsalammanger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.mangerbaedis.elsalammanger.auth.LoginActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class Coomen {
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;
    Context mContext;
    private static final String FILE_NAME = "coursatApp";
    public static final String KEY_USER_FNAME = "n";
    public static void print(String name,String tag){
        Log.d(tag, "onSuccess: " + name);
    }
    public static boolean isNetworkOnline(Context context) {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;

    }

    public Coomen(Context mContext) {
        this.mContext = mContext;
        mSharedPreferences = mContext.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }
    public HashMap<String, String> loadData() {
        HashMap<String, String> userData = new HashMap<>();

        userData.put(KEY_USER_FNAME, mSharedPreferences.getString(KEY_USER_FNAME, ""));
        return userData;
    }

    public void saveData(String muser) {

        mEditor.putString(KEY_USER_FNAME, muser);


        mEditor.commit();
    }
    public void logOut() {
        mEditor.clear();
        mEditor.commit();
        Intent mIntent = new Intent(mContext, LoginActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(mIntent);
        Toast.makeText(mContext, "ok", Toast.LENGTH_SHORT).show();
    }

    public static String getDate(long time){

        Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date=new StringBuilder(DateFormat.format("dd-MM-yyyy HH:mm",
                calendar
        ).toString());
        return date.toString();
    }
}
