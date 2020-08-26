package com.elsalmcity.elsalammanger.notifecation;


import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


    }
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        String tokenRefresh= FirebaseInstanceId.getInstance().getToken();

        UpdateToken(tokenRefresh);

    }

    private void UpdateToken(final String tokenRefresh) {

        FirebaseDatabase db= FirebaseDatabase.getInstance();
        DatabaseReference ref= db.getReference("Tokens");
        Token data=new Token(tokenRefresh,false);
        ref.child(tokenRefresh).setValue(data);

    }

}
