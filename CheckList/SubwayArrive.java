package com.oneproject.www.allinone;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by pc on 2017-08-08.
 */
public class SubwayArrive {
    String a,b;

    String stationArriveTime[] = new String[2];
    String stationStartTime;
    String stationWay;
    int inout;

    public String getStationArriveTime(){ return stationArriveTime[0]; }

    public String getStationNextTime(){ return stationArriveTime[1]; }

    public String getStationStartTime(){
        return stationStartTime;
    }

    public String getStationWay(){
        return stationWay;
    }

    //String inout_tag[] = {"1", "2"}; // [ 1 - 상행, 내선 /  2 - 하행, 외선]
    //String week_tag[] = {"1", "2", "3"}; // [1 - 평일,  2 - 토요일,  3 - 휴일, 일요일]

    public SubwayArrive(String station_cd, int inout){
        Calendar oCalendar = Calendar.getInstance( );
        int day = oCalendar.get(Calendar.DAY_OF_WEEK) - 1; // 일요일(0), 월~금(1~5), 토요일(6)
        int oDay; // 요일구분

        if(day == 0){
            oDay = 3; //일요일
        }
        else if(day == 6){
            oDay = 2; //토요일
        }
        else {
            oDay = 1; //평일
        }

        String arriveUrl = "인증키"
                + station_cd + "/" + inout + "/" + oDay;
        try {
            new DownloadWebPageTask().execute(arriveUrl);
        } catch (Exception e) { e.printStackTrace();}
    }

    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try{
                return (String)downloadUrl(params[0]);
            }catch (IOException e){
                return "다운로드실패";
            }
        }

        protected void onPostExecute(String result) {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();
                boolean bSet = false, bSet2 = false, bSet3 = false;
                int i=0;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        if (tag_name.equals("LEFTTIME")){
                            bSet = true;
                        }

                        if (tag_name.equals("SUBWAYNAME")){
                            bSet3 = true;
                        }
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (bSet) {
                            stationArriveTime[i] = xpp.getText();
                            System.out.println(xpp.getText()+"테스트");
                            if(i==0){
                                a=xpp.getText();
                            }else{
                                b=xpp.getText();
                            }
                            bSet= false;
                            i++;
                        }

                        if(bSet3){
                            stationWay = xpp.getText();
                            bSet3= false;
                        }

                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
            }
        }

        private String downloadUrl(String myurl) throws IOException {

            HttpURLConnection conn = null;
            try{
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));
                String line = null;
                String page = "";
                while((line = bufreader.readLine()) != null) {
                    page += line;
                }

                return page;
            } finally {
                conn.disconnect();
            }
        }
    }
}