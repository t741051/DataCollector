package com.mitlab.zusliu.User.Interface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


import android.app.Activity;
import android.app.MediaRouteButton;
import android.app.Notification;
import android.app.NotificationManager;

import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.ListView;
import android.view.View;


import com.THLight.USBeacon.App.Lib.BatteryPowerData;
import com.THLight.USBeacon.App.Lib.iBeaconData;
import com.THLight.USBeacon.App.Lib.iBeaconScanManager;
import com.mitlab.zusliu.R;
import com.mitlab.zusliu.Update.List.View.ListAdapter;
import com.mitlab.zusliu.Update.List.View.ListItem;

import android.os.Vibrator;


import system.config.Setup;

//////////////////////////////////
import android.view.ViewGroup.LayoutParams;

///////////////////////////////////

public class MainActivity extends Activity implements iBeaconScanManager.OniBeaconScan {


    // 初始旗標
    boolean[] flag = new boolean[5];

    // 初始化人機介面物件
    public ListView l = null;

    // 初始化裝置掃描管理器物件
    public iBeaconScanManager scanner = null;

    // 初始化清單配置器物件
    public ListAdapter ListAdapter = null;

    // 初始化藍牙配置器物件
    public BluetoothAdapter BluetoothAdapter = null;

    // 參數：裝置清單(資料清單)
    public ArrayList<ScannedBeacon> beacons = new ArrayList<ScannedBeacon>();



    //public Button button;
    FrameLayout frame_1;
    FrameLayout frame_2;
    public ImageView [] map_mark_image = new ImageView[15];
    public Button [] button_function = new Button[5];
    // 儲存地標位置
    static int [] mark_position_X = {600,560,600,600,530,260,480,380,380,280,480};
    static int [] mark_position_Y = {30 ,110,180,285,420,400,480,420,320,320,300};

    static int [] btn_position_X = {0,100,0};
    static int [] btn_position_Y = {0,0,100};
    static int [] beacon_state_1 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    // TODO 方法：主程式
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Step.01(初始化流程) 取得人機介面物件
        this.l = (ListView) findViewById(R.id.myListView);
        // Step.02(初始化流程) 取得裝置掃描管理器(iBeaconScanManager)物件
        this.scanner = new iBeaconScanManager(this, this);
        // Step.03(初始化流程) 取得清單配置器(ListAdapter)物件
        // Step.04(初始化流程) 取得藍牙(bluetooth)服務
        this.ListAdapter = new ListAdapter(this);
        this.BluetoothAdapter = this.BluetoothAdapter.getDefaultAdapter();
        // Step.05(初始化流程) 檢查藍牙(bluetooth)狀態
        if (!this.BluetoothAdapter.isEnabled()) {
            // turn on bluetooth device
            Intent intent = new Intent(this.BluetoothAdapter.ACTION_REQUEST_ENABLE);
            MainActivity.this.startActivityForResult(intent, Setup.REQ_ENABLE_BT);
        }
        // Step.06(初始化流程) 處理器(handler)物件執行

        this.handler.sendEmptyMessageDelayed(Setup.REQ_SCAN_BEACON, 0);
        this.handler.sendEmptyMessageDelayed(Setup.REQ_UPDATE_BEACON, Setup.TIME_BEACON_UPDATE);
        // Step.07(初始化流程) 設定清單配置器
        this.l.setAdapter(this.ListAdapter);
///////////////////////////////////////////////////////////////////////////////////
        frame_1 = new FrameLayout(this);
        frame_1 = (FrameLayout)findViewById(R.id.frame_layout_1);
        frame_2 = new FrameLayout(this);
        frame_2 = (FrameLayout)findViewById(R.id.frame_layout_2);
        display_mark(11);

///////////////////////////////////////////////////////////////////////////////////
    }
