package com.oneproject.www.allinone;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WarningActivity extends AppCompatActivity {
    WebView wvWarning;
    Button btnSubmit;
    TextView warn_time,warn_name,warn_content;

    SharedPreferences s;

    EditText etUrl;
    Button btnUrl;


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
       btn_logout.setVisibility(View.GONE);
        ivIcon.setImageResource(R.drawable.wifion);
        ivLetter.setText(R.string.title_warning);
        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar) actionbar.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        return true;
    }
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);

        Intent it=getIntent();
        String name=it.getStringExtra("iot_name");
        String num=it.getStringExtra("iot_num");
        s=getSharedPreferences("appData", Activity.MODE_PRIVATE);
        wvWarning=(WebView)findViewById(R.id.wvWarning);

        etUrl=(EditText)findViewById(R.id.etUrl);
        btnUrl=(Button)findViewById(R.id.btnUrl);
        url=s.getString("ip","");
        btnUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url=etUrl.getText().toString();
                wvWarning.loadUrl("http://"+url+"/stream");
            }
        });

        wvWarning.getSettings().setJavaScriptEnabled(true);
        wvWarning.loadUrl("http://"+url+"/stream");
        //192.168.0.31
        wvWarning.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                WarningActivity.this.setProgress(progress * 100);
            }
        });
        wvWarning.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String fallingUrl) {
                Toast.makeText(WarningActivity.this, "로딩오류"+description, Toast.LENGTH_SHORT).show();
            }
        });

        btnSubmit=(Button)findViewById(R.id.btnSubmit_warning);

        warn_name=(TextView)findViewById(R.id.warn_name);
        warn_time=(TextView)findViewById(R.id.warn_time);
        warn_content=(TextView)findViewById(R.id.warn_content);

        warn_name.setText(name);
        warn_content.setText(num);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] items=new CharSequence[]{"신고하기","괜찮습니다"};
                AlertDialog.Builder alt=new AlertDialog.Builder(WarningActivity.this);
                alt.setTitle(R.string.title_judge);
                alt.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                Intent it=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"));
                                startActivity(it);
                                //112
                                break;
                            case 1:
                                finish();
                                break;
                        }
                    }
                });
                alt.show();
            }
        });
    }
    //class active ->0
}
