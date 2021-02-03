package com.mangerbaedis.elsalammanger.notifecation;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;


import com.mangerbaedis.elsalammanger.Coomen;
import com.mangerbaedis.elsalammanger.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private NotificationManager mManager;
    public static final String EDMIT_ID = "com.elsalmcity.elsalamcity";
    public static final String CHANNEL_NAME = "ANDROID CHANNEL";

    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String click_action = remoteMessage.getData().get("click_action");
        final String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        Coomen.print(body,"keybody");


        if (remoteMessage.getData().size() > 0) {





            Intent intent = new Intent(click_action);
            intent.putExtra("title", body);

            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    1, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            final NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getApplicationContext(), CHANNEL_NAME)
                            .setSound(uri)

                            .setContentTitle(title)
                            .setContentText(body)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setSmallIcon(R.drawable.logoonc);

            } else {
                builder.setSmallIcon(R.drawable.logoonc);
            }

            NotificationManager notificationManager =
                    (NotificationManager)
                            getSystemService(Context.NOTIFICATION_SERVICE);
            int id = (int) System.currentTimeMillis();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_NAME,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(false);
                notificationChannel.enableVibration(true);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

                getManger().createNotificationChannel(notificationChannel);
            /*
                NotificationChannel channel = new NotificationChannel(NotifecationHelper.EDMIT_ID, NotifecationHelper.CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH);
                channel.enableVibration(true);
                channel.enableLights(true);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

                notificationManager.createNotificationChannel(channel);*/
            }
            notificationManager.notify(id, builder.build());


        }


    }

    public NotificationManager getManger() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

}
