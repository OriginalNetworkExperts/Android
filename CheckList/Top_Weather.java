package com.oneproject.www.allinone;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.oneproject.www.allinone.DBHelper.TABLE_NAME;
import static com.oneproject.www.allinone.DBHelper._CONTEXT;
import static com.oneproject.www.allinone.DBHelper._DATE;
import static com.oneproject.www.allinone.DBHelper._NOTE;

/**
 * Created by admin on 2017-08-14.
 */

public class Top_Weather extends Fragment {
    LinearLayout lltop1,lltop2;
    boolean rain;
    TextView txtNow1,txtNow2,txtNow3;
    Handler mHandler;
    Runnable r;

    DBHelper mHelper;
    SQLiteDatabase db;

    String mdate;

    SharedPreferences login;
    SharedPreferences weather;
    SharedPreferences.Editor editor;
    String user;

    TextView textView1,textView2,tvLast;
    ImageView Tweather;
    LinearLayout llweather;
    public Top_Weather() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public boolean isRain(String weather) {
        if (weather.equals("맑음")) {
            Tweather.setImageResource(R.drawable.clear);
        } else if (weather.equals("흐림")) {
            Tweather.setImageResource(R.drawable.cloudy);
        } else if (weather.equals("구름많음")) {
            Tweather.setImageResource(R.drawable.cloudy);
        } else if (weather.equals("구름조금")) {
            Tweather.setImageResource(R.drawable.partly_cloudy);
        } else if (weather.equals("눈")) {
            Tweather.setImageResource(R.drawable.snow);
            rain = true;
        } else if (weather.contains("비")) {
            Tweather.setImageResource(R.drawable.rain);
            rain = true;
        }
        return rain;
    }
    String formatDate;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.top_weather,container,false);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow=new SimpleDateFormat("yyyyMMdd");
        mdate=sdfNow.format(date);
        SimpleDateFormat sdfNow1 = new SimpleDateFormat("yyyy년 MM월 dd일");
        SimpleDateFormat sdfNow2 = new SimpleDateFormat("aa");
        SimpleDateFormat sdfNow3 = new SimpleDateFormat("hh:mm");
        formatDate = sdfNow1.format(date);
        String formatDate2 = sdfNow2.format(date);
        String formatDate3 = sdfNow3.format(date);
         tvLast=(TextView)layout.findViewById(R.id.tvLast);
        SimpleDateFormat sdfNow4 = new SimpleDateFormat("E");

        System.out.print("ohohoh1");

        llweather=(LinearLayout)layout.findViewById(R.id.llweather);

        textView1 = (TextView)layout. findViewById(R.id.textView1);
        textView2 = (TextView) layout.findViewById(R.id.textView2);
        tvLast=(TextView)layout.findViewById(R.id.tvLast);

        Tweather = (ImageView) layout.findViewById(R.id.Tweather);
        Tweather.setImageResource(R.drawable.cloudy);

        txtNow1 = (TextView) layout.findViewById(R.id.txtNow1);
        txtNow2 = (TextView) layout.findViewById(R.id.txtNow2);
        txtNow3 = (TextView) layout.findViewById(R.id.txtNow3);

        txtNow1.setText(formatDate);
        txtNow2.setText(formatDate2);
        txtNow3.setText(formatDate3);
        login = this.getActivity().getSharedPreferences("appData", Activity.MODE_PRIVATE);
        weather=this.getActivity().getSharedPreferences("weather",0);
        editor=weather.edit();
        user = login.getString("ID", "");
        mHandler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                updateDisplay();
            }
        };
        mHandler.postDelayed(r, 1000);
        try {
            mHelper = new DBHelper(getActivity());
            db = mHelper.getWritableDatabase();
        }catch (Exception e){

        }
        if(weather.getBoolean("weather",false)){
            textView1.setText(weather.getString("tem",""));
            textView2.setText(weather.getString("bush",""));
            isRain(weather.getString("icon",""));
            tvLast.setText(R.string.alert_update);
            tvLast.append(weather.getString("last",""));
        }
        if(!(weather.getBoolean("weather",false))) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setWeather();
                }
            }, 500);
        }
        llweather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWeather();
            }
        });
        return layout;
    }
    void setWeather(){
        WeatherConnection weatherConnection = new WeatherConnection();
        WeatherConnection2 weatherConnection2 = new WeatherConnection2();
        WeatherConnection3 weatherConnection3 = new WeatherConnection3();

        AsyncTask<String, String, String> result = weatherConnection.execute("", "");

        AsyncTask<String, String, String> result2 = weatherConnection2.execute("", "");

        AsyncTask<String, String, String> result3 = weatherConnection3.execute("", "");
        String weather="",msg="",msg2="";
        try {
            msg = result.get();
            System.out.println(msg);

            msg2 = result2.get();
            System.out.println(msg2);

            String msg3 = result3.get();
            System.out.println(msg3);

            textView1.setText(msg.toString());
            textView2.setText(msg2.toString());
            tvLast.setText(R.string.alert_update);
            tvLast.append(formatDate);
            weather = msg3.toString();
            isRain(weather);

        } catch (Exception e) {

        }finally {
            editor.putBoolean("weather",true);
            editor.putString("tem",msg);
            editor.putString("bush",msg2);
            editor.putString("icon",weather);
            editor.putString("last",formatDate);
            editor.apply();
        }
        if (rain) {
            try {
                String context = "";
                System.out.println(mdate + "test");
                Cursor c = db.rawQuery(String.format("select %s from %s where %s='우산'and %s='AUTO' and %s='%s'", _CONTEXT, TABLE_NAME, _CONTEXT, _NOTE, _DATE, mdate), null);
                while (c.moveToNext())
                    context = c.getString(c.getColumnIndex(_CONTEXT));
                if (!(context.length() > 0)) {
                    String query = "insert into " + TABLE_NAME + " values(" +
                            "null, '" + user + "' , '" + mdate + "','F','우산','','F','','','AUTO');";
                    exeQuery(query);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public boolean exeQuery(String query) {
        try {
            db.execSQL(query);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public void updateDisplay() {
        //현재시간
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow1 = new SimpleDateFormat("yyyy년 MM월 dd일");
        SimpleDateFormat sdfNow2 = new SimpleDateFormat("aa");
        SimpleDateFormat sdfNow3 = new SimpleDateFormat("hh:mm");
        String formatDate = sdfNow1.format(date);
        String formatDate2 = sdfNow2.format(date);
        String formatDate3 = sdfNow3.format(date);


        txtNow1.setText(formatDate);
        txtNow2.setText(formatDate2);
        txtNow3.setText(formatDate3);

        mHandler.postDelayed(r, 1000);
    }

    private class WeatherConnection extends AsyncTask<String, String, String> {

        // 백그라운드에서 작업하게 한다
        @Override
        public String doInBackground(String... params) {

            // Jsoup을 이용한 날씨데이터 Pasing하기.
            try {
                String path = "http://weather.naver.com/rgn/townWetr.nhn?naverRgnCd=09380104";

                /*
                00 		string 	해외 or 알 수 없음.
                01 		string 	강원도
                02 		string 	경기도
                03 		string 	경상남도
                04 		string 	경상북도
                05 		string 	광주광역시
                06 		string 	대구광역시
                07 		string 	대전광역시
                08 		string 	부산광역시
                09 		string 	서울특별시
                10 		string 	울산광역시
                11 		string 	인천광역시
                12 		string 	전라남도
                13 		string 	전라북도
                14 		string 	제주특별자치도
                15 		string 	충청남도
                16 		string 	충청북도
                17 		string 	세종특별자치시
                 */

                Document document = Jsoup.connect(path).get();
                Elements elements = document.select("em");
                Element targetElement = elements.get(2);

                Elements elements1 = document.select("span");
                Element targetElement1 = elements1.get(43);
                Element targetElement2 = elements1.get(40);

                String text = targetElement.text();
                String text1 = targetElement1.text();
                String text2 = targetElement2.text();
                return text;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // 네트워크 작업은 AsyncTask 를 사용해야 한다
    public class WeatherConnection2 extends AsyncTask<String, String, String> {

        // 백그라운드에서 작업하게 한다
        @Override
        public String doInBackground(String... params) {

            try {
                String path = "http://weather.naver.com/rgn/townWetr.nhn?naverRgnCd=09380104";
                Document document = Jsoup.connect(path).get();

                Elements elements1 = document.select("span"); //

                System.out.println(elements1); //

                Element targetElement1 = elements1.get(43); //
                Element targetElement2 = elements1.get(40); //

                String text1 = targetElement1.text(); //
                String text2 = targetElement2.text(); //

                return text1;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // 네트워크 작업은 AsyncTask 를 사용해야 한다
    public class WeatherConnection3 extends AsyncTask<String, String, String> {

        // 백그라운드에서 작업하게 한다
        @Override
        public String doInBackground(String... params) {

            try {
                String path = "http://weather.naver.com/rgn/townWetr.nhn?naverRgnCd=09380104";
                Document document = Jsoup.connect(path).get();

                Elements str = document.select("strong"); //
                Element targetElement = str.get(3); //
                String text = targetElement.text(); //

                System.out.println(targetElement);

                return text;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
