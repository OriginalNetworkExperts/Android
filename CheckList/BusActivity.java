package com.oneproject.www.allinone;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by admin on 2017-08-16.
 */

public class BusActivity extends AppCompatActivity {
    SharedPreferences.Editor editor;
    SharedPreferences setting;
    Button btnBus;
    ArrayList<String> busStops_id, busStops_name;

    BusAdapter busAdapter;
    EditText etBus;
    ListView lvBus;
    BusStopAPI busStopAPI;
    String station;

    int list_temp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);

        setTitle(R.string.title_bus);

        setting = getSharedPreferences("transit", 0);
        editor = setting.edit();
        lvBus = (ListView) findViewById(R.id.lvbus);
        etBus = (EditText) findViewById(R.id.etBus);
        btnBus = (Button) findViewById(R.id.btnSearch);
        btnBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                station = etBus.getText().toString();
                doBus(station);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Bus> list = new ArrayList<Bus>();
                        busStops_id = busStopAPI.getStationId();
                        busStops_name = busStopAPI.getStationName();
                            list=buslist();


                        busAdapter = new BusAdapter(getApplication(), list);
                        lvBus.setAdapter(busAdapter);//여기에 딜레이 후 시작할 작업들을 입력
                    }
                }, 1000);// 0.5초 정도 딜레이를 준 후 시작
            }
        });
    }
    ArrayList<Bus> buslist(){
        ArrayList<Bus> list=new ArrayList<>();
        list_temp=0;
        while (list_temp < busStops_id.size()) {
            Bus b = new Bus(busStops_id.get(list_temp), busStops_name.get(list_temp));
            list.add(b);
            list_temp++;
        }
        return list;
    }
    public void doBus(String station) {
        busStopAPI = new BusStopAPI(station);
    }

    class BusAdapter extends BaseAdapter {
        Context context;
        ArrayList<Bus> post;

        public BusAdapter(Context c, ArrayList<Bus> l) {
            context = c;
            post = l;
        }

        @Override
        public int getCount() { // adpter총길이
            return post.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertview, ViewGroup viewGroup) {
            LayoutInflater lay = LayoutInflater.from(context);
            View view = lay.inflate(R.layout.item_bus, null);
            TextView tv = (TextView) view.findViewById(R.id.chBus);
            TextView tv2=(TextView)view.findViewById(R.id.chBus2);
            tv2.setVisibility(View.GONE);
            tv.setTextColor(Color.WHITE);
            tv.setText(post.get(position).getBusName());
            final int p = position;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = getID(p);
                    String name = getName(p);
                    editor.putBoolean("setted", true);
                    editor.putString("bstop", id);
                    editor.putString("bstopname", name);
                    Toast.makeText(getApplication(), "설정되었습니다.", Toast.LENGTH_SHORT).show();
                    editor.commit();
                    setResult(RESULT_OK);
                    finish();
                }
            });
            return view;
        }

        public String getID(int position) {
            String id = post.get(position).getBusID();
            return id;
        }

        public String getName(int position) {
            String name = post.get(position).getBusName();
            return name;
        }
    }
}
