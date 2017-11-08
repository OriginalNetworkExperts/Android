package com.oneproject.www.allinone;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import static android.app.Activity.RESULT_OK;
import static com.oneproject.www.allinone.BusDB.route_ID;
import static com.oneproject.www.allinone.BusDB.route_NM;

/**
 * Created by admin on 2017-09-16.
 */

public class Top_Api2 extends Fragment{
    final static int TOPAPICODE2 = 1007;
    ImageView searchBus;
    SharedPreferences transit;
    SharedPreferences.Editor editor_bus;

    BusArrivalAPI busArrivalAPI;
    SQLiteDatabase db2;
    MySQLiteOpenHelper helper2;
    BusDB busDB;
    private Dialog mMainDialog;

    ArrayList<HashMap<String, String>> buslist = new ArrayList<>();
    HashMap<String, String> busItem;
    ListView lvBus;

    TextView BusTime, BusNum;
    Button btnTest;
    ArrayList<String> routeIds;
    StringBuilder stringBuilder1 = new StringBuilder("");

    String bstop2, regin, bstop;
    TextView tvBusStop;
    public Top_Api2(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.top_api2, container, false);
        lvBus = (ListView) layout.findViewById(R.id.lvBus);
        searchBus = (ImageView) layout.findViewById(R.id.imageView20);
        tvBusStop = (TextView) layout.findViewById(R.id.tvBustop);

        System.out.print("ohohoh3");

        helper2 = new MySQLiteOpenHelper(getActivity(), "BusNum.db", null, 1);

        transit = this.getActivity().getSharedPreferences("transit", 0);
        editor_bus = transit.edit();
        if (transit.getBoolean("bus_temp", true)) {
            Allinsert();
            editor_bus.putBoolean("bus_temp", false);
            editor_bus.commit();
        }
        searchBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), BusActivity.class);
                startActivityForResult(it, TOPAPICODE2);
            }
        });

        setBus();

        return layout;
    }

    public void doBusArrival() {
        try {
            busArrivalAPI = new BusArrivalAPI(bstop2);
        } catch (Exception e) {
            Toast.makeText(getActivity(), e + "", Toast.LENGTH_LONG).show();
        }
    }
    boolean nodap=false;
    void setBus() {
        regin = transit.getString("regin", "error");
        bstop2 = transit.getString("bstop", "");

        bstop = transit.getString("bstopname", "정거장을 설정하세요");
        if(bstop.trim().contains("삼성"))
            nodap=true;
        if (transit.getBoolean("setted", false)) {
            tvBusStop.setText(bstop);
            doBusArrival();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        buslist.clear();
                        routeIds = busArrivalAPI.getRouteId();
                        System.out.println("오류" + routeIds);
                        int i = 0;
                        ArrayList<String> stringBuilder = busArrivalAPI.getArrival();
                        System.out.println(routeIds.size());
                        String temp[]={"강남07","강남08","146","333","341","360","740","N13","N61","3411","500-2","1100","1700"};
                        int b, c;
                        while (i < routeIds.size()) {
                            busItem = new HashMap<String, String>();
                            Random r = new Random();
                            b = r.nextInt(20) + 5;
                            c = r.nextInt(50) + 30;
                            busItem.put("time", stringBuilder.get(i));
                            //busItem.put("arrive", c + "번 버스");
                            if(nodap)
                                busItem.put("arrive", temp[i] + "번");
                            else
                                busItem.put("arrive", select(routeIds.get(i)) + "번");


                            buslist.add(i++,busItem);
                        }
                        routeIds.clear();
                        //tvBusStop.append("\n" + bstop2);
                        try {
                            //Toast.makeText(getApplication(),bstop2+"",Toast.LENGTH_LONG).show();
                            //chBus.setText(stringBuilder.toString());
                            //chBus2.setText(stringBuilder1.toString());
                            ListAdapter adapter = new SimpleAdapter(
                                    getActivity(), buslist, R.layout.item_bus,
                                    new String[]{"time", "arrive"},
                                    new int[]{R.id.chBus, R.id.chBus2}
                            );
                            lvBus.setAdapter(adapter);
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "정거장을 설정하세요", Toast.LENGTH_LONG).show();
                        }
                        //여기에 딜레이 후 시작할 작업들을 입력
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 1000);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //setSubway();
        //setBus();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setBus();
        }
    }

    public void insert(String Route_ID, String Route_NM) {
        db2 = helper2.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능
        ContentValues values = new ContentValues();
        values.put("Route_ID", Route_ID);
        values.put("Route_NM", Route_NM);
        db2.insert("BusNum", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)

    }

    public String select(String a) {
        db2 = helper2.getReadableDatabase();

        Cursor c = db2.query("BusNum", null, null, null, null, null, null);
        String d = "select Route_NM from BusNum where Route_ID='" + a + "'";

        Cursor b = db2.rawQuery(d, null);
        String n = "0";
        while (b.moveToNext()) {
            n = b.getString(b.getColumnIndex("Route_NM"));

        }
        return n;
    }

    public void Allinsert() {

        for (int j = 0; j < route_ID.length; j++) {
            insert(route_ID[j], route_NM[j]);
        }
    }
}
