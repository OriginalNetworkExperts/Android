package com.oneproject.www.allinone;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
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

public class SettingActivity extends AppCompatActivity {
    ImageView setting1, setting2, setting3;
    ImageView icon_check, icon_iot, icon_setting;
    ImageView dataplus;

    View dialogView;

    String name = "";
    String pass = "";
    String ent = "";
    //String nfc="";
    String Authority = "일반";

    String tagResult;
    private static final String TAG_result = "result";
    private static final String TAG_authority = "authority";
    private static final String TAG_name = "name";
    private static final String TAG_pass = "password";
    //private static final String TAG_nfc = "nfc";
    private static final String TAG_enter = "enter";

    final int settingactivitycode = 1004;

    SharedPreferences login, setting;
    SharedPreferences.Editor editor;

    String id = "", rate = "", pw = "";

    JSONArray JsonArray = null;
    ArrayList<HashMap<String, String>> personList;
    ListView list;
    GridView list2;

    RadioButton rblist, rbIcon;

    final ArrayList<UserInfo> array = new ArrayList<>();

    TextView tvDel;

    EditText etName_user;

    BackPressCloseHandler b;

    String user_name;

    LinearLayout lltop;

    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        icon_check = (ImageView) findViewById(R.id.check);
        icon_iot = (ImageView) findViewById(R.id.iot);
        icon_setting = (ImageView) findViewById(R.id.setting);
        tvDel=(TextView)findViewById(R.id.tvDel);

        login = getSharedPreferences("appData", Activity.MODE_PRIVATE);
        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();

        id = login.getString("ID", "");
        pw = login.getString("PWD", "");
        user_name = login.getString("user", "null");

        list = (ListView) findViewById(R.id.listView);
        list2 = (GridView) findViewById(R.id.userView);
        lltop = (LinearLayout) findViewById(R.id.lltop);
        rblist = (RadioButton) findViewById(R.id.rblist);
        rbIcon = (RadioButton) findViewById(R.id.rbIcon);

