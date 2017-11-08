package com.oneproject.www.allinone;

/**
 * Created by admin on 2017-07-23.
 */

public class DiscoveryResults {
    String hostname;
    String ipaddress;
    String hardwareaddress;
    String nicVender="";
    int check;
    boolean checked=true;
    boolean checked2=false;
    public String getHardwareaddress() {
        return hardwareaddress;
    }

    public String getHostname() {
        return hostname;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public String getNicVender() {
        return nicVender;
    }

    public int getCheck() {
        return check;
    }
}
