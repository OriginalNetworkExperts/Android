package com.oneproject.www.allinone;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.oneproject.www.allinone.DBHelper.TABLE_NAME3;
import static com.oneproject.www.allinone.DBHelper._HARDWARE;
import static com.oneproject.www.allinone.DBHelper._HOST;
import static com.oneproject.www.allinone.DBHelper._ID;
import static com.oneproject.www.allinone.DBHelper._IP;
import static com.oneproject.www.allinone.DBHelper._NIC;

public class IotActivity extends AppCompatActivity {
    ImageView icon_check, icon_iot, icon_setting, imageView20, imageView21;
    View dialogView;
    public static final int iotactcode = 1002;

    EditText iot_name, iot_num;

    String tagResult;

    private static final String TAG_result = "result";
    private static final String TAG_IOTNAME = "iotname";
    private static final String TAG_IOTNUM = "iotnum";
    private static final String TAG_IOTSTATUS = "iotstatus";
    private static final String TAG_ACTIVE = "active";

    JSONArray JsonArray = null;

    ArrayList<Iotstatus> personList;
    ListView securitylist;
    String iotstatus, iotname;

    DBHelper mHelper;
    SQLiteDatabase db;
    Cursor cursor;
    MyCursorAdapter mAdapter;
    BackPressCloseHandler b;

    String id;
    SharedPreferences login;

    @Override
    public boolean supportRequestWindowFeature(int featureId) {
        return super.supportRequestWindowFeature(featureId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.activity_iot);
        setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.settingon);

        login = getSharedPreferences("appData", Activity.MODE_PRIVATE);
        id = login.getString("ID", "");

        icon_check = (ImageView) findViewById(R.id.check);
        icon_iot = (ImageView) findViewById(R.id.iot);
        icon_setting = (ImageView) findViewById(R.id.setting);
        imageView20 = (ImageView) findViewById(R.id.imageView20);
        imageView21 = (ImageView) findViewById(R.id.imageView21);

        personList = new ArrayList<Iotstatus>();
        securitylist = (ListView) findViewById(R.id.lvSec);

