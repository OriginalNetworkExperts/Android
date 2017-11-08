package com.oneproject.www.allinone;

/**
 * Created by pc on 2017-07-18.
 */

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
import java.util.ArrayList;

/**
 * Created by admin on 2017-06-17.
 */

public class BusStopAPI {
    String serviceUrl = "http://openapi.gbis.go.kr/ws/rest/busstationservice";
    String serviceKey = "DhmrVYeCftvLOg%2Ff17mXzZHO5lDvipMLJN7L0TzEPQ0MJ1VzYlSU7s4YZzKPrq5TbborQAuca8GrC%2BTu%2Fp%2FNHw%3D%3D";
    ArrayList<String> stationIds=new ArrayList<>();
    //String[] stationIds;
    ArrayList<String> stationNames=new ArrayList<>();
    String stationId="";//정류소 ID
    String stationName="";//정류소 이름
    String mobileNo="";//정류소고유번호
    String strUrl="";
    public BusStopAPI(String station){
        strUrl = serviceUrl + "?serviceKey="+serviceKey+"&keyword=" + station;
        try {
            new DownloadWebPageTask().execute(strUrl);
        } catch (Exception e) {
            //stationIds[0]="";
            stationNames.set(0,"검색결과가 없습니다.");
        }
    }

    public ArrayList<String> getStationId(){
        return stationIds;
    }
    /*
    public String[] getStationId(){
        return stationIds;
    }
    */
    public ArrayList<String> getStationName(){
        return stationNames;
    }
    public String getMobileNo(){
        return mobileNo;
    }
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPostExecute(String result) {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();

                boolean sSet_stationId = false;
                boolean sSet_stationName = false;
                boolean sSet_mobileNo = false;
                int i=0;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        if (tag_name.equals("stationId")) {
                            sSet_stationId = true;
                        }
                        if (tag_name.equals("stationName")) {
                            sSet_stationName = true;
                        }
                        if(tag_name.equals("mobileNo")){
                            sSet_mobileNo = true;
                        }
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (sSet_stationId) {
                            stationId = xpp.getText();
                            //stationIds[i++]=stationId;
                            stationIds.add(stationId);
                            //tvBus.setText("정류소 ID : " + stationId + "\n");
                            //myStationId = stationId;
                            sSet_stationId = false;
                        }
                        if (sSet_stationName) {
                            stationName = xpp.getText();
                            stationNames.add(stationName);
                            //tvBus.append("정류소 이름 : " + stationName + "\n");
                            sSet_stationName = false;
                        }
                        if (sSet_mobileNo){
                            mobileNo = xpp.getText();
                            //tvBus.setText("정류소 번호 : " + mobileNo + "\n");
                            sSet_mobileNo = false;
                        }
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
                //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return (String) downloadUrl((String) params[0]);
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }

        private String downloadUrl(String myurl) throws IOException {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));

                String line = null;
                String page = "";
                while ((line = bufreader.readLine()) != null) {
                    page += line;
                }
                return page;
            }finally {
                conn.disconnect();
            }
        }
    }
}