package com.oneproject.www.allinone;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
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

public class UserActivity extends AppCompatActivity {
    ListView lvUser;
    ImageView ivPlus;

    final static int useractivitycode=999;


    MyAdapter myAdapter;

    String tagResult;
    private static final String TAG_result = "result";
    private static final String TAG_authority = "authority";
    private static final String TAG_name = "name";
    private static final String TAG_pass = "password";
    //private static final String TAG_nfc = "nfc";
    private static final String TAG_enter = "enter";
    JSONArray JsonArray = null;
    final ArrayList<UserInfo> array = new ArrayList<>();
    ArrayList<HashMap<String, String>> personList;
    SharedPreferences login;
    SharedPreferences.Editor editor;
    String id,pw;

    View dialogView;
    EditText etName_user;
    String name,pass,Authority,ent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        lvUser=(ListView)findViewById(R.id.lvUser);
        ivPlus=(ImageView)findViewById(R.id.dataplus);
        login = getSharedPreferences("appData", Activity.MODE_PRIVATE);

        pw = login.getString("PWD", "");
        id = login.getString("ID", "");
        personList = new ArrayList<HashMap<String, String>>();
        editor=login.edit();
        try {
            getData("http://52.78.72.111:8080/setup_member_info.jsp?id=" + id);
        } catch (Exception e) {

        }
        if(login.getBoolean("selected",false)){
            Toast.makeText(UserActivity.this, getString(R.string.login, login.getString("user","")), Toast.LENGTH_SHORT).show();
            Intent it=new Intent(UserActivity.this,CheckActivity.class);
            startActivity(it);
            finish();
        }
        ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogView = View.inflate(UserActivity.this, R.layout.alt_user, null);
                AlertDialog.Builder alt = new AlertDialog.Builder(UserActivity.this);
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
                            result  = new nameTask().execute(user_name).get();
                        } catch (Exception e) {
                            Toast.makeText(UserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        //유효성 검사

                        if (result.equals("fail")) {
                            Toast.makeText(UserActivity.this, "사용자 이름이 중복 됩니다.", Toast.LENGTH_SHORT).show();
                            etName_user.requestFocus();
                            return;
                        }
                        if (etName_user.getText().toString().length() == 0) {
                            Toast.makeText(UserActivity.this,
                                    "사용자 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                            etName_user.requestFocus();
                            return;
                        }

                            try {
                                new UserInsert().execute(name, Authority, pass).get();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getData("http://52.78.72.111:8080/setup_member_info.jsp?id=" + id);
                                    }
                                }, 500);
                            } catch (Exception e) {
                                Toast.makeText(UserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alt.setNegativeButton(R.string.btn_cancel, null);
                alt.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        editor.clear();
        editor.apply();
        Intent it=new Intent(UserActivity.this,MainActivity.class);
        startActivity(it);
        finish();
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
            myAdapter = new MyAdapter(UserActivity.this, array);
            lvUser.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();
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
                sendMsg = "name=" + strings[0] + "&authority=" + strings[1] + "&password=" + strings[2] + "&id=" + id;
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
    void stopAsync(AsyncTask a) {
        try {
            if (a.getStatus() == AsyncTask.Status.RUNNING) {
                a.cancel(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialogView = View.inflate(UserActivity.this, R.layout.delete_user, null);
                    AlertDialog.Builder alt = new AlertDialog.Builder(UserActivity.this);
                    alt.setTitle("사용자 삭제");
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
                                Toast.makeText(UserActivity.this, "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    alt.setNegativeButton(R.string.btn_cancel, null);
                    alt.show();
                }
            });
            llitem_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(authority.equals("관리자")) {
                        Intent it = new Intent(UserActivity.this, LoginActivity.class);
                        it.putExtra("user_name", username);
                        it.putExtra("user_pass", password);
                        it.putExtra("user_type", true);
                        startActivityForResult(it, useractivitycode);
                    }else {
                        Toast.makeText(UserActivity.this, getString(R.string.login, username), Toast.LENGTH_SHORT).show();
                        editor.putBoolean("selected",true);
                        editor.putString("user",username);
                        editor.apply();
                        Intent it=new Intent(UserActivity.this,CheckActivity.class);
                        startActivity(it);
                        finish();
                    }
                }
            });
            //}
            return view;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            startActivity(new Intent(UserActivity.this, CheckActivity.class));
            finish();
        }
    }
}
