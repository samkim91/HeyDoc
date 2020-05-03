package com.project1.heydoc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;



public class MyFirebaseMessagingService extends FirebaseMessagingService {


    //구글의 토큰으 받아오는 메소드. 앱이 설치된 기기에 대한 고유값으로 푸시를 보낼 때 사용.
    @Override
    public void onNewToken(String s){
        super.onNewToken(s);
        Log.i("태그", "onNewToken : "+s);

        //이 기기가 토큰을 받았을 때 쉐어드프리퍼런스에 잠시 저장해놓는다. 후에 이 저장된 토큰으로 유저가 로그인할 때마다 부여해 줄 것이다.
        //이는 기기를 쓰는 유저는 어쨌든 유일할 것이기 때문에 문제가 없을 것이다.

        SharedPreferences sharedPreferences = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("thetoken", s);
        editor.commit();
        Log.i("태그", "SharedPreferences 에 저장 : "+s);

    }

    //메시지를 받았을 때 이 메시지에 대해 구현하는 부분.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i("태그", "onMessageReceived");
        Log.i("태그", remoteMessage.getTtl()+"/"+remoteMessage.getSentTime());

        sendNotification(remoteMessage);

//        if(remoteMessage!=null && remoteMessage.getData().size()>0){
//            Log.i("태그", "remoteMessage가 null이 아니면 들어옴.");
//
//        }
    }

    //remoteMessage 안에 getData와 getNotification이 있음.
    private void sendNotification(RemoteMessage remoteMessage) {
        Log.i("태그", "sendNotification");

//        title이랑 message는 작동안함... 왜인지 모르겠음
//        String title = remoteMessage.getData().get("title");
//        String message = remoteMessage.getData().get("message");
//        Log.i("태그", "title : "+title+"/"+"message : "+message);

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        Log.i("태그", "title : "+title+"/"+"body : "+body);


        //오레오 버전 이상부터 notification channel이 없으면 푸시가 발생하지 않아서 막아주는 조건문
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.i("태그", "Notification 만드는 조건문, 오레오 버전 위");
            String channel = "채널";
            String channel_name = "채널명";

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(channel, channel_name, NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("채널에 대한 설명");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(false);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
            notificationManager.createNotificationChannel(notificationChannel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel);
            builder.setSmallIcon(R.drawable.ic_launcher_background);
            builder.setContentText(title);
            builder.setContentText(body);
            builder.setChannelId(channel);
            builder.setAutoCancel(true);
            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            NotificationManager notificationManager1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager1.notify(9999, builder.build());


        }else {
            Log.i("태그", "Notification 만드는 조건문, 오레오 버전 아래");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "");
            builder.setSmallIcon(R.drawable.ic_launcher_background);
            builder.setContentTitle(title);
            builder.setContentText(body);
            builder.setAutoCancel(true);
            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(9999, builder.build());
        }

    }
}
