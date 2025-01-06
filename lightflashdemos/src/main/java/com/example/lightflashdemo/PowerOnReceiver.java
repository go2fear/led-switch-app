package com.example.lightflashdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.ys.serialport.LightController;

public class PowerOnReceiver extends BroadcastReceiver {

    public String TAG = "PowerOnReceiver";
    SharedPreferences mSharedPreferences;
    @Override
    public void onReceive(final Context context, Intent intent) {
        mSharedPreferences = context.getSharedPreferences("light_state",0);
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            Log.d(TAG,"555555");

            int model = mSharedPreferences.getInt("live_mode",-1);
            Log.d(TAG,"model :" + model);
            if(model != -1){
                String dev = mSharedPreferences.getString("curDevices","/dev/ttyS8");
                int rate = mSharedPreferences.getInt("curRate",9600);

                Log.d(TAG,"dev :" + dev);
                Log.d(TAG,"rate :" + rate);

                LightController.getInstance().openDevice(context, dev, rate);

                 if(2 == model){
                    intent = new Intent(context,MyService.class);
                    intent.putExtra("ligthLiveLength",2);
                }else if(4 == model){
                    intent = new Intent(context,MyService.class);
                    intent.putExtra("ligthLiveLength",4);
                }else if(6 == model){
                    intent = new Intent(context,MyService.class);
                    intent.putExtra("ligthLiveLength",6);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent);
                } else {
                    context.startService(intent);
                }
            }else {
                LightController.getInstance().resumeStausIfNeed(context);
            }
        }
    }
}