        b=new BackPressCloseHandler(this,getString(R.string.btn_close));


        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getData("http://52.78.72.111:8080/iotlist.jsp?id="+id);
                }
            }, 100);
            mHelper = new DBHelper(this);
            db = mHelper.getWritableDatabase();
            cursor = db.rawQuery(String.format("select * from %s", TABLE_NAME3), null);
            mAdapter = new MyCursorAdapter(IotActivity.this, cursor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ListView lvHome = (ListView) findViewById(R.id.lvHome);
        lvHome.setAdapter(mAdapter);

        imageView20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //https://play.google.com/store/search?q=iot
                Intent it = new Intent(IotActivity.this, DownloadActivity.class);
                startActivityForResult(it, iotactcode);
            }
        });
        imageView21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogView = View.inflate(IotActivity.this, R.layout.iotview_sec, null); // inflate 화하였음.
                AlertDialog.Builder alert = new AlertDialog.Builder(IotActivity.this);
                alert.setTitle("Security IoT추가");
                alert.setView(dialogView);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        iot_name = (EditText) dialogView.findViewById(R.id.iot_name); //dialogView 에서 id를 찾아옴.
                        iot_num = (EditText) dialogView.findViewById(R.id.iot_num);

                        String iotname = iot_name.getText().toString();
                        String iotnum = iot_num.getText().toString();
                        String result = "";
                        try {
                            result = new CustomTask().execute(iotname, iotnum).get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        //task = new ToJsp(); 보내기만
                        //task.execute();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // new DownloadTask().execute(url1); XML리드
                                getData("http://52.78.72.111:8080/iotlist.jsp?id="+id);
                                //myAdapter.notifyDataSetChanged();
                            }
                        }, 100);
                    }
                });
                alert.setNegativeButton("취소", null);

                alert.show();

            }
        });
        icon_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icon_check.setImageResource(R.drawable.checkon);
                icon_iot.setImageResource(R.drawable.wifi);
                icon_setting.setImageResource(R.drawable.settingoff);
                stopAsync(g);
                Intent it = new Intent(IotActivity.this, CheckActivity.class);
                startActivity(it);
                finish();
            }
        });
        icon_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icon_check.setImageResource(R.drawable.check);
                icon_iot.setImageResource(R.drawable.wifi);
                icon_setting.setImageResource(R.drawable.settingon);
                stopAsync(g);
                Intent it = new Intent(IotActivity.this, SettingActivity.class);
                startActivity(it);
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        b.onBackPressed();
    }


    class MyCursorAdapter extends CursorAdapter {
        @SuppressWarnings("deprecation")
        public MyCursorAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.iotview, parent, false);
            return v;
        }


        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tvIot1 = (TextView) view.findViewById(R.id.iot_text1);
            //TextView tvIot2 = (TextView) view.findViewById(R.id.iot_text2);
            //TextView tvIot3 = (TextView) view.findViewById(R.id.iot_text3);
            //TextView tvIot4 = (TextView) view.findViewById(R.id.iot_text4);

            LinearLayout lliotHome = (LinearLayout) view.findViewById(R.id.lliotHome);

            final int id = cursor.getInt(cursor.getColumnIndex(_ID));
            //String host = cursor.getString(cursor.getColumnIndex(_HOST));
            final String ip = cursor.getString(cursor.getColumnIndex(_IP));
            //String hardware = cursor.getString(cursor.getColumnIndex(_HARDWARE));
            final String nic = cursor.getString(cursor.getColumnIndex(_NIC));

            tvIot1.setText(nic+"("+ip+")");

            lliotHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(IotActivity.this, nic, Toast.LENGTH_SHORT).show();
                    String iot_pkg="";
                    switch (nic){
                        case "KT IoT":
                            iot_pkg="com.kt.ohman";
                            break;
                        case "LG U+ IoT":
                            iot_pkg="com.lguplus.homeiot";
                            break;
                        case "SKT IoT":
                            iot_pkg="com.skt.sh";
                            break;
                        default:
                            break;
                    }
                    try {
                        if (getPackageList(iot_pkg)) {
                            Intent intent = getPackageManager().getLaunchIntentForPackage(iot_pkg);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else{
                            String url = "https://play.google.com/store/search?q=iot";
                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(i);
                        }
                    }catch (Exception e){
                    }
                }
            });
            lliotHome.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    db.execSQL(String.format("delete from %s where %s=%s",TABLE_NAME3,_ID,id));
                    refreshDB();
                    Toast.makeText(IotActivity.this,R.string.alert_delete,Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }

    }
    public boolean getPackageList(String pkg) {
        boolean isExist = false;

        PackageManager pkgMgr = getPackageManager();
        List<ResolveInfo> mApps;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mApps = pkgMgr.queryIntentActivities(mainIntent, 0);

        try {
            for (int i = 0; i < mApps.size(); i++) {
                if(mApps.get(i).activityInfo.packageName.startsWith(pkg)){
                    isExist = true;
                    break;
                }
            }
        }
        catch (Exception e) {
            isExist = false;
        }
        return isExist;
    }
    public void refreshDB() {
        cursor = db.rawQuery(String.format("select * from %s", TABLE_NAME3), null);
        mAdapter.changeCursor(cursor);
    }

    ArrayList<DiscoveryResults> discoveryResults;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            discoveryResults = new ArrayList<>();
            //Toast.makeText(getApplicationContext(), "recieved", Toast.LENGTH_SHORT).show();
            ArrayList<String> hostname = data.getStringArrayListExtra("hostname");
            ArrayList<String> ipaddress = data.getStringArrayListExtra("ip");
            ArrayList<String> hardware = data.getStringArrayListExtra("hardware");
            ArrayList<String> nic = data.getStringArrayListExtra("nic");
            int count = data.getIntExtra("count", 0);
            for (int i = 0; i < count; i++) {
                DiscoveryResults discoveryResult = new DiscoveryResults();
                discoveryResult.hostname = hostname.get(i);
                discoveryResult.ipaddress = ipaddress.get(i);
                discoveryResult.hardwareaddress = hardware.get(i);
                discoveryResult.nicVender = nic.get(i);
                String query = String.format("insert into %s values (null,'%s','%s','%s','%s')", TABLE_NAME3, hostname.get(i), ipaddress.get(i), hardware.get(i), nic.get(i));
                db.execSQL(query);
                discoveryResults.add(i, discoveryResult);
            }
            refreshDB();
        }
    }

    void stopAsync(AsyncTask a) {
        try {
            if (a.getStatus() == AsyncTask.Status.RUNNING)
                a.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.


        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.title, null);
        ImageView ivIcon = (ImageView) actionbar.findViewById(R.id.title_icon);
        TextView ivLetter = (TextView) actionbar.findViewById(R.id.title_letter);
        Button btn_logout = (Button) actionbar.findViewById(R.id.logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logout
                Intent intent = new Intent(IotActivity.this, MainActivity.class);
                startActivity(intent);
                SharedPreferences auto = getSharedPreferences("appData", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = auto.edit();
                editor.clear();
                editor.commit();
                Toast.makeText(IotActivity.this, getString(R.string.logout), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        ivIcon.setImageResource(R.drawable.wifion);
        ivLetter.setText(R.string.title_iot);
        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar) actionbar.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        return true;
    }

    MyAdapter myAdapter;

    protected void showList() {
        try {
            //값을 못받아옴
            JSONObject jsonObj = new JSONObject(tagResult);
            personList.clear();
            JsonArray = jsonObj.getJSONArray(TAG_result);
            for (int i = 0; i < JsonArray.length(); i++) {
                JSONObject c = JsonArray.getJSONObject(i);
                String name = c.getString(TAG_IOTNAME);
                String num = c.getString(TAG_IOTNUM);
                String checked = c.getString(TAG_IOTSTATUS);
                String active=c.getString(TAG_ACTIVE);
                Iotstatus persons = new Iotstatus();
                persons.name = name;
                persons.num = num;
                persons.swCheck = checked.equals("1");
                persons.active=active.equals("1");

                try {
                    personList.get(i);
                } catch (Exception e) {
                    personList.add(i, persons);
                } finally {
                    personList.set(i, persons);
                }
            }
            myAdapter = new MyAdapter(IotActivity.this, personList);

            securitylist.setAdapter(myAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class MyAdapter extends BaseAdapter {
        ArrayList<Iotstatus> personList;
        Context c;

        public MyAdapter(Context c, ArrayList<Iotstatus> personList) {
            this.c = c;
            this.personList = personList;
        }

        @Override
        public Object getItem(int position) {
            return personList.get(position);
        }

        @Override
        public int getCount() {
            return personList.size();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            final Iotstatus iotstatus;
            final String iot_num;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(c);
                convertView = inflater.inflate(R.layout.iotlist_sec, parent, false);
                viewHolder.lllist=(RelativeLayout)convertView.findViewById(R.id.lllist_sec);
                viewHolder.iotname = (TextView) convertView.findViewById(R.id.iotname);
                viewHolder.iotnum = (TextView) convertView.findViewById(R.id.iotnum);
                viewHolder.iotstatus = (Switch) convertView.findViewById(R.id.iotstatus);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            iotstatus = personList.get(position);
            iot_num = iotstatus.getNum();
            final String iot_name=iotstatus.getName();
            viewHolder.iotname.setText(iot_name);
            viewHolder.iotnum.setText(iot_num);
            viewHolder.iotstatus.setChecked(iotstatus.swCheck);

            viewHolder.iotstatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    String status = "";
                    if (iotstatus.swCheck)
                        status = "0";
                    else
                        status = "1";

                    iotstatus.swCheck = !iotstatus.swCheck;
                    try {
                        new onoffTask().execute(status, iot_num).get();
                    } catch (Exception e) {
                    }finally {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getData("http://52.78.72.111:8080/iotlist.jsp?id="+id);
                            }
                        }, 500);
                    }
                    //왜있음
                        /*
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                            }
                        }, 500);
                        */
                }
            });
            if(iotstatus.active) {
                viewHolder.lllist.setBackgroundResource(R.color.color_warning);
                viewHolder.lllist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //사진 보여주는 액티비티->경보끄기
                        Intent it=new Intent(IotActivity.this,WarningActivity.class);
                        it.putExtra("iot_name",iot_name);
                        it.putExtra("iot_num",iot_num);
                        startActivity(it);
                    }
                });
            }
            viewHolder.lllist.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder alt=new AlertDialog.Builder(IotActivity.this);
                    alt.setTitle(R.string.title_del);
                    alt.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                new Iot_delete().execute(iot_num).get();
                                personList.remove(position);
                                notifyDataSetChanged();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    alt.setNegativeButton(R.string.btn_no,null);
                    alt.show();
                    return false;
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView iotname;
            TextView iotnum;
            Switch iotstatus;
            RelativeLayout lllist;
        }
    }

    class GetDataJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String uri = params[0];
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                StringBuilder sb = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String webTag;
                int i = 0;
                while ((webTag = bufferedReader.readLine()) != null) {
                    sb.append(webTag + "\n");
                    //Toast.makeText(IotActivity.this, (i++) + "", Toast.LENGTH_SHORT).show();
                    //if (isCancelled())
                    //   break;
                }//while
                return sb.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }//doinbackground

        @Override
        protected void onPostExecute(String result) {
            tagResult = result;
            showList();
        }//onpostExecute
    }

    GetDataJSON g;

    public void getData(String url) {
        g = new GetDataJSON();
        g.execute(url);
    }

    class onoffTask extends AsyncTask<String, Void, String> { //값 보내고 받기
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://52.78.72.111:8080/iotsw.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "iotstatus=" + strings[0] + "&iotnum=" + strings[1];
                Log.i("qpqp", sendMsg);
                osw.write(sendMsg);
                osw.flush();
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                        if (isCancelled())
                            break;
                    }
                    receiveMsg = buffer.toString();

                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }

    }

    class CustomTask extends AsyncTask<String, Void, String> { //값 보내고 받기
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://52.78.72.111:8080/iot_input.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "id=" + id + "&iotname=" + strings[0] + "&iotnum=" + strings[1];
                osw.write(sendMsg);
                osw.flush();
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                        if (isCancelled())
                            break;
                    }
                    receiveMsg = buffer.toString();

                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }
    }
    class Iot_delete extends AsyncTask<String, Void, String> { //값 보내고 받기
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://52.78.72.111:8080/iot_remove.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg ="iotnum=" + strings[0];
                osw.write(sendMsg);
                osw.flush();
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                        if (isCancelled())
                            break;
                    }
                    receiveMsg = buffer.toString();

                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }
    }
}