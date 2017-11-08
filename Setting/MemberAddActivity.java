package com.oneproject.www.allinone;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class MemberAddActivity extends AppCompatActivity {
    private TextView tvConfirm;
    private EditText etPassword;
    private EditText etPasswordConfirm;
    private EditText etName;
    //private EditText etBirth;
    private EditText etAddress;
    private EditText etPhone;
    private Button btJoin;
    private Button btCheck;
    private EditText etId;
    private EditText etMail;
    private TextView tvYear;
    private TextView tvMonth;
    private TextView tvDay;
    private LinearLayout Layyear;

    private boolean myPassCheck = false;
    private boolean myIdCheck = false;
    static final int DATE_DIALOG_ID = 0;

    public int year,month,day;
    private int mYear, mMonth, mDay;

    public MemberAddActivity()    {
        final Calendar cc = Calendar.getInstance();
        mYear = cc.get(Calendar.YEAR);
        mMonth = cc.get(Calendar.MONTH);
        mDay = cc.get(Calendar.DAY_OF_MONTH);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_add);
        tvConfirm =(TextView)findViewById(R.id.tvpassCheck);
        etPassword = (EditText) findViewById(R.id.etPass);
        etPasswordConfirm = (EditText) findViewById(R.id.etPass2);
        etName = (EditText) findViewById(R.id.etName);
        //etBirth = (EditText) findViewById(R.id.etBirth);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etPhone = (EditText) findViewById(R.id.etPhone);
        btJoin = (Button) findViewById(R.id.btnJoin);
        btCheck = (Button) findViewById(R.id.btCheck);
        etId = (EditText)findViewById(R.id.etId);
        etMail = (EditText)findViewById(R.id.etMail);
        tvYear =(TextView) findViewById(R.id.tvYear);
        tvMonth =(TextView)findViewById(R.id.tvMonth);
        tvDay =(TextView)findViewById(R.id.tvDay);
        Layyear = (LinearLayout)findViewById(R.id.Layyear);

        Layyear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        btCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginid = etId.getText().toString();
                try {
                    String result  = new CustomTask().execute(loginid).get();
                    if(result.equals("fail")) {
                        Toast.makeText(MemberAddActivity.this,getString(R.string.sign_id),Toast.LENGTH_SHORT).show();
                        btCheck.setBackgroundResource(R.drawable.dayofweek_back2);
                        myIdCheck = true;
                    } else if(result.equals("success")) {
                        Toast.makeText(MemberAddActivity.this,getString(R.string.sign_id_overap),Toast.LENGTH_SHORT).show();
                        myIdCheck = false;
                    }else{
                        Toast.makeText(MemberAddActivity.this,getString(R.string.sign_id_none),Toast.LENGTH_SHORT).show();
                        myIdCheck = false;
                    }
                }catch (Exception e) {}
            }
        });

        etId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btCheck.setBackgroundResource(R.drawable.dayofweek_back);
                myIdCheck=false;
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = etPassword.getText().toString();
                String confirm = etPasswordConfirm.getText().toString();
                if (!password.equals("")&&password.equals(confirm)) {
                    myPassCheck = true;
                    tvConfirm.setText("비밀번호가 일치 합니다.");
                    tvConfirm.setTextColor(Color.GREEN);

                } else {
                    myPassCheck = false;
                    tvConfirm.setText("비밀번호를 확인하세요.");
                    tvConfirm.setTextColor(Color.RED);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(myIdCheck == false){
                    Toast.makeText(MemberAddActivity.this, "아이디 중복검사를 실시해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    // 아이디 입력 확인
                    if (etId.getText().toString().length() == 0) {
                        Toast.makeText(MemberAddActivity.this,
                                getString(R.string.signup_check,getString(R.string.signup_id)), Toast.LENGTH_SHORT).show();
                        etId.requestFocus();
                        return;
                    }
                    // 비밀번호 입력 확인
                    if (etPassword.getText().toString().length() == 0) {
                        Toast.makeText(MemberAddActivity.this,
                                getString(R.string.signup_check,getString(R.string.signup_pass)), Toast.LENGTH_SHORT).show();
                        etPassword.requestFocus();
                        return;
                    }
                    // 비밀번호 확인 입력 확인
                    if (etPasswordConfirm.getText().toString().length() == 0) {
                        Toast.makeText(MemberAddActivity.this,
                                getString(R.string.signup_check,getString(R.string.signup_pass2)), Toast.LENGTH_SHORT).show();
                        etPasswordConfirm.requestFocus();
                        return;
                    }
                    // 비밀번호 일치 확인
                    if (!myPassCheck) {
                        Toast.makeText(MemberAddActivity.this, "비밀번호가 일치하지 않습니다!", Toast.LENGTH_SHORT).show();
                        etPassword.setText("");
                        etPasswordConfirm.setText("");
                        etPassword.requestFocus();
                        return;
                    }
                    // 이름
                    if(etName.getText().toString().length() == 0){
                        Toast.makeText(MemberAddActivity.this,
                                getString(R.string.signup_check,getString(R.string.signup_name)), Toast.LENGTH_SHORT).show();
                        etName.setText("");
                        etName.requestFocus();
                        return;
                    }
                    //년 월 일 추가

                    if(tvYear.getText().toString().length() == 0){
                        Toast.makeText(MemberAddActivity.this,
                                getString(R.string.signup_check,getString(R.string.signup_birth)), Toast.LENGTH_SHORT).show();
                        tvYear.requestFocus();
                        return;
                    }
                    // 주소
                    if(etAddress.getText().toString().length() == 0){
                        Toast.makeText(MemberAddActivity.this,
                                getString(R.string.signup_check,getString(R.string.signup_address)), Toast.LENGTH_SHORT).show();
                        etAddress.setText("");
                        etAddress.requestFocus();
                        return;
                    }
                    // 전화
                    if(etPhone.getText().toString().length() == 0){
                        Toast.makeText(MemberAddActivity.this,
                                getString(R.string.signup_check,getString(R.string.signup_phone)), Toast.LENGTH_SHORT).show();
                        etPhone.setText("");
                        etPhone.requestFocus();
                        return;
                    }
                    // 이메일
                    if(etMail.getText().toString().length() == 0){
                        Toast.makeText(MemberAddActivity.this,
                                getString(R.string.signup_check,getString(R.string.signup_email)), Toast.LENGTH_SHORT).show();
                        etMail.setText("");
                        etMail.requestFocus();
                        return;
                    }

                    //생일
                /*
                if(etBirth.getText().toString().length() == 0){
                    Toast.makeText(MainActivity.this, "생년월일을 입력하세요!", Toast.LENGTH_SHORT).show();
                    etBirth.setText("");
                    etBirth.requestFocus();
                    return;
                }*/



                    String id = etId.getText().toString();
                    String pass = etPassword.getText().toString();
                    String name = etName.getText().toString();
                    String address = etAddress.getText().toString();
                    String mail = etMail.getText().toString();
                    //String birth = etBirth.getText().toString();
                    String phone = etPhone.getText().toString();
                    String year = tvYear.getText().toString();
                    String month = tvMonth.getText().toString();
                    String day = tvDay.getText().toString();

                    new memJoin().execute(id,pass,name,address,phone,mail,year,month,day);
                    Intent result = new Intent();
                    String successId = etId.getText().toString();
                    result.putExtra("id", successId);
                    // 자신을 호출한 Activity로 데이터를 보낸다.
                    setResult(RESULT_OK, result);
                    finish();
                }
            }
        });
    }

    class memJoin extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;
        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                //URL url = new URL("http://10.0.2.2:8080/hello/join.jsp");
                // http://localhost:8080/hello/idconfirm.jsp
                URL url = new URL("http://52.78.72.111:8080/user_input2.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "id="+strings[0]+"&pw="+strings[1]+"&name="+strings[2]
                        +"&address="+strings[3]+"&phone="+strings[4]+"&email="+strings[5]
                        +"&year="+strings[6]+"&month="+strings[7]+"&day="+strings[8];
                osw.write(sendMsg);
                osw.flush();
                if(conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                } else {
                    Log.i("통신 결과", conn.getResponseCode()+"에러");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }
    }

    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;
        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                //URL url = new URL("http://10.0.2.2:8080/hello/idconfirm.jsp");
                // http://localhost:8080/hello/idconfirm.jsp
                URL url = new URL("http://52.78.72.111:8080/user_regit_idcheck.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "id="+strings[0];
                osw.write(sendMsg);
                osw.flush();
                if(conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                } else {
                    Log.i("통신 결과", conn.getResponseCode()+"에러");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }
    }


    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int yearSelected, int monthOfYear, int dayOfMonth) {
            year = yearSelected;
            month = monthOfYear;
            day = dayOfMonth;

            tvYear.setText(new StringBuilder().append(year));
            tvMonth.setText(new StringBuilder().append(month+1));
            tvDay.setText(new StringBuilder().append(day));
        }
    };


    @Override
    protected Dialog onCreateDialog(int id)    {
        switch (id)        {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,mDateSetListener,mYear,mMonth,mDay);
        }
        return null;
    }

}

