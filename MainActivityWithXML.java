package com.example.hhp.android_practice1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    Switch st1;
    LinearLayout rayout1;
    CheckBox dogcheck, catcheck, racheck;
    ImageView dogimg, catimg, raimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        st1 = (Switch)findViewById(R.id.st1);
        rayout1 = (LinearLayout)findViewById(R.id.rayout1);
        dogcheck = (CheckBox)findViewById(R.id.dogcheck);
        catcheck = (CheckBox)findViewById(R.id.catcheck);
        racheck = (CheckBox)findViewById(R.id.racheck);
        dogimg = (ImageView)findViewById(R.id.dogimg);
        catimg = (ImageView)findViewById(R.id.catimg);
        raimg = (ImageView)findViewById(R.id.raimg);

        st1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    rayout1.setVisibility(View.VISIBLE);
                else
                    rayout1.setVisibility(View.INVISIBLE);
            }
        });

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
