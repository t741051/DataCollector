package com.mitlab.zusliu.User.Interface;

import com.THLight.USBeacon.App.Lib.iBeaconData;

public class ScannedBeacon extends iBeaconData
{
    public long lastUpdate = 0;

    // using default parameter constructor
    public ScannedBeacon() { /***/ }

    // using custom parameter constructor
    public ScannedBeacon(long _l) { /***/ }

    public static ScannedBeacon copyOf(iBeaconData iBeacon)
    {
        ScannedBeacon newBeacon = new ScannedBeacon();

        newBeacon.beaconUuid = iBeacon.beaconUuid;
        newBeacon.major = iBeacon.major;
        newBeacon.minor = iBeacon.minor;
        newBeacon.oneMeterRssi = iBeacon.oneMeterRssi;
        newBeacon.rssi = iBeacon.rssi;
        newBeacon.lastUpdate = 0;
        newBeacon.macAddress = iBeacon.macAddress;

        return newBeacon;
    }

}