///////////////////////////////////////////////////////////////////////////////////測試
    public int correspod_beacon(int major,int minor){
        int num;
        if(major == 0 && minor == 6){
            num = 0;
        }else if(major == 1 && minor == 0){
            num = 1;
        }else if(major == 4 && minor == 1){
            num = 2;
        }else if(major == 4 && minor == 2){
            num = 3;
        }else if(major == 4 && minor == 3){
            num = 4;
        }else if(major == 8 && minor == 2){
            num = 5;
        }else if(major == 9 && minor == 1){
            num = 6;
        }else if(major == 9 && minor == 2){
            num = 7;
        }else if(major == 9 && minor == 3){
            num = 8;
        }else if(major == 10 && minor == 0){
            num = 9;
        }else{
            num = 10;
        }
        return num;
    }
     public void buttonOnClick(View v) {
        // 寫要做的事...
         //display_btn(3);
         change_mark(1,2);
    }
    public void display_btn(int x){
        // TODO 方法：產生X個圖標
        for(int i = 0;i < x;i++)
        {
            button_function[i] = new Button(this);
            button_function[i].setX(btn_position_X[i]);
            button_function[i].setY(btn_position_Y[i]);
            frame_1.addView(button_function[i]);
            button_function[i].getLayoutParams().height = 65;
            button_function[i].getLayoutParams().width = 65;
        }
    }
    public void display_mark(int x){
        // TODO 方法：產生X個圖標
        for(int i = 0;i < x;i++)
        {
            map_mark_image[i] = new ImageView(this);
            map_mark_image[i].setImageResource(R.drawable.map_mark);
            map_mark_image[i].setX(mark_position_X[i]);
            map_mark_image[i].setY(mark_position_Y[i]);
            frame_1.addView(map_mark_image[i]);
            map_mark_image[i].getLayoutParams().height = 65;
            map_mark_image[i].getLayoutParams().width = 65;
        }
    }
    public void change_mark(int x,int state){
        // TODO 方法：選擇第X個 改變圖標 0:黑色 1:紅色
        switch(state){

            case 0:
                Log.v("s_h_b", String.valueOf(map_mark_image[x].getLayoutParams().height));
                map_mark_image[x].getLayoutParams().height = 65;
                map_mark_image[x].getLayoutParams().width = 65;
                map_mark_image[x].setImageResource(R.drawable.map_mark);
                Log.v("e_h_b", String.valueOf(map_mark_image[x].getLayoutParams().height));
                break;
            case 1:
                Log.v("s_h_r", String.valueOf(map_mark_image[x].getLayoutParams().height));
                map_mark_image[x].getLayoutParams().height = 65;
                map_mark_image[x].getLayoutParams().width = 65;
                map_mark_image[x].setImageResource(R.drawable.red_map_mark);
                Log.v("e_h_r", String.valueOf(map_mark_image[x].getLayoutParams().height));
                break;

            case 2:
                Log.v("s_h_r", String.valueOf(map_mark_image[x].getLayoutParams().height));
                map_mark_image[x].getLayoutParams().height = 65;
                map_mark_image[x].getLayoutParams().width = 65;
                map_mark_image[x].setImageResource(R.drawable.light_mark);
                Log.v("e_h_r", String.valueOf(map_mark_image[x].getLayoutParams().height));
                break;



        }
    }
    /*
    public void beacon_state_1(int major,int minor,int rssi){
        if()
    }

     */
    public void beacon_state(int major,int minor,int rssi){
        if(-rssi < 60){
            beacon_state_1[correspod_beacon(major,minor)] = 1;
            //change_mark(correspod_beacon(major,minor),1);
        }else{
            beacon_state_1[correspod_beacon(major,minor)] = 0;
            //change_mark(correspod_beacon(major,minor),0);
        }

    }

