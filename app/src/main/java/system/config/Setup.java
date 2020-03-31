package system.config;

import test.bench._TestBench;

public class Setup
{
    // 初始化測試除錯器(_TestBench)物件
    public static _TestBench _tb = new _TestBench();

    // 參數：主程式(MainActivity)使用
    public static boolean FLAG_DEBUG = true;

    // 參數：主程式(MainActivity)使用
    public static final int TIME_BEACON_START = 1000;
    public static final int TIME_BEACON_RESTART = 1100;
    public static final int TIME_BEACON_UPDATE =100;
    public static final int TIME_BEACON_TIMEOUT = 2000;

    // 參數：主程式(MainActivity)使用
    public static final int REQ_SCAN_BEACON = 1000;
    public static final int REQ_UPDATE_BEACON = 1001;

    // 參數：主程式(MainActivity)使用
    public static final int REQ_ENABLE_BT = 2000;

}