package test.bench;

import android.util.Log;
import com.mitlab.zusliu.User.Interface.ScannedBeacon;
import java.util.ArrayList;

public class _TestBench
{
    // using default parameter constructor
    public _TestBench() { /***/ }

    // using custom parameter constructor
    public void ShowBeaconsList(ArrayList<ScannedBeacon> _Beacons)
    {
        Log.v("偵測裝置(數量)", String.valueOf(_Beacons.size()));

        // get beacons information for debug
        for(ScannedBeacon beacon : _Beacons)
        {
            Log.v("偵測裝置(識別碼)", String.valueOf(beacon.beaconUuid));
            Log.v("偵測裝置(主編號)", String.valueOf(beacon.major));
            Log.v("偵測裝置(副編號)", String.valueOf(beacon.minor));
            Log.v("偵測裝置(訊號量)", String.valueOf(beacon.rssi));
            Log.v("偵測裝置(電池量)", String.valueOf(beacon.batteryPower));
        }
    }

}