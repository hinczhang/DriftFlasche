package org.standardserve.driftflasche.network;

import org.standardserve.driftflasche.BuildConfig;

public class NetworkAccess {

    private NetworkAccess(){}

    public static String getLOGINAccess(){
        return BuildConfig.IP_ADDRESS  + BuildConfig.LOGIN_ID;
    }

    public static String getBOTTLEAccess(){
        return BuildConfig.IP_ADDRESS  + BuildConfig.BOTTLE_ID;
    }
}