        dataplus = (ImageView) findViewById(R.id.dataplus);
        personList = new ArrayList<HashMap<String, String>>();
        try {
            getData("http://52.78.72.111:8080/setup_member_info.jsp?id=" + id);
        } catch (Exception e) {

        }
        rbIcon.setChecked(setting.getBoolean("grid2", false));
        rbIcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rbIcon.isChecked()) {
                    editor.putBoolean("grid2", true);
                    lltop.setVisibility(View.GONE);
                    list2.setVisibility(View.VISIBLE);
                } else {
                    editor.putBoolean("grid2", false);
                    lltop.setVisibility(View.VISIBLE);
                    list2.setVisibility(View.GONE);
                }
                editor.apply();
                changeAdapter(myAdapter);
            }
        });
        dataplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogView = View.inflate(SettingActivity.this, R.layout.alt_user, null);
                AlertDialog.Builder alt = new AlertDialog.Builder(SettingActivity.this);
                alt.setView(dialogView);
                alt.setTitle(R.string.alt_user);
                etName_user = (EditText) dialogView.findViewById(R.id.etName_user);
                etName_user.setHint(getString(R.string.signup_check, getString(R.string.signup_name)));
                alt.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //jsp 문 update 사용
                        name = etName_user.getText().toString();
                        String result = "";
                        String user_name = etName_user.getText().toString();
                        try {
                            result = new nameTask().execute(user_name).get();
                        } catch (Exception e) {
                            Toast.makeText(SettingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        //유효성 검사

                        if (result.equals("fail")) {
                            Toast.makeText(SettingActivity.this, "사용자 이름이 중복 됩니다.", Toast.LENGTH_SHORT).show();
                            etName_user.requestFocus();
                            return;
                        }
                        if (etName_user.getText().toString().length() == 0) {
                            Toast.makeText(SettingActivity.this,
                                    "사용자 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                            etName_user.requestFocus();
                            return;
                        }

                        try {
                            new UserInsert().execute(name, Authority, pass, ent).get();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getData("http://52.78.72.111:8080/setup_member_info.jsp?id=" + id);
                                }
                            }, 500);
                        } catch (Exception e) {
                            Toast.makeText(SettingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alt.setNegativeButton(R.string.btn_cancel, null);
                alt.show();
            }
        });

        b = new BackPressCloseHandler(this, getString(R.string.btn_close));

        setting1 = (ImageView) findViewById(R.id.setting1);
        setting2 = (ImageView) findViewById(R.id.setting2);
        setting3 = (ImageView) findViewById(R.id.setting3);

        setting1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setting3.setImageResource(R.drawable.setting2);
                setting2.setImageResource(R.drawable.setting2);
                setting1.setImageResource(R.drawable.setting1);
                rate = "low";

                makeToast(getString(R.string.low));
                try {
                    new IotRate().execute(id, rate).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 500);
            }
        });

        setting2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting3.setImageResource(R.drawable.setting2);
                setting2.setImageResource(R.drawable.setting1);
                setting1.setImageResource(R.drawable.setting2);
                rate = "middle";
                makeToast(getString(R.string.middle));
                try {
                    new IotRate().execute(id, rate).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 500);
            }
        });

        setting3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting3.setImageResource(R.drawable.setting1);
                setting2.setImageResource(R.drawable.setting2);
                setting1.setImageResource(R.drawable.setting2);
                rate = "high";
                makeToast(getString(R.string.high));
                try {
                    new IotRate().execute(id, rate).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 500);
            }
        });

        icon_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icon_check.setImageResource(R.drawable.checkon);
                icon_iot.setImageResource(R.drawable.wifi);
                icon_setting.setImageResource(R.drawable.settingoff);
                stopAsync(g2);
                Intent it = new Intent(SettingActivity.this, CheckActivity.class);
                startActivity(it);
                finish();
            }
        });
        icon_iot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icon_check.setImageResource(R.drawable.check);
                icon_iot.setImageResource(R.drawable.wifion);
                icon_setting.setImageResource(R.drawable.settingoff);
                stopAsync(g2);
                Intent it = new Intent(SettingActivity.this, IotActivity.class);
                startActivity(it);
                finish();
            }
        });
    }

    void stopAsync(AsyncTask a) {
        try {
            if (a.getStatus() == AsyncTask.Status.RUNNING) {
                a.cancel(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void changeAdapter(MyAdapter myAdapter) {
        if (setting.getBoolean("grid2", false)) {
            list2.setAdapter(myAdapter);
            list2.setVisibility(View.VISIBLE);
            lltop.setVisibility(View.GONE);
            list.setVisibility(View.GONE);
        } else {
            list.setAdapter(myAdapter);
            list2.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            lltop.setVisibility(View.VISIBLE);
        }
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        b.onBackPressed();
    }

    void makeToast(String message) {
        Toast.makeText(SettingActivity.this, getString(R.string.setting_sec, message), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            user_name = login.getString("user", "null");
    }

    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(tagResult);
            array.clear();
            JsonArray = jsonObj.getJSONArray(TAG_result);
            for (int i = 0; i < JsonArray.length(); i++) {
                JSONObject c = JsonArray.getJSONObject(i);
                String name = c.getString(TAG_name);
                String authority = c.getString(TAG_authority);
                //String nfc = c.getString(TAG_nfc);
                String password = c.getString(TAG_pass);
                String enter = c.getString(TAG_enter);
                UserInfo userInfo = new UserInfo();
                userInfo.posName = name;
                userInfo.posAuthority = authority;
                userInfo.posPass = password;
                userInfo.posEnter = enter;
                array.add(userInfo);
            }
            myAdapter = new MyAdapter(SettingActivity.this, array);
            changeAdapter(myAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetDataJSON extends AsyncTask<String, Void, String> {
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
                while ((webTag = bufferedReader.readLine()) != null) {
                    sb.append(webTag + "\n");
                }
                return sb.toString().trim();
            } catch (Exception e) {
                Log.d("i", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            tagResult = result;
            showList();
        }
    }

    @Override
    protected void onPause() {
        stopAsync(g2);
        super.onPause();
    }

    GetDataJSON g2;

    public void getData(String url) {
        g2 = new GetDataJSON();
        g2.execute(url);
    }

    @Override
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
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
                SharedPreferences.Editor editor = login.edit();
                editor.clear();
                editor.apply();
                Toast.makeText(SettingActivity.this, getString(R.string.logout), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        ivIcon.setImageResource(R.drawable.settingon);
        ivLetter.setText(R.string.title_setting);
        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar) actionbar.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        return true;
    }

    private class UserInsert extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://52.78.72.111:8080/member_input.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "name=" + strings[0] + "&authority=" + strings[1] + "&password=" + strings[2] + "&enter=" + strings[3] + "&id=" + id;
                osw.write(sendMsg);
                osw.flush();
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
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

    private class MyAdapter extends BaseAdapter {
        Context context;
        ArrayList<UserInfo> posterList;

        private MyAdapter(Context c, ArrayList<UserInfo> l) {
            context = c;
            posterList = l;
        }

        @Override
        public int getCount() { //데이터의 총길이
            return posterList.size(); //동적 할당이므로 length 사용 안함
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
        public View getView(final int position, View convertView, ViewGroup parent) { //표현 방법
            LayoutInflater layoutInflater
                    = LayoutInflater.from(context);
            View view;
            final UserInfo userInfo = posterList.get(position);

            view = layoutInflater.inflate(R.layout.list_item, null);
            LinearLayout llitem_user = (LinearLayout) view.findViewById(R.id.llitem_user);
            //LinearLayout llRight = (LinearLayout) view.findViewById(R.id.llRight);
            LinearLayout lldel=(LinearLayout)view.findViewById(R.id.lldel);

            TextView tvName = (TextView) view.findViewById(R.id.name);
            TextView tvAuthority = (TextView) view.findViewById(R.id.authority);
            //TextView tvNfc = (TextView)view.findViewById(R.id.nfc);
            // Button delete = (Button)view.findViewById(R.id.delete);
            TextView enter = (TextView) view.findViewById(R.id.enter);
            Button delete = (Button) view.findViewById(R.id.delete);
            final String password = userInfo.posPass;
            final String username = userInfo.posName;
            final String authority=userInfo.posAuthority;
            tvName.setText(username);
            tvAuthority.setText(authority);
            //tvNfc.setText(posterList.get(position).posNfc);
            enter.setText(userInfo.getPosEnter());

            if(user_name.equals(username))
                llitem_user.setBackgroundColor(getColor(R.color.color_list_background));

            if (setting.getBoolean("grid2", false)) {
                tvAuthority.setVisibility(View.GONE);
                enter.setVisibility(View.GONE);
                lldel.setVisibility(View.GONE);
                if(authority.equals("관리자"))
                tvName.setTextColor(getColor(R.color.ncolor_orange));
                tvName.setTextSize(20);
            } else{
                tvAuthority.setVisibility(View.VISIBLE);
                enter.setVisibility(View.VISIBLE);
                lldel.setVisibility(View.VISIBLE);
            }
            if(login.getBoolean("admin",false)) {
                if(setting.getBoolean("grid2",false)){
                    llitem_user.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder alt= makeAlt(SettingActivity.this,position,password,authority);
                            alt.show();
                            return false;
                        }
                    });
                }else{
                    tvDel.setVisibility(View.VISIBLE);
                    lldel.setVisibility(View.VISIBLE);
                }
            }
            else{
                tvDel.setVisibility(View.GONE);
                lldel.setVisibility(View.GONE);
            }


            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alt = makeAlt(SettingActivity.this,position,password,authority);
                    alt.show();
                }
            });
            llitem_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(authority.equals("관리자")) {
                        Intent it = new Intent(SettingActivity.this, LoginActivity.class);
                        it.putExtra("user_name", username);
                        it.putExtra("user_pass", password);
                        it.putExtra("user_type", true);
                        startActivityForResult(it, settingactivitycode);
                    }else {
                        editor=login.edit();
                        editor.putBoolean("selected",true);
                        editor.putString("user",username);
                        editor.putBoolean("admin",false);
                        editor.apply();
                        Toast.makeText(SettingActivity.this, getString(R.string.login, username), Toast.LENGTH_SHORT).show();
                        Intent it=new Intent(SettingActivity.this,CheckActivity.class);
                        startActivity(it);
                        finish();
                    }
                }
            });
            //}
            return view;
        }
        AlertDialog.Builder makeAlt(final Context cxt,final int position,final String password,final String authority){
            dialogView = View.inflate(cxt, R.layout.delete_user, null);
            AlertDialog.Builder alt = new AlertDialog.Builder(cxt);
            alt.setTitle(R.string.title_del2);
            alt.setView(dialogView);
            final TextView tvDeleteName = (TextView) dialogView.findViewById(R.id.tvDeleteName);
            tvDeleteName.setText(posterList.get(position).posName);
            final EditText etDeletePass = (EditText) dialogView.findViewById(R.id.etDeletePass);
            etDeletePass.setHint(getString(R.string.signup_check, getString(R.string.signup_pass)));
            alt.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //jsp 문 update 사용
                    if (etDeletePass.getText().toString().equals(password)) {
                        new DeleteTask().execute(tvDeleteName.getText().toString(), etDeletePass.getText().toString(),authority);
                        //sendMsg = "name="+strings[0]+"&authority="+strings[1]+"&password="+strings[2];
                        posterList.remove(position);
                        notifyDataSetChanged();
                    } else if (pw.equals(etDeletePass.getText().toString())) {
                        new DeleteTask().execute(tvDeleteName.getText().toString(), password,authority);
                        //sendMsg = "name="+strings[0]+"&authority="+strings[1]+"&password="+strings[2];
                        posterList.remove(position);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(cxt, getString(R.string.sign_pass_check), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alt.setNegativeButton(R.string.btn_cancel, null);
            return alt;
        }
    }

    private class DeleteTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://52.78.72.111:8080/member_remove.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "name=" + strings[0] + "&password=" + strings[1] + "&id=" + id+"&authority"+strings[2];
                osw.write(sendMsg);
                osw.flush();
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
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


    class nameTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://52.78.72.111:8080/member_check.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "name=" + strings[0] + "&id=" + id;
                osw.write(sendMsg);
                osw.flush();
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
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

    class IotRate extends AsyncTask<String, Void, String> { //값 보내고 받기
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://52.78.72.111:8080/iot_rating.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "id=" + strings[0] + "&rate=" + strings[1];
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

    @Override
    protected void onResume() {
        super.onResume();
        try {
            getData("http://52.78.72.111:8080/setup_member_info.jsp?id=" + id);
        } catch (Exception e) {

        }
    }
}
