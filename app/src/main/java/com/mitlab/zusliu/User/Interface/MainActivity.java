package com.mitlab.zusliu.User.Interface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import android.widget.TextView;
import android.widget.Toast;

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
    FrameLayout frame_1;    //地圖frame
    FrameLayout frame_2;    //按鍵frame

    public Button btn_1,btn_2,btn_3;  //商家總覽 地點標記 按鍵
    public ImageButton [] img_btn_mark = new ImageButton[15];   //地標按鍵
    public TextView mark_state;
    static boolean flag_btn = false;    // "+" 按鍵狀態
    static boolean btn_1_flag = false;  //"+"按鍵狀態
    static boolean btn_2_flag = false;  //地點標記按鍵狀態
    static boolean btn_3_flag = false;  //網站導覽按鍵狀態
    static boolean [] flag_mark_btn = {false,false,false,false,false,false,false,false,false,false,false};  //地標被點選狀態
    static boolean [] flag_mark_btn2 = {false,false,false,false,false,false,false,false,false,false,false};  //地點標記狀態
    static boolean [] flag_mark_btn3 = {false,false,false,false,false,false,false,false,false,false,false};  //網站導覽狀態

    // 儲存地標位置
    /////////////////////////////////0   1   2   3   4   5   6   7   8   9   10
    static int [] mark_position_X = {560,560,560,560,470, 80,240,380,380, 90,240};
    static int [] mark_position_Y = {30 ,115,200,285,375,430,430,430,320,320,320};
    static int [] beacon_num      = {  5, 81, 30, 41, 42, 43,  6, 82, 91, 92, 93};
    static int [] img_btn_state   = {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0};
    static int choose_one = 0;    //只讓一個點顯示
    static int pre_beacon = 0;
    static int beacon_in  = 0;
    static int user_place = 12;
    static int beacon_amount = 11;
    Timer timer = new Timer();;    //宣告一個時間函示

    static String mark_state_text = "";
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
        btn_1 = (Button)findViewById(R.id.button3);
        btn_2 = (Button)findViewById(R.id.button4);
        btn_3 = (Button)findViewById(R.id.button5);

        mark_state = new TextView(this);

        frame_2.addView(mark_state);
        mark_state.setTextColor(getResources().getColor(android.R.color.holo_blue_light));

        display_mark_btn(beacon_amount);
