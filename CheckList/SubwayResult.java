package com.oneproject.www.allinone;

/**
 * Created by pc on 2017-08-08.
 */

public class SubwayResult {

    private String subwayName;
    private String subwayCode;
    private String subwayLinenum;

    public SubwayResult(String subwayCode, String subwayName, String subwayLinenum){
        this.subwayCode = subwayCode;
        this.subwayName = subwayName;
        this.subwayLinenum = subwayLinenum;
    }

    public String getSubwayCode(){
        return subwayCode;
    }

    public String getSubwayName() { return subwayName; }

    public String getSubwayLinenum(){
        return subwayLinenum; 
    }

}

