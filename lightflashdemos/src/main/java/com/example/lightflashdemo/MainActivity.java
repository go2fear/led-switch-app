package com.example.lightflashdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.ys.serialport.LightController;
import com.ys.serialport.SerialPort;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HandshakeCompletedEvent;

public class MainActivity extends Activity implements View.OnClickListener,
         SeekBar.OnSeekBarChangeListener {

    GridView mGridView;
    int[] ids = new int[]{R.mipmap.icon01,R.mipmap.icon02,R.mipmap.icon03,R.mipmap.icon04,R.mipmap.icon05,R.mipmap.icon06,
        R.mipmap.icon07,R.mipmap.icon08,R.mipmap.icon09 }; //,R.mipmap.icon10,R.mipmap.icon11,R.mipmap.icon12};
    private Button devNameBt, devRateBt,devBt,flashOpenBt,flashCloseBt,liveCloseBt,systemRecoveryBt,
        lightAllCloseBt,lightLiveOne,lightLiveTwo,lightLiveThree;
    private AlertDialog mDevicesDialog, mRateDialog;
    private String curDevice;
    private int curRate;
    int[] selectors = new int[]{0,0,0,0,0,0,0,0,0,0,0,0};
    AppCompatSeekBar mRed,mGreen,mBlue;
    TextView mTvRed,mTvGreen,mTvBlue;
    private List<LightController.Led> closeLeds;
    GvAdapter gvAdapter;

    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        devNameBt = findViewById(R.id.light_dev);
        devRateBt = findViewById(R.id.light_rate);
        devBt = findViewById(R.id.light_bt);
        flashCloseBt = findViewById(R.id.light_crazy_bt_closse);
        flashOpenBt = findViewById(R.id.light_crazy_bt_open);
        liveCloseBt = findViewById(R.id.light_live_bt);
        systemRecoveryBt = findViewById(R.id.system_recovery);
        lightAllCloseBt = findViewById(R.id.light_all_close);
        lightLiveOne = findViewById(R.id.light_live_one);
        lightLiveTwo = findViewById(R.id.light_live_two);
        lightLiveThree = findViewById(R.id.light_live_three);

        mSharedPreferences = getSharedPreferences("light_state", 0);

        mRed = findViewById(R.id.red);
        mGreen = findViewById(R.id.green);
        mBlue = findViewById(R.id.blue);
        mRed.setOnSeekBarChangeListener(this);
        mGreen.setOnSeekBarChangeListener(this);
        mBlue.setOnSeekBarChangeListener(this);
        mRed.setMax(255);
        mGreen.setMax(255);
        mBlue.setMax(255);
        mTvRed = findViewById(R.id.red_num);
        mTvGreen = findViewById(R.id.green_num);
        mTvBlue = findViewById(R.id.blue_num);

        devNameBt.setOnClickListener(this);
        devRateBt.setOnClickListener(this);
        devBt.setOnClickListener(this);
        flashCloseBt.setOnClickListener(this);
        flashOpenBt.setOnClickListener(this);
        liveCloseBt.setOnClickListener(this);
        systemRecoveryBt.setOnClickListener(this);
        lightAllCloseBt.setOnClickListener(this);
        lightLiveOne.setOnClickListener(this);
        lightLiveTwo.setOnClickListener(this);
        lightLiveThree.setOnClickListener(this);

        ratesDialog();
        devicesDialog();

        mGridView = findViewById(R.id.gv_lattice);
        initGridView();

        closeLeds = new ArrayList<>();
        closeLeds.add(LightController.Led.RED);
        closeLeds.add(LightController.Led.GREEN);
        closeLeds.add(LightController.Led.BLUE);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    List<LightController.Led> redList = new ArrayList<>();
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        if(seekBar == mRed){
            redList.clear();
            redList.add(LightController.Led.BLUE);
            LightController.getInstance().close(redList);
            LightController.getInstance().keepMode(LightController.Led.RED, 0, progress );
            mTvRed.setText("red   :" + progress);
        }else if(seekBar == mGreen){
            redList.clear();
            redList.add(LightController.Led.RED);
            LightController.getInstance().close(redList);
            LightController.getInstance().keepMode(LightController.Led.GREEN, 0, progress );
            mTvGreen.setText("green :" + progress);
        }else if(seekBar == mBlue){
            redList.clear();
            redList.add(LightController.Led.GREEN);
            LightController.getInstance().close(redList);
            LightController.getInstance().keepMode(LightController.Led.BLUE, 0, progress);
            mTvBlue.setText("blue   :" + progress);
        }
    }

    Intent intent;
    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (view == devNameBt) {
            if (mDevicesDialog != null) {
                mDevicesDialog.show();
                Log.e("jesse","6666");
            }else{
                Log.e("jesse","dialog is null");
            }
        } else if (view == devRateBt) {
            if (mRateDialog != null) mRateDialog.show();
        } else if(view == devBt){
            LightController.getInstance().close();
            if (devBt.getText().equals(getResources().getString(R.string.open))) {
                curDevice = devNameBt.getText().toString();
                curRate = Integer.parseInt(devRateBt.getText().toString());

                mSharedPreferences.edit().putString("curDevices",curDevice).commit();
                mSharedPreferences.edit().putInt("curRate",curRate).commit();

                LightController.getInstance().openDevice(this, curDevice, curRate);
                devBt.setText(R.string.close);
            } else {
                devBt.setText(R.string.open);
            }
        }else if (view == flashCloseBt){
            stopService(new Intent(this,MyService.class));
            LightController.getInstance().close(closeLeds);
            mSharedPreferences.edit().putInt("live_mode",-1).commit();
        }else if (view == flashOpenBt){
            clear();
            stopService(new Intent(this,MyService.class));
            //开乱闪,先关闭呼吸
            LightController.getInstance().crazyMode(0);
            mSharedPreferences.edit().putInt("live_mode",-1).commit();
        }else if (view == liveCloseBt){
           stopService(new Intent(this,MyService.class));
            mSharedPreferences.edit().putInt("live_mode",-1).commit();
        }else if (view == systemRecoveryBt){
            clear();
            LightController.getInstance().resume();
            mSharedPreferences.edit().putInt("live_mode",-1).commit();
        }else if (view == lightAllCloseBt){
            clear();
            LightController.getInstance().close(closeLeds);
            stopService(new Intent(this,MyService.class));
            mSharedPreferences.edit().putInt("live_mode",-1).commit();
        }else if(view == lightLiveOne){
            clear();
            intent = new Intent(this,MyService.class);
            intent.putExtra("ligthLiveLength",2);
            startService(intent);
            mSharedPreferences.edit().putInt("live_mode",2).commit();
        }else if(view == lightLiveTwo){
            clear();
            intent = new Intent(this,MyService.class);
            intent.putExtra("ligthLiveLength",4);
            startService(intent);
            mSharedPreferences.edit().putInt("live_mode",4).commit();
        }else if(view == lightLiveThree){
            intent = new Intent(this,MyService.class);
            intent.putExtra("ligthLiveLength",6);
            startService(intent);
            mSharedPreferences.edit().putInt("live_mode",6).commit();
        }
    }

    private void clear(){
        boolean clean = false;
        for(int i=0;i<selectors.length;i++){
            if(selectors[i] == 1)clean = true;
            selectors[i] = 0;
        }
        if(clean) gvAdapter.notifyDataSetChanged();
    }

    private void initGridView(){
         gvAdapter = new GvAdapter(this,ids,selectors);
        mGridView.setAdapter(gvAdapter);
        gvAdapter.setListener(new CheckListener() {
            @Override
            public void setCheckState(boolean isCheck, int positon) {
                for(int i=0;i<selectors.length;i++){
                    selectors[i] = 0;
                }
                selectors[positon] = isCheck ? 1 : 0;
                gvAdapter.notifyDataSetChanged();
                setLightModel(positon,isCheck);
            }
        });
    }

    boolean shows;
    private void setLightModel(int position,boolean show){
        shows = show;
        if(!shows){
            LightController.getInstance().close(closeLeds);
            return;
        }
        mSharedPreferences.edit().putInt("live_mode",-1).commit();

        if(position == 0){
            adjusColors(255,23,4);
        }else if(position == 1) {
            adjusColors(255,46,192);
        }else if(position == 2){
            adjusColors(29,0,166);
        }else if(position == 3) {
            adjusColors(192,8,171);
        }else if(position == 4){
            adjusColors(153,31,194);
        }else if(position == 5){
            adjusColors(55,170,223);
        }else if(position == 6){
            adjusColors(18,172,45);
        }else if(position == 7){
            adjusColors(62,197,89);
        }else if(position == 8){
            //adjusColors(252,217,0);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LightController.getInstance().keepMode(LightController.Led.RED, 0, 252);
                    SystemClock.sleep(30);
                    LightController.getInstance().keepMode(LightController.Led.GREEN, 0, 217);
                    SystemClock.sleep(40);
                    LightController.getInstance().keepMode(LightController.Led.BLUE, 0, 0);
                }
            }).start();

        }else if(position == 9) {
            LightController.getInstance().keepMode(LightController.Led.BLUE, 0, 255 );
            LightController.getInstance().keepMode(LightController.Led.RED, 0, 255 );
        }else if(position == 10){
            LightController.getInstance().keepMode(LightController.Led.GREEN, 0, 255 );
            LightController.getInstance().keepMode(LightController.Led.RED, 0, 255 );
        }else if(position == 11){
            adjusColors(0,255,255);
        }
    }

    private void adjusColors(final int red, final int green, final int blue) {
        new Thread(new Runnable() {
                @Override
                public void run() {
                        LightController.getInstance().keepMode(LightController.Led.RED, 0, red);
                        SystemClock.sleep(30);
                        LightController.getInstance().keepMode(LightController.Led.GREEN, 0, green);
                        SystemClock.sleep(25);
                        LightController.getInstance().keepMode(LightController.Led.BLUE, 0, blue);
                }
            }).start();
    }

    private void ratesDialog() {
        if (mRateDialog == null) {
            curRate = Integer.parseInt(SerialPort.RATES[13]);
            devRateBt.setText(SerialPort.RATES[13]);
            AlertDialog.Builder builder = new AlertDialog.Builder(this,
                    SerialPort.RATES.length);
            builder.setSingleChoiceItems(SerialPort.RATES, 13,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resetButton();
                            curRate = Integer.parseInt(SerialPort.RATES[which]);
                            devRateBt.setText(SerialPort.RATES[which]);
                            mRateDialog.dismiss();
                        }
                    });
            mRateDialog = builder.create();
        }
    }

    String[] devices = null;
    private void devicesDialog() {
        if (mDevicesDialog == null) {
            devices = SerialPort.getDevices();
            if (devices == null || devices.length == 0){
                devices = getDevice();
            }
            if(devices == null || devices.length == 0)return;
            curDevice = devices[0];
            devNameBt.setText(devices[0]);
            AlertDialog.Builder builder = new AlertDialog.Builder(this, devices.length);
            builder.setSingleChoiceItems(devices, 0,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resetButton();
                            curDevice = devices[which];
                            devNameBt.setText(curDevice);
                            mDevicesDialog.dismiss();
                        }
                    });
            mDevicesDialog = builder.create();
        }
    }

    private String[] getDevice(){
        String command = CommandUtils.execCommandSh("find /dev/ -name \"tty*\"");
        if (TextUtils.isEmpty(command)) return null;
        return command.split(" ");
    }

    private void resetButton() {
        if (getResources().getString(R.string.close).equals(devBt.getText().toString())) {
            devBt.performClick();
        }
    }
}
