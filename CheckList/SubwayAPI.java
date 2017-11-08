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
import java.util.ArrayList;
/**
 * Created by pc on 2017-08-07.
 */
public class SubwayAPI {
    String strUrl = "http://openapi.seoul.go.kr:8088/686549444e696f6439386b73727373/xml/SearchSTNBySubwayLineService/1/683/";
    static ArrayList<String> station_cd = new ArrayList<>();
    static ArrayList<String> station_nm = new ArrayList<>();
    static ArrayList<String> station_lm = new ArrayList<>();
    String stationCode;
    String stationName;
    String stationLinenum;
    public SubwayAPI() {
        try {
            //station_cd.clear();
            //station_nm.clear();
            //station_lm.clear();
            new DownloadWebPageTask().execute(strUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ArrayList<String> getStationCode() {return station_cd; }
    public ArrayList<String> getStationName() {
        return station_nm;
    }
    public ArrayList<String> getStationLineNum() { return station_lm; }
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        protected void onPostExecute(String result) {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();
                boolean bSet_cd = false, bSet_nm = false, bSet_lm = false;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        if (tag_name.equals("STATION_CD")) {
                            bSet_cd = true;
                        }
                        if (tag_name.equals("STATION_NM")) {
                            bSet_nm = true;
                        }
                        if (tag_name.equals("LINE_NUM")) {
                            bSet_lm = true;
                        }
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (bSet_cd) {
                            // 코드
                            stationCode = xpp.getText();
                            station_cd.add(stationCode);
                            //System.out.println(station_cd);
                            bSet_cd = false;
                        }
                        if (bSet_nm) {
                            // 역명
                            stationName = xpp.getText();
                            station_nm.add(stationName);
                            //System.out.println(content);
                            bSet_nm = false;
                        }
                        if (bSet_lm) {
                            // 라인넘버
                            stationLinenum = xpp.getText();
                            station_lm.add(stationLinenum);
                            //System.out.println(content);
                            bSet_lm = false;
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                return (String) downloadUrl(params[0]);
            } catch (IOException e) {
                return "다운로드실패";
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
            } finally {
                conn.disconnect();
            }
        }
    }
}