///////////////////////////////////////////////////////////////////////////////////
    }
    ///////////////////////////////////////////////////////////////////////////////////測試
    //TODO Beacon編號對應
    public int correspod_beacon(int major,int minor){
        beacon_in = (major * 10) + minor;
        int num;
        for(num = 0;num <= 10;num++){
            if(beacon_in == beacon_num[num]) break;
        }
        return num;
    }

    //TODO "+"按鍵
    public void buttonOnClick(View v) {
        // 寫要做的事...
        if(btn_2_flag==false && btn_3_flag==false){
            btn_1_flag = !btn_1_flag;
            if(btn_1_flag == true){
                btn_1.setVisibility(View.VISIBLE);
                btn_2.setVisibility(View.VISIBLE);
                btn_3.setVisibility(View.VISIBLE);
            }else{
                btn_1.setVisibility(View.INVISIBLE);
                btn_2.setVisibility(View.INVISIBLE);
                btn_3.setVisibility(View.INVISIBLE);
                btn_2_flag = false;
                btn_2.setBackgroundColor(getResources().getColor(android.R.color.white));
                btn_3_flag = false;
                btn_3.setBackgroundColor(getResources().getColor(android.R.color.white));
            }
        }

    }
    //TODO 商家資訊按鍵
    public void buttonOnClick_1(View v) {
        // 寫要做的事...
        if(btn_2_flag == false && btn_3_flag == false) {
            connect_to_web(0);
        }
/*
        btn_1_flag = !btn_1_flag;
        if(btn_1_flag) btn_1.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        else  btn_1.setBackgroundColor(getResources().getColor(android.R.color.white));

 */
        //display_mark_state();
        //connect_to_web(0);
    }
    //TODO 地點標記按鍵
    public void buttonOnClick_2(View v) {
        // 寫要做的事...
        if(btn_1_flag == true && btn_3_flag == false) btn_2_flag = !btn_2_flag;
        else {}
        if(btn_2_flag){
            btn_2.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
            /*for(int i = 0;i < 11;i++) {
                if (flag_mark_btn[i] == true && flag_mark_btn2[i] == true) {
                    change_mark(i,2);
                }
                else if(flag_mark_btn[i] == true && flag_mark_btn2[i] == false){
                    change_mark(i,1);
                }
                else if(flag_mark_btn[i] == false && flag_mark_btn2[i] == true){
                    change_mark(i,3);
                }
                else {
                    change_mark(i, 0);
                }
            }*/
        }
        else  btn_2.setBackgroundColor(getResources().getColor(android.R.color.white));

        //connect_to_web(0);
    }
    //TODO 網站導覽暗箭
    public void buttonOnClick_3(View v) {
        // 寫要做的事...
        if(btn_1_flag == true && btn_2_flag == false)btn_3_flag = !btn_3_flag;
        else {}
        if(btn_3_flag){
            for(int i = 0;i < beacon_amount;i++) flag_mark_btn3[i] = false;
            btn_3.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        }
        else  {
            btn_3.setBackgroundColor(getResources().getColor(android.R.color.white));
            for(int i = 0;i < 11;i++) {
                if (flag_mark_btn[i] == true && flag_mark_btn2[i] == true) {
                    change_mark(i,2);
                }
                else if(flag_mark_btn[i] == true && flag_mark_btn2[i] == false){
                    change_mark(i,1);
                }
                else if(flag_mark_btn[i] == false && flag_mark_btn2[i] == true){
                    change_mark(i,3);
                }
                else {
                    change_mark(i, 0);
                }
            }
            for(int j = 0;j < beacon_amount;j++){
                if(flag_mark_btn3[j]==true) connect_to_web(j+1);
            }
            /*if(btn_2_flag==false) {
                connect_to_web(0);
            }*/
        }
    }

    //TODO 連結到網站
    public void connect_to_web(int a){
        Uri uri = Uri.parse("https://www.google.com");
        switch (a){
            case 0:
                uri = Uri.parse("http://140.118.122.242/test/beacon_test.php");
                break;
            case 1:
                uri = Uri.parse("https://www.yahoo.com.tw");
                break;
            case 2:
                uri = Uri.parse("https://www.nike.com/tw/");
                break;
            case 3:
                uri = Uri.parse("https://moodle.ntust.edu.tw/");
                break;
            case 4:
                uri = Uri.parse("https://translate.google.com.tw/");
                break;
            case 5:
                uri = Uri.parse("https://www.youtube.com/");
                break;
            case 6:
                uri = Uri.parse("https://www.pornhub.com/");
                break;
            case 7:
                uri = Uri.parse("https://www.facebook.com/");
                break;
            case 8:
                uri = Uri.parse("https://www.ntust.edu.tw/home.php");
                break;
            case 9:
                uri = Uri.parse("https://www.ntust.edu.tw/home.php");
                break;
            case 10:
                uri = Uri.parse("https://www.ntust.edu.tw/home.php");
                break;
            case 11:
                uri = Uri.parse("https://www.ntust.edu.tw/home.php");
                break;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    // TODO 方法：查看圖標狀態
    public void display_mark_state(){

        mark_state_text = "";
        for(int i = 0;i < beacon_amount;i++){
            mark_state_text += flag_mark_btn[i] + " ";
        }
        mark_state.setText(mark_state_text);
    }
    // TODO 方法：產生X個圖標按鍵
    public void display_mark_btn(int x){
        for(int i = 0;i < x;i++){
            img_btn_mark[i] = new ImageButton(this);
            img_btn_mark[i].setImageResource(R.drawable.map_mark);
            img_btn_mark[i].setX(mark_position_X[i]);
            img_btn_mark[i].setY(mark_position_Y[i]);
            frame_1.addView(img_btn_mark[i]);
            img_btn_mark[i].getLayoutParams().height = 65;
            img_btn_mark[i].getLayoutParams().width = 65;
            img_btn_mark[i].setBackgroundColor(0xffffff);
            img_btn_click(i);
        }
    }

    //TODO 地標按鍵觸發
    public void img_btn_click(int x){
        final int a = x;
        img_btn_mark[a].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_2_flag){
                    flag_mark_btn2[a] = !flag_mark_btn2[a];
                    if(flag_mark_btn2[a] == true){
                        for(int i = 0;i < beacon_amount;i++) {
                            flag_mark_btn2[i] = false;
                            if(flag_mark_btn[i] == true){
                                change_mark(i,1);
                            }
                            else {
                                change_mark(i, 0);
                            }
                        }
                        flag_mark_btn2[a] = true;
                        change_mark(a,3);
                        Toast.makeText(getApplicationContext(),"number " + a, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        change_mark(a,0);
                        for(int i = 0;i < beacon_amount;i++) {
                            flag_mark_btn2[i] = false;
                            if (flag_mark_btn[i] == true) {
                                change_mark(i,1);
                            }
                            else {
                                change_mark(i, 0);

                            }
                        }
                    }
                }
                else if(btn_3_flag){
                    flag_mark_btn3[a] = !flag_mark_btn3[a];
                    if(flag_mark_btn3[a] == true){
                        for(int i = 0;i < beacon_amount;i++) {
                            flag_mark_btn3[i]=false;
                            if (flag_mark_btn[i] == true && flag_mark_btn2[i] == true) {
                                change_mark(i,2);
                            }
                            else if(flag_mark_btn[i] == true && flag_mark_btn2[i] == false){
                                change_mark(i,1);
                            }
                            else if(flag_mark_btn[i] == false && flag_mark_btn2[i] == true){
                                change_mark(i,3);
                            }
                            else {
                                change_mark(i, 0);
                            }
                        }
                        flag_mark_btn3[a] = true;
                        change_mark(a,4);
                        Toast.makeText(getApplicationContext(),"number " + a, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //change_mark(a,0);
                        for(int i = 0;i < beacon_amount;i++) {
                            flag_mark_btn3[i]=false;
                            if (flag_mark_btn[i] == true && flag_mark_btn2[i] == true) {
                                change_mark(i,2);
                            }
                            else if(flag_mark_btn[i] == true && flag_mark_btn2[i] == false){
                                change_mark(i,1);
                            }
                            else if(flag_mark_btn[i] == false && flag_mark_btn2[i] == true){
                                change_mark(i,3);
                            }
                            else {
                                change_mark(i, 0);
                            }
                        }
                    }
                }
                else if(btn_2_flag!=true && btn_3_flag!=true){
                    Toast.makeText(getApplicationContext(),"number " + a, Toast.LENGTH_SHORT).show();
                    //connect_to_web(1);
                }
            }
        });
    }

    public void change_mark(int x,int state){
        // TODO 方法：選擇第X個 改變圖標 0:黑色 1:紅色
        switch(state){
            case 0:
                Log.v("s_h_b", String.valueOf(img_btn_mark[x].getLayoutParams().height));
                img_btn_mark[x].getLayoutParams().height = 65;
                img_btn_mark[x].getLayoutParams().width = 65;
                img_btn_mark[x].setImageResource(R.drawable.map_mark);
                Log.v("e_h_b", String.valueOf(img_btn_mark[x].getLayoutParams().height));
                break;
            case 1:
                Log.v("s_h_r", String.valueOf(img_btn_mark[x].getLayoutParams().height));
                img_btn_mark[x].getLayoutParams().height = 65;
                img_btn_mark[x].getLayoutParams().width = 65;
                img_btn_mark[x].setImageResource(R.drawable.blue_mark);
                Log.v("e_h_r", String.valueOf(img_btn_mark[x].getLayoutParams().height));
                break;
            case 2:
                img_btn_mark[x].getLayoutParams().height = 65;
                img_btn_mark[x].getLayoutParams().width = 65;
                img_btn_mark[x].setImageResource(R.drawable.light_mark);
                break;
            case 3:
                img_btn_mark[x].getLayoutParams().height = 65;
                img_btn_mark[x].getLayoutParams().width = 65;
                img_btn_mark[x].setImageResource(R.drawable.red_marker);
                break;
            case 4:
                img_btn_mark[x].getLayoutParams().height = 65;
                img_btn_mark[x].getLayoutParams().width = 65;
                img_btn_mark[x].setImageResource(R.drawable.pin);
                break;
            default:
                img_btn_mark[x].getLayoutParams().height = 65;
                img_btn_mark[x].getLayoutParams().width = 65;
                img_btn_mark[x].setImageResource(R.drawable.map_mark);
                break;
        }
        img_btn_state[x] = state;
    }
    //TODO 判斷beacon距離改變狀態
    public void beacon_state(int major,int minor,int rssi){
        if(-rssi < 60){
            //change_mark(correspod_beacon(major, minor), 1);
            for(int i = 0;i < beacon_amount;i++) {
                if(flag_mark_btn[i]==true){
                    choose_one=1;
                    //change_mark(i, 1);
                }
            }
            if(choose_one == 0){
                change_mark(correspod_beacon(major, minor), 1);
                user_place = correspod_beacon(major, minor);
                flag_mark_btn[user_place]=true;
            }
            choose_one=0;
            /*if(choose_one ==1){
                if(pre_beacon != beacon_in) {
                    change_mark(correspod_beacon(pre_beacon / 10, pre_beacon % 10), 0);
                }
                change_mark(correspod_beacon(major, minor), 1);
                pre_beacon = beacon_in;
            }
            else{
                choose_one = 1;
                change_mark(correspod_beacon(major, minor), 1);
                user_place = correspod_beacon(major, minor);
                flag_mark_btn[user_place]=true;
                pre_beacon = beacon_in;
            }*/
        }
        else{
            //////////////////////////////////////////////////////
            //標記或導覽狀態距離變遠 把點化回來
            /*
            for(int a=0;a<beacon_amount;a++){
                if(btn_2_flag){
                    flag_mark_btn2[a] = !flag_mark_btn2[a];
                    if(flag_mark_btn2[a] == true){
                        for(int i = 0;i < beacon_amount;i++) {
                            flag_mark_btn2[i] = false;
                            if(flag_mark_btn[i] == true){
                                change_mark(i,1);
                            }
                            else {
                                change_mark(i, 0);
                            }
                        }
                        flag_mark_btn2[a] = true;
                        change_mark(a,3);
                        Toast.makeText(getApplicationContext(),"number " + a, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        change_mark(a,0);
                        for(int i = 0;i < beacon_amount;i++) {
                            flag_mark_btn2[i] = false;
                            if (flag_mark_btn[i] == true) {
                                change_mark(i,1);
                            }
                            else {
                                change_mark(i, 0);

                            }
                        }
                    }
                }
                else if(btn_3_flag){
                    flag_mark_btn3[a] = !flag_mark_btn3[a];
                    if(flag_mark_btn3[a] == true){
                        for(int i = 0;i < beacon_amount;i++) {
                            flag_mark_btn3[i]=false;
                            if (flag_mark_btn[i] == true && flag_mark_btn2[i] == true) {
                                change_mark(i,2);
                            }
                            else if(flag_mark_btn[i] == true && flag_mark_btn2[i] == false){
                                change_mark(i,1);
                            }
                            else if(flag_mark_btn[i] == false && flag_mark_btn2[i] == true){
                                change_mark(i,3);
                            }
                            else {
                                change_mark(i, 0);
                            }
                        }
                        flag_mark_btn3[a] = true;
                        change_mark(a,4);
                        Toast.makeText(getApplicationContext(),"number " + a, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //change_mark(a,0);
                        for(int i = 0;i < beacon_amount;i++) {
                            flag_mark_btn3[i]=false;
                            if (flag_mark_btn[i] == true && flag_mark_btn2[i] == true) {
                                change_mark(i,2);
                            }
                            else if(flag_mark_btn[i] == true && flag_mark_btn2[i] == false){
                                change_mark(i,1);
                            }
                            else if(flag_mark_btn[i] == false && flag_mark_btn2[i] == true){
                                change_mark(i,3);
                            }
                            else {
                                change_mark(i, 0);
                            }
                        }
                    }
                }
            }
            */
            //////////////////////////////////////////////////////
            //choose_one = 0;
            flag_mark_btn[correspod_beacon(major,minor)]=false;
            //change_mark(correspod_beacon(major,minor),0);
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////
// TODO 方法：活動處理器
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            // turn on bluetooth device
            case Setup.REQ_ENABLE_BT:{
                if (resultCode == RESULT_OK) {/***/}
                break;
            }
        }
    }

    // TODO 方法：處理器(背景執行)
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                // start beacon scanner
                case Setup.REQ_SCAN_BEACON:{
                    synchronized (ListAdapter){
                        // Step.01(啟動裝置背景執行流程) 呼叫方法：裝置掃描(掃描裝置訊號)(掃描裝置電池量)
                        scanner.startScaniBeacon(Setup.TIME_BEACON_START);
                        handler.sendEmptyMessageDelayed(Setup.REQ_SCAN_BEACON, Setup.TIME_BEACON_RESTART);
                        break;
                    }
                }

                // update beacon status
                case Setup.REQ_UPDATE_BEACON:{
                    synchronized (ListAdapter){
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
    public void VerifyBeacons(){

        for(int i = this.beacons.size() - 1; i >= 0; i--){
            ScannedBeacon beacon = this.beacons.get(i);

            if((System.currentTimeMillis() - beacon.lastUpdate) > Setup.TIME_BEACON_TIMEOUT){
                // Step.02(更新裝置背景執行流程) 移除清單
                //////////////////////////////////////////////////////////////////////////////////////
                change_mark(correspod_beacon(beacon.major,beacon.minor),0);
                choose_one = 0;
                /////////////////////////////////////////////////////////////////////////////////////
                this.beacons.remove(i);

            }
        }

        this.ListAdapter.clearItem();
        // Step.03(更新裝置背景執行流程) 清除項目

        for(ScannedBeacon beacon : this.beacons){
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
        if(Setup.FLAG_DEBUG){
            Setup._tb.ShowBeaconsList(this.beacons);
        }
    }

    // TODO 方法：裝置掃描(掃描裝置訊號)
    @Override
    public void onScaned(iBeaconData BeaconData){
        ScannedBeacon beacon = null;

        for(ScannedBeacon _beacon : this.beacons){
            if(_beacon.equals(BeaconData, false)) {
                beacon = _beacon;
                break;
            }
        }

        if(beacon == null){
            // Step.02(啟動裝置背景執行流程) 新增清單
            beacon = ScannedBeacon.copyOf(BeaconData);
            this.beacons.add(beacon);
        }else{
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
    public void detector(int distance, int upper, int lower, String num){


        NotificationManager NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        callback();

        notification(num, callback());

        if (flag[Integer.parseInt(num)] == false){
            if (distance > -upper){
                myVibrator();
                flag[Integer.parseInt(num)] = true;
                NM.notify(Integer.parseInt(num), notification(num, callback()));

            }
        }else if(distance <= -lower){
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
