package com.example.lightflashdemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import com.ys.serialport.LightController;


public class MyService extends Service {

    NotificationManager notificationManager;

    private static final String NOTIFICATION_ID = "channedId";
    private static final String NOTIFICATION_NAME = "channedId";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("jesse","onCreate");

        liveLigthModel();
        t.start();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //创建NotificationChannel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_ID, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(1,getNotification());
    }

    private Notification getNotification() {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("灯条")
                .setAutoCancel(true);
        //设置Notification的ChannelID,否则不能正常显示
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_ID);
        }
        Notification notification = builder.build();
        return notification;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("jesse","onStartCommand");

        ligthLiveLength = intent.getIntExtra("ligthLiveLength",2);
        if(ligthLiveLength == 2){
            intervalTime = ligthLiveLength * 1000 +2000;
        }else {
            intervalTime = ligthLiveLength * 1000 +500;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    boolean ligthLoop = true;
    int ligthLiveLength = 2;
    Thread t;
    int intervalTime;
    private void liveLigthModel(){
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (ligthLoop){
                    Log.d("lightflashdemo","ligthLiveLength :" +  ligthLiveLength);
                    if(ligthLoop) {
                        LightController.getInstance().liveMode(LightController.Led.RED, ligthLiveLength);
                        SystemClock.sleep(intervalTime);
                    }
                    if(ligthLoop) {
                        LightController.getInstance().liveMode(LightController.Led.GREEN, ligthLiveLength);
                        SystemClock.sleep(intervalTime);
                    }
                    if(ligthLoop) {
                        LightController.getInstance().liveMode(LightController.Led.BLUE, ligthLiveLength);
                        SystemClock.sleep(intervalTime);
                    }
                }
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ligthLoop = false;
        if(t != null)t.interrupt();
    }
}
