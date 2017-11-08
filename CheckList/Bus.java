package com.oneproject.www.allinone;

/**
 * Created by pc on 2017-07-18.
 */

public class Bus {
    private String busID;
    private String busName;
    //private String busMobile;
    public Bus(String busID,String busName){
        this.busID=busID;
        this.busName=busName;
    }
    public Bus(){

    }

    public void setBusID(String busID) {
        this.busID = busID;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getBusID(){
        return busID;
    }
    public String getBusName(){
        return busName;
    }
}