////////////////////////////////////////////////////////////////////////////////////
// TODO 方法：活動處理器
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // turn on bluetooth device
            case Setup.REQ_ENABLE_BT: {
                if (resultCode == RESULT_OK) {/***/}
                break;
            }
        }
    }

    // TODO 方法：處理器(背景執行)
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // start beacon scanner
                case Setup.REQ_SCAN_BEACON: {
                    synchronized (ListAdapter) {
                        // Step.01(啟動裝置背景執行流程) 呼叫方法：裝置掃描(掃描裝置訊號)(掃描裝置電池量)
                        scanner.startScaniBeacon(Setup.TIME_BEACON_START);
                        handler.sendEmptyMessageDelayed(Setup.REQ_SCAN_BEACON, Setup.TIME_BEACON_RESTART);
                        break;
                    }
                }

                // update beacon status
                case Setup.REQ_UPDATE_BEACON: {
                    synchronized (ListAdapter) {
                        // Step.01(更新裝置背景執行流程) 呼叫方法：裝置掃描(掃描裝置變動)
                        VerifyBeacons();

                        ListAdapter.notifyDataSetChanged();
                        handler.sendEmptyMessageDelayed(Setup.REQ_UPDATE_BEACON, Setup.TIME_BEACON_UPDATE);
                        break;
                    }
                }
            }
        }
    };

    // TODO 方法：裝置掃描(掃描裝置變動)
    public void VerifyBeacons() {

        for (int i = this.beacons.size() - 1; i >= 0; i--) {
            ScannedBeacon beacon = this.beacons.get(i);

            if ((System.currentTimeMillis() - beacon.lastUpdate) > Setup.TIME_BEACON_TIMEOUT) {
                // Step.02(更新裝置背景執行流程) 移除清單
                change_mark(beacon.major,0);
                this.beacons.remove(i);

            }
        }

        this.ListAdapter.clearItem();
        // Step.03(更新裝置背景執行流程) 清除項目

        for (ScannedBeacon beacon : this.beacons) {
            // Step.03(更新裝置背景執行流程) 重新新增項目
            ListItem item = new ListItem();

            item.setText1(String.valueOf(beacon.beaconUuid));
            item.setText2(String.valueOf(beacon.major));
            item.setText3(String.valueOf(beacon.minor));
            item.setText4(String.valueOf(beacon.rssi));
            item.setText5(String.valueOf(beacon.batteryPower));

            //////////////////////////////////////////////
            beacon_state(beacon.major,beacon.minor,beacon.rssi);
            //////////////////////////////////////////////

            this.ListAdapter.addItem(item);
        }

        // show beacons list for debug
        if (Setup.FLAG_DEBUG) {
            Setup._tb.ShowBeaconsList(this.beacons);
        }
    }

    // TODO 方法：裝置掃描(掃描裝置訊號)
    @Override
    public void onScaned(iBeaconData BeaconData) {
        ScannedBeacon beacon = null;

        for (ScannedBeacon _beacon : this.beacons) {
            if (_beacon.equals(BeaconData, false)) {
                beacon = _beacon;
                break;
            }
        }

        if (beacon == null) {
            // Step.02(啟動裝置背景執行流程) 新增清單
            beacon = ScannedBeacon.copyOf(BeaconData);
            this.beacons.add(beacon);
        } else {
            beacon.rssi = BeaconData.rssi;
        }
        check(BeaconData.major, BeaconData.minor, BeaconData.rssi);

        beacon.lastUpdate = System.currentTimeMillis();
    }

    // TODO 方法：裝置掃描(掃描裝置電池量)
    @Override
    public void onBatteryPowerScaned(BatteryPowerData batteryPowerData) {
        for (int i = 0; i < this.beacons.size(); i++) {
            ScannedBeacon beacon = this.beacons.get(i);

            if (beacon.macAddress.equals(batteryPowerData.macAddress)) {
                beacon.batteryPower = batteryPowerData.batteryPower;
                this.beacons.set(i, beacon);
            }
        }
    }


    //TODO 方法：確認編號與距離
    public void check(int major, int minor, int rssi) {
        Map map = new HashMap();

        int ele = major * 10 + minor;
        switch (ele) {
            case 1:
                map.put(1, rssi);
                detector(rssi, 70, 80, "1");
                break;

            case 2:
                map.put(2, rssi);
                detector(rssi, 70, 80, "2");
                break;

            case 3:
                map.put(3, rssi);
                detector(rssi, 70, 80, "3");
                break;

            case 4:
                map.put(4, rssi);
                detector(rssi, 70, 80, "4");
                break;

            default:
                break;

        }
    }

    //TODO 方法：通知震動,推播,回到app
    public void detector(int distance, int upper, int lower, String num) {


        NotificationManager NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        callback();

        notification(num, callback());

        if (flag[Integer.parseInt(num)] == false) {

            if (distance > -upper) {
                myVibrator();
                flag[Integer.parseInt(num)] = true;
                NM.notify(Integer.parseInt(num), notification(num, callback()));

            }

        } else if (distance <= -lower) {

            flag[Integer.parseInt(num)] = false;
            NM.cancel(Integer.parseInt(num));

        }

    }

    //TODO 方法：回app
    public PendingIntent callback() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, 0);
        return pendingIntent;
    }


    //TODO 方法：狀態列
    public Notification notification(String num, PendingIntent callback) {

        Notification notification = new Notification.Builder(this)

                .setSmallIcon(R.drawable.ntust)
                .setContentTitle("MIT LAB beacon " + num)
                .setContentText("MIT LAB beacon " + num)
                .setContentIntent(callback)
                .build();

        return notification;
    }


    //TODO 方法：震動
    public void myVibrator() {
        Vibrator myVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        myVibrator.vibrate(1000);
    }


}
