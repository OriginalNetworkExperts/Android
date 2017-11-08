package com.oneproject.www.allinone;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.oneproject.www.allinone.DBHelper.TABLE_NAME;
import static com.oneproject.www.allinone.DBHelper._CHECK;
import static com.oneproject.www.allinone.DBHelper._CONTEXT;
import static com.oneproject.www.allinone.DBHelper._DATE;
import static com.oneproject.www.allinone.DBHelper._RATE;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    TextToSpeech tts;
    String fnEnter,id;
    PendingIntent pendingIntent;
    Button login_btn;
    TextView join_btn;
    EditText userId, userPwd;
    CheckBox shareId;
    private SharedPreferences appData;
    private String logId;
    private String logPwd;
    private boolean saveLoginData;
    private NfcAdapter nfcAdapter;

    String username;

    DBHelper mHelper;
    SQLiteDatabase db;
    Cursor cursor;

    BackPressCloseHandler b;

    private Boolean isNetWork() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        try {
            if (networkInfo != null && networkInfo.isConnected() || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            } else {
                Toast.makeText(getApplication(), getString(R.string.alert_network), Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b = new BackPressCloseHandler(this, getString(R.string.btn_close));
        userId = (EditText) findViewById(R.id.userId);
        userPwd = (EditText) findViewById(R.id.userPwd);
        login_btn = (Button) findViewById(R.id.login_btn);
        join_btn = (TextView) findViewById(R.id.join_btn);
        shareId = (CheckBox) findViewById(R.id.shareId);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        appData = getSharedPreferences("appData", MODE_PRIVATE);
        id=appData.getString("ID","");
        username = appData.getString("user", "null");
        load();


        tts = new TextToSpeech(getApplicationContext(), this);

        Intent intent1 = getIntent();
        nfcProgress(intent1);

        userId.setText(logId);
        userPwd.setText(logPwd);

        if (isNetWork()) {
            if (saveLoginData) {
                shareId.setChecked(saveLoginData);
                login(logId, logPwd);
            }

        }
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String loginid = userId.getText().toString();
                String loginpwd = userPwd.getText().toString();
                login(loginid, loginpwd);
            }
        });

        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MemberAddActivity.class);
                startActivityForResult(intent, 1000);
            }
        });
    }

    String text1;

    void nfcProgress(Intent intent1) {

            Parcelable[] rawMsgs = intent1.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            int temp_count = 0;
            int i = 0;
            float temp_count2 = 0;
            String temp = "";
            try {
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdfNow1 = new SimpleDateFormat("yyyy");
                SimpleDateFormat sdfNow2 = new SimpleDateFormat("MM");
                SimpleDateFormat sdfNow3 = new SimpleDateFormat("dd");
                String mdate = sdfNow1.format(date) + (Integer.parseInt(sdfNow2.format(date))) + "" + (Integer.parseInt(sdfNow3.format(date)));
                mHelper = new DBHelper(this);
                db = mHelper.getWritableDatabase();

                System.out.println(String.format("select count(*) from %s where %s='%s' and %s='F'", TABLE_NAME, _DATE, mdate, _CHECK));

                cursor = db.rawQuery(String.format("select count(*) from %s where %s='%s' and %s='F'", TABLE_NAME, _DATE, mdate, _CHECK), null);
                while (cursor.moveToNext())
                    temp_count = Integer.parseInt(cursor.getString(0));

                cursor = db.rawQuery(String.format("select %s,%s from %s where %s='%s' and %s='F'", _RATE, _CONTEXT, TABLE_NAME, _DATE, mdate, _CHECK), null);
                while (cursor.moveToNext()) {
                    temp_count2 = Float.parseFloat(cursor.getString(0));
                    if (temp_count2 > 3.5)
                        i++;
                    //temp+=(cursor.getString(1)+"   ");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (rawMsgs != null) {
                NdefMessage msgs = (NdefMessage) rawMsgs[0];
                NdefRecord[] rec = msgs.getRecords();
                byte[] bt = rec[0].getPayload();
                // String text = new String(bt);
                //tv.setText(text);
                //수정//
                boolean temp_bool = false;
                fnEnter = new String(bt);
                text1 = fnEnter;
                if (temp_count > 0) {
                    text1 = "체크리스트를 확인해주세요";
                    temp_bool = true;
                }
                if (i > 0) {
                    //if (temp.length()> 0) {
                    text1 = "중요한 체크리스트가 있습니다";
                    //text1 = temp;
                    temp_bool = true;
                }
                if (fnEnter.equals("외출")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                ttsGreater21(text1);
                            else
                                ttsUnder20(text1);

                        }
                    }, 1000);
                } else {
                    text1 = "출입하였습니다.";
                    temp_bool = true;
                }
                if(appData.getBoolean("not_login",true)) {
                    text1="로그인을 해주세요.";
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                ttsGreater21(text1);
                            else
                                ttsUnder20(text1);

                        }
                    }, 1000);

                    temp_bool = true;
                }
                if (temp_bool)
                    Toast.makeText(getApplicationContext(), text1, Toast.LENGTH_SHORT).show();

                Toast.makeText(getApplicationContext(),id+"   "+username+"   "+fnEnter,Toast.LENGTH_SHORT).show();
                if(fnEnter.equals("외출")){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new intTask().execute(id.trim(), username.trim(), fnEnter.trim());
                        }
                    },5000);
                }
                new intTask().execute(id.trim(), username.trim(), fnEnter.trim());
            }

    }

    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.ERROR) {
            tts.setLanguage(Locale.KOREAN);
        }
    }

    CustomTask c;

    void login(String loginid, String loginpwd) {
        if (isNetWork()) {
            try {
                if (loginid.equals("") || loginpwd.equals((""))) {
                    Toast.makeText(MainActivity.this, getString(R.string.sign_none), Toast.LENGTH_SHORT).show();
                } else {
                    c = new CustomTask();
                    String result = c.execute(loginid, loginpwd).get();
                    if (result.equals("true")) {
                        //Toast.makeText(MainActivity.this, getString(R.string.login, loginid), Toast.LENGTH_SHORT).show();
                        save();
                        FirebaseMessaging.getInstance().subscribeToTopic(loginid + "_token");
                        FirebaseInstanceId.getInstance().getToken();
                        Intent it = new Intent(MainActivity.this, UserActivity.class);
                        it.putExtra("id", loginid);
                        startActivity(it);
                        finish();
                    } else if (result.equals("false") || result.equals("noId")) {
                        Toast.makeText(MainActivity.this, getString(R.string.sign_id_check), Toast.LENGTH_SHORT).show();
                        userId.setText("");
                        userPwd.setText("");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.offline), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        b.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            Toast.makeText(MainActivity.this, getString(R.string.signup_submit), Toast.LENGTH_SHORT).show();
            userId.setText(data.getStringExtra("id"));
        }
    }

    private class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://52.78.72.111:8080/loginCheck.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "id=" + strings[0] + "&pw=" + strings[1];
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

    private void save() {
        SharedPreferences.Editor editor = appData.edit();
        //editor.putBoolean("SAVE_LOGIN_DATA", shareId.isChecked());
        editor.putBoolean("SAVE_LOGIN_DATA", true);
        editor.putBoolean("not_login",false);
        editor.putString("ID", userId.getText().toString().trim());
        editor.putString("PWD", userPwd.getText().toString().trim());
        editor.apply();
        c.cancel(true);
    }

    private void load() {
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        if(saveLoginData) {
            logId = appData.getString("ID", "");
            logPwd = appData.getString("PWD", "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        nfcProgress(intent);
    }

    class intTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://52.78.72.111:8080/member_enterence.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "id=" + strings[0] + "&name=" + strings[1]+"&enter="+strings[2];
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
    protected void onDestroy() {
        super.onDestroy();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (tts != null) {
                    tts.stop();
                    tts.shutdown();
                }
            }
        }, 3000);
    }


    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId = this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }


}
