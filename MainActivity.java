package com.example.hhp.andoroid_practice2_onlyjavacode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import static com.example.hhp.andoroid_practice2_onlyjavacode.R.drawable.dog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout.LayoutParams paramsMPMP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams paramsWCWC = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        final LinearLayout mainLayout = new LinearLayout(this);
        setContentView(mainLayout,paramsMPMP);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        TextView startmsg = new TextView(this);
        startmsg.setText("선택을 시작하겠습니까?");
        startmsg.setTextSize(25);
        mainLayout.addView(startmsg);

        Switch st1 = new Switch(this);
        mainLayout.addView(st1);



        final LinearLayout subLayout = new LinearLayout(this);
        subLayout.setLayoutParams(paramsWCWC);
        mainLayout.addView(subLayout);
        //setContentView(subLayout,paramsMPMP); 자바 내부에서 불러오는건 1개만 가능?
        subLayout.setOrientation(LinearLayout.VERTICAL);
        subLayout.setVisibility(View.INVISIBLE);

        st1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    subLayout.setVisibility(View.VISIBLE);//final안하면 안됨. 왜?
                else
                    subLayout.setVisibility(View.INVISIBLE);
            }
        });

            TextView like = new TextView(this);
            like.setText("좋아하는 동물은 무엇입니까?");
            subLayout.addView(like);

            CheckBox dogcheck = new CheckBox(this);
            dogcheck.setText("시바");
            subLayout.addView(dogcheck);

            CheckBox catcheck = new CheckBox(this);
            catcheck.setText("스코티시폴드");
            subLayout.addView(catcheck);

            CheckBox racheck = new CheckBox(this);
            racheck.setText("너굴맨");
            subLayout.addView(racheck);

        LinearLayout imgLayout = new LinearLayout(this);
        imgLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(imgLayout);

        LinearLayout.LayoutParams paramsIMG = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1);

            final ImageView dogimg = new ImageView(this);
            dogimg.setLayoutParams(paramsIMG);
            dogimg.setImageResource(dog);
            dogimg.setVisibility(View.INVISIBLE);
            imgLayout.addView(dogimg);

            final ImageView catimg = new ImageView(this);
            catimg.setLayoutParams(paramsIMG);
            catimg.setImageResource(R.drawable.ca);
            catimg.setVisibility(View.INVISIBLE);
            imgLayout.addView(catimg);

            final ImageView raimg = new ImageView(this);
            raimg.setLayoutParams(paramsIMG);
            raimg.setImageResource(R.drawable.ra);
            raimg.setVisibility(View.INVISIBLE);
            imgLayout.addView(raimg);

        dogcheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    dogimg.setVisibility(View.VISIBLE);
                else
                    dogimg.setVisibility(View.INVISIBLE);
            }
        });

        catcheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    catimg.setVisibility(View.VISIBLE);
                else
                    catimg.setVisibility(View.INVISIBLE);
            }
        });

        racheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    raimg.setVisibility(View.VISIBLE);
                else
                    raimg.setVisibility(View.INVISIBLE);
            }
        });
    }
}
