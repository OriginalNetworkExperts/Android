package com.oneproject.www.allinone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    String name;
    private String pass;
    EditText etPass;
    SharedPreferences appData;
    SharedPreferences.Editor editor;
    boolean type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.alert_userlogin));
        appData = getSharedPreferences("appData", MODE_PRIVATE);
        TextView tv = (TextView) findViewById(R.id.tvUsername);
        etPass = (EditText) findViewById(R.id.etUser);
        Intent intent = getIntent();
        try {
            name = intent.getStringExtra("user_name");
            pass = intent.getStringExtra("user_pass");
            type = intent.getBooleanExtra("user_type", false);
            tv.setText(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button btnSubmit = (Button) findViewById(R.id.submit);
        Button btnCancel = (Button) findViewById(R.id.cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPass.getText().toString().equals(pass)) {
                    editor = appData.edit();
                    editor.putString("user", name);
                    setResult(RESULT_OK);
                    if (type) {
                        editor.putBoolean("selected",true);
                        Toast.makeText(LoginActivity.this, getString(R.string.login, name), Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(LoginActivity.this, R.string.alert_change, Toast.LENGTH_SHORT).show();
                    editor.putBoolean("admin",true);
                    editor.putString("user",name);
                    editor.apply();
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.sign_pass_check, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
