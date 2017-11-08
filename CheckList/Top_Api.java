package com.oneproject.www.allinone;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
 * Created by admin on 2017-08-14.
 */
public class Top_Api extends Fragment {
    final static int TOPAPICODE = 1006;
    ImageView searchSubway;
    TextView chSubway_num, chSubway, chSubway2;
    String code_sub;
    SubwayArrive subwayArrive;
    SharedPreferences transit;
    int temp=0;
    private Dialog mMainDialog;

    public Top_Api() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.top_api, container, false);

        System.out.print("ohohoh2");

        searchSubway = (ImageView) layout.findViewById(R.id.imageView21);

        chSubway_num = (TextView) layout.findViewById(R.id.tvSubway_num);
        chSubway = (TextView) layout.findViewById(R.id.chSubway);
        chSubway2 = (TextView) layout.findViewById(R.id.chSubway2);
        ivNm=(ImageView)layout.findViewById(R.id.ivNm);

        transit = this.getActivity().getSharedPreferences("transit", 0);

        searchSubway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getActivity(), SubwaySearch.class);
                startActivityForResult(it, TOPAPICODE);
            }
        });

        subway_num = transit.getString("subway_num", "");
        subway_name = transit.getString("subway_name", getString(R.string.loading));
        code_sub = transit.getString("code", "");


        return layout;
    }

    String subway_name, subway_num;
    String aTime, nTime, nowTime, nowtime;
    String subwayArriveTime, subwayNextTime;
    ImageView ivNm;
    int resTime1, resTime2;
    String dis;
    void setSubway() {
        int inout = transit.getInt("inout", 1);
        if(inout==1)
            dis="상행,내선";
        else
            dis="하행,외선";
        chSubway_num.setText(subway_name+"("+dis+")");

        switch (subway_num){
            case "1":
                ivNm.setImageResource(R.drawable.s1);
                break;
             case "2":
                ivNm.setImageResource(R.drawable.s2);
                break;
             case "3":
                ivNm.setImageResource(R.drawable.s3);
                break;
             case "4":
                ivNm.setImageResource(R.drawable.s4);
                break;
             case "5":
                ivNm.setImageResource(R.drawable.s5);
                break;
             case "6":
                ivNm.setImageResource(R.drawable.s6);
                break;
             case "7":
                ivNm.setImageResource(R.drawable.s7);
                break;
             case "8":
                ivNm.setImageResource(R.drawable.s8);
                break;
             case "9":
                ivNm.setImageResource(R.drawable.s9);
                break;
             case "K"://중앙
                ivNm.setImageResource(R.drawable.jungang);
                break;
             case "B"://분당
                ivNm.setImageResource(R.drawable.bundang);
                break;
        }
        doSubwayArrive(code_sub, inout);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm:ss");
                nowTime = sdfNow.format(new Date(System.currentTimeMillis()));
                nowtime = nowTime.replace(":", "");
                subwayArriveTime = subwayArrive.a;
                subwayNextTime = subwayArrive.b;

                // Toast.makeText(getActivity(), subwayArriveTime + "   " + subwayNextTime, Toast.LENGTH_SHORT);
                try {
                    aTime = subwayArriveTime.replace(":", "");
                    nTime = subwayNextTime.replace(":", "");
                }catch (Exception e){
                    e.printStackTrace();
                    return;
                }
                resTime1 = (Integer.parseInt(aTime) - Integer.parseInt(nowtime)) / 100;
                resTime2 = (Integer.parseInt(nTime) - Integer.parseInt(nowtime)) / 100;

                if (Integer.parseInt(aTime) == 999999||resTime1>30||resTime1<0) {
                    chSubway.setText("출발대기");
                    chSubway2.setText(subwayArriveTime);

                } else {
                    resTime1 = (Integer.parseInt(aTime) - Integer.parseInt(nowtime)) / 100;
                    resTime2 = (Integer.parseInt(nTime) - Integer.parseInt(nowtime)) / 100;

                    if (Integer.parseInt(aTime) / 10000 != Integer.parseInt(nowtime) / 10000) {
                        resTime1 -= 40;
                        resTime2 -= 40;
                    } else if (Integer.parseInt(nTime) / 10000 != Integer.parseInt(nowtime) / 10000)
                        resTime2 -= 40;

                    if (resTime1 <= 0) {
                        chSubway.setText("곧 도착");
                        chSubway2.setText(resTime2 + "분 후");
                    }
                    else {
                        chSubway.setText(resTime1 + "분 후");
                        chSubway2.setText(resTime2 + "분 후");
                    }
                }
            }
        }, 1000);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
            setSubway();
    }

    public void doSubwayArrive(String station_cd, int inout) {
        subwayArrive = new SubwayArrive(station_cd, inout);
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
            setSubway();
        }
    }

}
