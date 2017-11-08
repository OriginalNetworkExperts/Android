package com.oneproject.www.allinone;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//android->java로 변경됨
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.oneproject.www.allinone.DBHelper.TABLE_NAME;
import static com.oneproject.www.allinone.DBHelper._CHECK;
import static com.oneproject.www.allinone.DBHelper._COLOR;
import static com.oneproject.www.allinone.DBHelper._CONTEXT;
import static com.oneproject.www.allinone.DBHelper._DATE;
import static com.oneproject.www.allinone.DBHelper._DAYOFWEEK;
import static com.oneproject.www.allinone.DBHelper._FAVORITE;
import static com.oneproject.www.allinone.DBHelper._ID;
import static com.oneproject.www.allinone.DBHelper._NOTE;
import static com.oneproject.www.allinone.DBHelper._RATE;

public class CheckActivity extends AppCompatActivity {

    final static int checkactivitycode = 1001;
    ListView lvitem1;
    GridView gvItem;
    BackPressCloseHandler b;
    ImageView icon_check, icon_iot, icon_setting;
    ImageView chPlus, btnMenu, btnFavorite;
    View dialogView;

    RadioButton lvList, lvIcon;
    //캘린더
    TextView txtdate;
    CheckBox cbAll;
    EditText edt;
    Button ybtn, tbtn;

    MyCursorAdapter myAdapter;

    //MyCursorAdapter2 myAdapter2;

    DBHelper mHelper;
    SQLiteDatabase db;
    Cursor cursor;

    // Test
    //TextView test;
    //TextView tvTest;
    SharedPreferences setting;
    SharedPreferences.Editor editor;
    int mYear, mMonth, mDay;
    String mDayofWeek = "";
    String mdate;
    String user;

    Fragment top_weather,top_api,top_api2;

    Handler mHandler;
    Runnable r;

    String mDayofWeek2;
    long now;

    ViewPager vp;

    ArrayList<Favorite_check> favorite_checks;
    MyCursorAdapter3 myCursorAdapter3;
    boolean fav = false;
    String fav_s = "F";
    Cursor cursor_fav;

    boolean rain = false;

    PageAdapter pageAdapter;


    @Override
    public boolean supportRequestWindowFeature(int featureId) {
        return super.supportRequestWindowFeature(featureId);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
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
        ImageView ivHelp = (ImageView) actionbar.findViewById(R.id.help);
        TextView ivLetter = (TextView) actionbar.findViewById(R.id.title_letter);
        Button btn_logout = (Button) actionbar.findViewById(R.id.logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logout
                Intent intent = new Intent(CheckActivity.this, MainActivity.class);
                startActivity(intent);
                SharedPreferences auto = getSharedPreferences("appData", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = auto.edit();
                editor.clear();
                editor.apply();
                Toast.makeText(CheckActivity.this, getString(R.string.logout), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        ivHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
            }
        });
        ivIcon.setImageResource(R.drawable.checkon);
        ivLetter.setText(R.string.title_check);
        actionBar.setCustomView(actionbar);
        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar) actionbar.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        return true;
    }

    SharedPreferences login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
//        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        //      setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.settingon);
        login = getSharedPreferences("appData", Activity.MODE_PRIVATE);
        user = login.getString("ID", "");

        now = System.currentTimeMillis();
        favorite_checks = new ArrayList<>();
        //tvTest = (TextView) findViewById(R.id.tvtest);
        b = new BackPressCloseHandler(this, getString(R.string.btn_close));

        top_weather=new Top_Weather();
        top_api=new Top_Api();
        top_api2=new Top_Api2();

        cbAll = (CheckBox) findViewById(R.id.checkall);

        lvIcon = (RadioButton) findViewById(R.id.ivIcon);
        lvList = (RadioButton) findViewById(R.id.ivlist);

        vp = (ViewPager) findViewById(R.id.vp);
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        vp.setAdapter(pageAdapter);

        mHandler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                int pos;
                int temp = vp.getCurrentItem();
                if (temp == 0)
                    pos = 1;
                else
                    pos = 0;
                //Toast.makeText(CheckActivity.this,pos+"",Toast.LENGTH_SHORT).show();
                vp.setCurrentItem(pos);
                // mHandler.postDelayed(r, 5000);
            }
        };
        mHandler.postDelayed(r, 8000);

        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();
        //user = setting.getString("user", "test");

        //bot=(RelativeLayout)findViewById(R.id.relativeLayout10);

        btnMenu = (ImageView) findViewById(R.id.btnMenu);
        btnFavorite = (ImageView) findViewById(R.id.btnFavorite);

        txtdate = (TextView) findViewById(R.id.txtdate);
        chPlus = (ImageView) findViewById(R.id.chPlus);

        ybtn = (Button) findViewById(R.id.yesterday_btn);
        tbtn = (Button) findViewById(R.id.tomorrow_btn);


        ybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                now = now - 1000 * 60 * 60 * 24;
                getDate_check();
            }
        });

        tbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                now = now + 1000 * 60 * 60 * 24;
                getDate_check();
            }
        });

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow4 = new SimpleDateFormat("E");

        mDayofWeek = sdfNow4.format(date);
        english(mDayofWeek);
        //android->java로 수정됨
        Calendar cal = new java.util.GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        updateNow();


        // String temp_context = "", temp_dayofweek = "";
        try {
            mHelper = new DBHelper(this);
            db = mHelper.getWritableDatabase();
            //Toast.makeText(getApplicationContext(),"test",Toast.LENGTH_SHORT).show();
            //settempCursor(temp_context,temp_dayofweek);
            String query = "select * from " + TABLE_NAME + " where date='" + mdate + "' or dayofweek like '%" + mDayofWeek2 + "%'";
            cursor = db.rawQuery(query, null);
            myAdapter = new MyCursorAdapter(this, cursor);

            //myAdapter2 = new MyCursorAdapter2(this, cursor2);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e + "", Toast.LENGTH_SHORT).show();
        }
        gvItem = (GridView) findViewById(R.id.gvCheck);
        lvitem1 = (ListView) findViewById(R.id.lvItem1);

        lvIcon.setChecked(setting.getBoolean("grid", false));

        lvIcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (lvIcon.isChecked()) {
                    editor.putBoolean("grid", true);
                    gvItem.setVisibility(View.VISIBLE);
                    lvitem1.setVisibility(View.GONE);
                } else {
                    editor.putBoolean("grid", false);
                    gvItem.setVisibility(View.GONE);
                    lvitem1.setVisibility(View.VISIBLE);
                }
                editor.apply();
                changeAdapter();
            }
        });
        changeAdapter();

        //lvitem2 = (ListView) findViewById(R.id.lvItem2);
        //lvitem2.setAdapter(myAdapter2);

        icon_check = (ImageView) findViewById(R.id.check);
        icon_iot = (ImageView) findViewById(R.id.iot);
        icon_setting = (ImageView) findViewById(R.id.setting);

        txtdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CheckActivity.this, mDateSetListener, mYear, mMonth, mDay).show();
            }
        });
        chPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogView = View.inflate(CheckActivity.this, R.layout.chlist, null);
                AlertDialog.Builder alt = new AlertDialog.Builder(CheckActivity.this);
                alt.setView(dialogView);
                //alt.setTitle("목록추가");
                LinearLayout llcheck = (LinearLayout) dialogView.findViewById(R.id.llcheck);
                final LinearLayout llitem = (LinearLayout) dialogView.findViewById(R.id.llitems);
                final CheckBox cb = (CheckBox) dialogView.findViewById(R.id.cb);
                final ImageView btnFav = (ImageView) dialogView.findViewById(R.id.btnFav);
                TextView tvColor = (TextView) dialogView.findViewById(R.id.tvcolor_test);
                final RatingBar rating = (RatingBar) dialogView.findViewById(R.id.rating);
                cb.setFocusable(false);
                cb.setClickable(false);
                llcheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (llitem.getVisibility() == View.GONE) {
                            llitem.setVisibility(View.VISIBLE);
                            cb.setChecked(true);
                        } else {
                            llitem.setVisibility(View.GONE);
                            cb.setChecked(false);
                        }
                    }
                });
                btnFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fav = !fav;
                        if (fav) {
                            fav_s = "T";
                            btnFav.setImageResource(R.drawable.favorite_check);
                        } else {
                            fav_s = "F";
                            btnFav.setImageResource(R.drawable.favorite);
                        }

                    }
                });
                final ToggleButton btn_Sun = (ToggleButton) dialogView.findViewById(R.id.btnSun);
                final ToggleButton btn_Mon = (ToggleButton) dialogView.findViewById(R.id.btnMon);
                final ToggleButton btn_Tue = (ToggleButton) dialogView.findViewById(R.id.btnTue);
                final ToggleButton btn_Wed = (ToggleButton) dialogView.findViewById(R.id.btnWed);
                final ToggleButton btn_Thu = (ToggleButton) dialogView.findViewById(R.id.btnThu);
                final ToggleButton btn_Fri = (ToggleButton) dialogView.findViewById(R.id.btnFri);
                final ToggleButton btn_Sat = (ToggleButton) dialogView.findViewById(R.id.btnSat);
                toggleclick(btn_Sun, 2);
                toggleclick(btn_Mon, 0);
                toggleclick(btn_Tue, 0);
                toggleclick(btn_Wed, 0);
                toggleclick(btn_Thu, 0);
                toggleclick(btn_Fri, 0);
                toggleclick(btn_Sat, 1);

                final RadioButton btn_Black = (RadioButton) dialogView.findViewById(R.id.btnBlack);
                final RadioButton btn_Blue = (RadioButton) dialogView.findViewById(R.id.btnBue);
                final RadioButton btn_SBlue = (RadioButton) dialogView.findViewById(R.id.btnSBlue);
                final RadioButton btn_Red = (RadioButton) dialogView.findViewById(R.id.btnRed);
                final RadioButton btn_Orange = (RadioButton) dialogView.findViewById(R.id.btnOrange);
                final RadioButton btn_Green = (RadioButton) dialogView.findViewById(R.id.btnGreen);
                final RadioButton colors[] = {btn_Black, btn_Blue, btn_SBlue, btn_Red, btn_Orange, btn_Green};
                colorclick(colors[0], R.color.ncolor_black, tvColor);
                colorclick(colors[1], R.color.ncolor_blue, tvColor);
                colorclick(colors[2], R.color.ncolor_skyblue, tvColor);
                colorclick(colors[3], R.color.ncolor_red, tvColor);
                colorclick(colors[4], R.color.ncolor_orange, tvColor);
                colorclick(colors[5], R.color.ncolor_green, tvColor);

                alt.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        edt = (EditText) dialogView.findViewById(R.id.edt);
                        String context = edt.getText().toString();
                        if (context.length() > 0) {
                            String query = "";
                            String dayofweek = "";
                            String color = "black";
                            String rate = "";
                            ToggleButton[] toggleButtons = {btn_Sun, btn_Mon, btn_Tue, btn_Wed, btn_Thu, btn_Fri, btn_Sat};
                            String[] strings = {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};
                            String[] colors_str = {"black", "blue", "skyblue", "red", "orange", "green"};
                            for (int j = 0; j < 7; j++) {
                                if (toggleButtons[j].isChecked()) {
                                    if (dayofweek.length() > 0)
                                        dayofweek += "_";
                                    dayofweek += strings[j];
                                }
                            }
                            for (int k = 0; k < colors.length; k++) {
                                if (colors[k].isChecked())
                                    color = colors_str[k];
                            }
                            rate = String.valueOf(rating.getRating());
                            //Toast.makeText(getApplicationContext(), dayofweek, Toast.LENGTH_SHORT).show();
                            query = "insert into " + TABLE_NAME + " values(" +
                                    "null, '" + user + "' , '" + mdate + "','F','" + context + "','" + dayofweek + "','" + fav_s + "','" + color + "'," +
                                    "'" + rate + "','');";
                            // tvtest.setText(query + "");
                            exeQuery(query);
                            refreshDB();

                        } else {
                            Toast.makeText(getApplicationContext(), "내용을 입력하십시오.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
                alt.setNegativeButton("취소", null);

                alt.show();

            }
        });
        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String query;
                if (cbAll.isChecked())
                    query = String.format("update %s set %s='T' where %s='%s'", TABLE_NAME, _CHECK, _DATE, mdate);
                else
                    query = String.format("update %s set %s='F' where %s='%s'", TABLE_NAME, _CHECK, _DATE, mdate);
                exeQuery(query);
                refreshDB();
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(CheckActivity.this, ListActivity.class);
                startActivityForResult(it, checkactivitycode);
            }
        });
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogView = View.inflate(CheckActivity.this, R.layout.alt_favortie, null);
                AlertDialog.Builder alt = new AlertDialog.Builder(CheckActivity.this);
                alt.setView(dialogView);
                alt.setTitle("즐겨찾기 목록");
                ListView lvItem_user = (ListView) dialogView.findViewById(R.id.lvItem_fav);
                try {
                    cursor_fav = db.rawQuery("select * from Memo where favorite = 'T' and note=''", null);
                    myCursorAdapter3 = new MyCursorAdapter3(getApplication(), cursor_fav);

                } catch (Exception e) {
                    Toast.makeText(CheckActivity.this, e + "", Toast.LENGTH_SHORT).show();
                }
                lvItem_user.setAdapter(myCursorAdapter3);
                alt.setNegativeButton(R.string.btn_cancel, null);
                alt.setPositiveButton(R.string.btn_plus, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int j = 0;
                        while (j < favorite_checks.size()) {
                            if (favorite_checks.get(j).getCheck().equals("T")) {
                                String query = "insert into " + TABLE_NAME + " values(" +
                                        "null, '" + user + "' , '" + mdate + "','F','" + favorite_checks.get(j).getContext() + "','','T','','0','AUTO');";
                                exeQuery(query);
                            }
                            j++;
                        }
                        refreshDB();
                    }
                });
                alt.show();
            }
        });


        icon_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icon_check.setImageResource(R.drawable.checkon);
                icon_iot.setImageResource(R.drawable.wifi);
                icon_setting.setImageResource(R.drawable.settingoff);
            }
        });
        icon_iot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icon_check.setImageResource(R.drawable.check);
                icon_iot.setImageResource(R.drawable.wifion);
                icon_setting.setImageResource(R.drawable.settingoff);
                Intent it = new Intent(CheckActivity.this, IotActivity.class);
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
                Intent it = new Intent(CheckActivity.this, SettingActivity.class);
                startActivity(it);
                finish();
            }
        });
        //현재시간

    }

    void changeAdapter() {
        if (setting.getBoolean("grid", false)) {
            gvItem.setAdapter(myAdapter);
            gvItem.setVisibility(View.VISIBLE);
            lvitem1.setVisibility(View.GONE);
        } else {
            lvitem1.setAdapter(myAdapter);
            gvItem.setVisibility(View.GONE);
            lvitem1.setVisibility(View.VISIBLE);
        }
        refreshDB();
    }

    Cursor tempcursor, tempcursor2;

    public void settempCursor(String temp_context, String temp_dayofweek) {
        try {
            //boolean tempboolean = false;
            int id = 0;
            tempcursor = db.rawQuery("select " + _ID + "," + _CONTEXT + ","
                    + _DAYOFWEEK + "," + _CHECK + " from " + TABLE_NAME + " where " + _DAYOFWEEK + " like '%" + mDayofWeek2 + "%' and date <> " + mdate +
                    " and note =''", null);
            while (tempcursor.moveToNext()) {
                id = tempcursor.getInt(tempcursor.getColumnIndex(_ID));
                temp_context = tempcursor.getString(tempcursor.getColumnIndex(_CONTEXT));
                temp_dayofweek = tempcursor.getString(tempcursor.getColumnIndex(_DAYOFWEEK));
                tempcursor2 = db.rawQuery("select note from Memo where note like '%" + id + "%'", null);
                String tempString = "";
                while (tempcursor2.moveToNext()) {
                    tempString = tempcursor2.getString(tempcursor2.getColumnIndex(_NOTE));
                }
                if (temp_dayofweek.length() > 0 && tempString.length() == 0) {
                    String query = "";
                    query = "insert into " + TABLE_NAME + " values(" +
                            "null, '" + user + "' , '" + mdate + "','F','" + temp_context + "','" + temp_dayofweek + "','F','','','');";
                    exeQuery(query);
                    query = "update Memo set note = 'copied' where _id = " + id;
                    exeQuery(query);
                }
            }

        } catch (Exception e) {
            // tvTest.setText(e + "");
            Toast.makeText(getApplicationContext(), e + "", Toast.LENGTH_SHORT).show();
        }
    }

    public void setListViewHeightBasedOnChildren(AbsListView listView) {
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        //Toast.makeText(CheckActivity.this,totalHeight+"",Toast.LENGTH_SHORT).show();
        ListView temp = (ListView) listView;
        if (!setting.getBoolean("grid", false)) {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                // pre-condition
                return;
            }
            int totalHeight = 0;
            //792 880 하나당88
            // Toast.makeText(getApplicationContext(),listAdapter.getCount()+"",Toast.LENGTH_SHORT).show();
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                //if (totalHeight < maxHeight)
                totalHeight += listItem.getMeasuredHeight();
            }

            if (totalHeight > 704) {
                params.height = totalHeight + (temp.getDividerHeight() * (listAdapter.getCount() - 1));
            } else
                params.height = 704;
        }
        //Toast.makeText(CheckActivity.this,params.height+"",Toast.LENGTH_SHORT).show();
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public void english(String mDayofWeek) {
        switch (mDayofWeek) {
            case "월":
                mDayofWeek2 = "mon";
                break;
            case "화":
                mDayofWeek2 = "tue";
                break;
            case "수":
                mDayofWeek2 = "wed";
                break;
            case "목":
                mDayofWeek2 = "thu";
                break;
            case "금":
                mDayofWeek2 = "fri";
                break;
            case "토":
                mDayofWeek2 = "sat";
                break;
            case "일":
                mDayofWeek2 = "sun";
                break;
            default:
                mDayofWeek2 = mDayofWeek;
                break;
        }
    }

    public void toggleclick(final ToggleButton t, final int day) {
        if (t.isChecked()) {
            t.setBackgroundResource(R.drawable.dayofweek_back2);
            t.setTextColor(getResources().getColor(R.color.color_dayofweek_check));
        } else {
            t.setBackgroundResource(R.drawable.dayofweek_back);
            if (day == 0)
                t.setTextColor(getResources().getColor(R.color.ncolor_black));
            else if (day == 1)
                t.setTextColor(getResources().getColor(R.color.ncolor_blue));
            else if (day == 2)
                t.setTextColor(getResources().getColor(R.color.ncolor_red));
        }
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (t.isChecked()) {
                    t.setBackgroundResource(R.drawable.dayofweek_back2);
                    t.setTextColor(getResources().getColor(R.color.color_dayofweek_check));
                } else {
                    t.setBackgroundResource(R.drawable.dayofweek_back);
                    if (day == 0)
                        t.setTextColor(getResources().getColor(R.color.ncolor_black));
                    else if (day == 1)
                        t.setTextColor(getResources().getColor(R.color.ncolor_blue));
                    else if (day == 2)
                        t.setTextColor(getResources().getColor(R.color.ncolor_red));
                }
            }
        });
    }

    public void colorclick(final RadioButton t, final int r2, final TextView tv) {

        t.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (t.isChecked())
                    tv.setTextColor(getResources().getColor(r2));

            }
        });
    }


    public void refreshDB() {
        String temp_context = "", temp_dayofweek = "";

        //` `settempCursor(temp_context, temp_dayofweek);
        String query = "select * from " + TABLE_NAME + " where date='" + mdate + "'and dayofweek ='' or dayofweek like '%" + mDayofWeek2 + "%'";
        cursor = db.rawQuery(query, null);
        //cursor2 = db.rawQuery("select * from " + TABLE_NAME + " where " + _DAYOFWEEK + " like '%" + mDayofWeek2 + "%' and date = '" + mdate + "'", null);

        myAdapter.changeCursor(cursor);
        //myAdapter2.changeCursor(cursor2);
        if (setting.getBoolean("gird", false))
            setListViewHeightBasedOnChildren(gvItem);
        else
            setListViewHeightBasedOnChildren(lvitem1);
        //setListViewHeightBasedOnChildren(lvitem2);
    }

    public boolean exeQuery(String query) {
        try {
            db.execSQL(query);
            return true;
        } catch (Exception e) {
            Toast.makeText(CheckActivity.this, e + "", Toast.LENGTH_LONG).show();
            //tvTest.append("\n" + e);
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        b.onBackPressed();
    }

    DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                    String temp = mYear + "." + (mMonth + 1) + "." + mDay;
                    try {
                        Date date = dateFormat.parse(temp);
                        now = date.getTime();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e + "", Toast.LENGTH_SHORT).show();
                    }
                    getDate_check();
                }
            };

    void getDate_check() {
        Date date = new Date(now);
        SimpleDateFormat sdfNow1 = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdfNow2 = new SimpleDateFormat("MM");
        SimpleDateFormat sdfNow3 = new SimpleDateFormat("dd");
        SimpleDateFormat sdfNow4 = new SimpleDateFormat("E");
        mYear = Integer.parseInt(sdfNow1.format(date));
        mMonth = Integer.parseInt(sdfNow2.format(date)) - 1;
        mDay = Integer.parseInt(sdfNow3.format(date));
        mDayofWeek = sdfNow4.format(date);
        updateNow();
        refreshDB();
        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        cbAll.setChecked(false);
        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String query;
                if (cbAll.isChecked())
                    query = String.format("update %s set %s='T' where %s='%s'", TABLE_NAME, _CHECK, _DATE, mdate);
                else
                    query = String.format("update %s set %s='F' where %s='%s'", TABLE_NAME, _CHECK, _DATE, mdate);
                exeQuery(query);
                refreshDB();
            }
        });
    }

    void updateNow() {
        english(mDayofWeek);
        txtdate.setText(String.format("%d년 %d월 %d일 %s", mYear, mMonth + 1, mDay, mDayofWeek));

        mdate = String.format("%d%d%d", mYear, mMonth + 1, mDay);
        //listtemp = !listtemp;
        editor.putString("date", mdate);
        editor.putString("dayofweek", mDayofWeek);
        editor.putString("dayofweek2", mDayofWeek2);
        editor.putLong("temp_now", now);
        editor.commit();
    }

    class MyCursorAdapter extends CursorAdapter {
        @SuppressWarnings("deprecation")
        public MyCursorAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v;
            if (setting.getBoolean("grid", false))
                v = inflater.inflate(R.layout.check_item_grid, parent, false);
            else
                v = inflater.inflate(R.layout.check_item, parent, false);
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tvCheck;
            final CheckBox cbCheck;
            tvCheck = (TextView) view.findViewById(R.id.tvCheckItem);
            cbCheck = (CheckBox) view.findViewById(R.id.cbCheck);
            cbCheck.setFocusable(false);
            cbCheck.setClickable(false);
            LinearLayout llitem_1 = (LinearLayout) view.findViewById(R.id.llitem_1);
            final int _id = cursor.getInt(cursor.getColumnIndex(_ID));
            final String _date = cursor.getString(cursor.getColumnIndex(_DATE));
            String _check = cursor.getString(cursor.getColumnIndex(_CHECK));
            final String _context = cursor.getString(cursor.getColumnIndex(_CONTEXT));
            final String _dayofweek = cursor.getString(cursor.getColumnIndex(_DAYOFWEEK));
            final String _fav = cursor.getString(cursor.getColumnIndex(_FAVORITE));
            final String color = cursor.getString(cursor.getColumnIndex(_COLOR));
            final String rate = cursor.getString(cursor.getColumnIndex(_RATE));

            tvCheck.setText(_context);

            int colorRes, colorRes2;
            switch (color) {
                case "black":
                    colorRes = R.color.ncolor_black;
                    colorRes2 = R.color.color_black;
                    break;
                case "blue":
                    colorRes = R.color.ncolor_blue;
                    colorRes2 = R.color.color_blue;
                    break;
                case "skyblue":
                    colorRes = R.color.ncolor_skyblue;
                    colorRes2 = R.color.color_skyblue;
                    break;
                case "red":
                    colorRes = R.color.ncolor_red;
                    colorRes2 = R.color.color_red;
                    break;
                case "orange":
                    colorRes = R.color.ncolor_orange;
                    colorRes2 = R.color.color_orange;
                    break;
                case "green":
                    colorRes = R.color.ncolor_green;
                    colorRes2 = R.color.color_green;
                    break;
                default:
                    colorRes = R.color.ncolor_black;
                    colorRes2 = R.color.color_black;
                    break;
            }
            if (_dayofweek.length() > 1) {
                tvCheck.setTextColor(getResources().getColor(R.color.color_dayofweek));
            }
            if (_check.equals("T")) {
                cbCheck.setChecked(true);
                tvCheck.setTextColor(getResources().getColor(colorRes2));
            } else {
                cbCheck.setChecked(false);
                tvCheck.setTextColor(getResources().getColor(colorRes));
                //tvCheck.setPaintFlags(tvCheck.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
            }
            llitem_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cbCheck.isChecked()) {
                        cbCheck.setChecked(false);
                    } else {
                        cbCheck.setChecked(true);
                    }
                }
            });
            cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    try {
                        String check = "";
                        if (cbCheck.isChecked())
                            check = "T";
                        else
                            check = "F";
                        String query = String.format("update %s " +
                                "set %s='%s'" +
                                " where _id=%s", TABLE_NAME, _CHECK, check, _id);
                        // tvtest.append(query+"\n");
                        exeQuery(query);
                        refreshDB();
                    } catch (Exception e) {
                        // tvtest.append("\n"+e);
                    }
                }
            });
            llitem_1.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder alt = setAlt(CheckActivity.this, _id, _date, _context, _dayofweek, _fav, color, rate);
                    alt.show();
                    return false;
                }
            });
        }

    }

    AlertDialog.Builder setAlt(Context c, final int _id, String _date, final String _context, final String _dayofweek, final String _fav, String color, String rate) {
        dialogView = View.inflate(CheckActivity.this, R.layout.chlist, null);
        AlertDialog.Builder alt = new AlertDialog.Builder(c);
        alt.setView(dialogView);
        LinearLayout llcheck = (LinearLayout) dialogView.findViewById(R.id.llcheck);
        final LinearLayout llitem = (LinearLayout) dialogView.findViewById(R.id.llitems);
        final CheckBox cb = (CheckBox) dialogView.findViewById(R.id.cb);
        final ImageView btnFav = (ImageView) dialogView.findViewById(R.id.btnFav);
        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tvTitle);
        TextView tvColor = (TextView) dialogView.findViewById(R.id.tvcolor_test);
        final RatingBar rating = (RatingBar) dialogView.findViewById(R.id.rating);
        edt = (EditText) dialogView.findViewById(R.id.edt);
        edt.setText(_context);

        rating.setRating(Float.parseFloat(rate));
        tvTitle.setText("목록 수정");
        cb.setFocusable(false);
        cb.setClickable(false);

        if (_fav.equals("T")) {
            btnFav.setImageResource(R.drawable.favorite_check);
            fav = true;
        } else {
            fav = false;
        }
        llcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llitem.getVisibility() == View.GONE) {
                    llitem.setVisibility(View.VISIBLE);
                    cb.setChecked(true);
                } else {
                    llitem.setVisibility(View.GONE);
                    cb.setChecked(false);
                }
            }
        });
        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fav = !fav;
                if (fav) {
                    fav_s = "T";
                    btnFav.setImageResource(R.drawable.favorite_check);
                } else {
                    fav_s = "F";
                    btnFav.setImageResource(R.drawable.favorite);
                }
            }
        });
        final ToggleButton btn_Sun = (ToggleButton) dialogView.findViewById(R.id.btnSun);
        final ToggleButton btn_Mon = (ToggleButton) dialogView.findViewById(R.id.btnMon);
        final ToggleButton btn_Tue = (ToggleButton) dialogView.findViewById(R.id.btnTue);
        final ToggleButton btn_Wed = (ToggleButton) dialogView.findViewById(R.id.btnWed);
        final ToggleButton btn_Thu = (ToggleButton) dialogView.findViewById(R.id.btnThu);
        final ToggleButton btn_Fri = (ToggleButton) dialogView.findViewById(R.id.btnFri);
        final ToggleButton btn_Sat = (ToggleButton) dialogView.findViewById(R.id.btnSat);
        if (_dayofweek.contains("sun"))
            btn_Sun.setChecked(true);
        if (_dayofweek.contains("mon"))
            btn_Mon.setChecked(true);
        if (_dayofweek.contains("tue"))
            btn_Tue.setChecked(true);
        if (_dayofweek.contains("wed"))
            btn_Wed.setChecked(true);
        if (_dayofweek.contains("thu"))
            btn_Thu.setChecked(true);
        if (_dayofweek.contains("fri"))
            btn_Fri.setChecked(true);
        if (_dayofweek.contains("sat"))
            btn_Sat.setChecked(true);
        toggleclick(btn_Sun,2);
        toggleclick(btn_Mon,0);
        toggleclick(btn_Tue,0);
        toggleclick(btn_Wed,0);
        toggleclick(btn_Thu,0);
        toggleclick(btn_Fri,0);
        toggleclick(btn_Sat,1);

        final RadioButton btn_Black = (RadioButton) dialogView.findViewById(R.id.btnBlack);
        final RadioButton btn_Blue = (RadioButton) dialogView.findViewById(R.id.btnBue);
        final RadioButton btn_SBlue = (RadioButton) dialogView.findViewById(R.id.btnSBlue);
        final RadioButton btn_Red = (RadioButton) dialogView.findViewById(R.id.btnRed);
        final RadioButton btn_Orange = (RadioButton) dialogView.findViewById(R.id.btnOrange);
        final RadioButton btn_Green = (RadioButton) dialogView.findViewById(R.id.btnGreen);
        final RadioButton colors[] = {btn_Black, btn_Blue, btn_SBlue, btn_Red, btn_Orange, btn_Green};
        colorclick(colors[0], R.color.ncolor_black, tvColor);
        colorclick(colors[1], R.color.ncolor_blue, tvColor);
        colorclick(colors[2], R.color.ncolor_skyblue, tvColor);
        colorclick(colors[3], R.color.ncolor_red, tvColor);
        colorclick(colors[4], R.color.ncolor_orange, tvColor);
        colorclick(colors[5], R.color.ncolor_green, tvColor);
        int k = 0;
        switch (color) {
            case "black":
                k = 0;
                break;
            case "blue":
                k = 1;
                break;
            case "skyblue":
                k = 2;
                break;
            case "red":
                k = 3;
                break;
            case "orange":
                k = 4;
                break;
            case "green":
                k = 5;
                break;
            default:
                k = 0;
                break;
        }
        colors[k].setChecked(true);

        alt.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String context = edt.getText().toString();
                if (context.length() > 0) {
                    String query = "";
                    String dayofweek = "";
                    String color = "";
                    String rate = "";
                    ToggleButton[] toggleButtons = {btn_Sun, btn_Mon, btn_Tue, btn_Wed, btn_Thu, btn_Fri, btn_Sat};
                    String[] strings = {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};
                    String[] colors_str = {"black", "blue", "skyblue", "red", "orange", "green"};
                    for (int j = 0; j < 7; j++) {
                        if (toggleButtons[j].isChecked()) {
                            if (dayofweek.length() > 0)
                                dayofweek += "_";
                            dayofweek += strings[j];
                        }
                    }
                    for (int k = 0; k < colors.length; k++) {
                        if (colors[k].isChecked())
                            color = colors_str[k];
                    }
                    rate = String.valueOf(rating.getRating());
                    //Toast.makeText(getApplicationContext(), dayofweek, Toast.LENGTH_SHORT).show();

                    //update문으로
                    query = String.format("update %s set " +
                            "%s='%s'," +
                            "%s='%s'," +
                            "%s='%s' , " +
                            "%s='%s' , " +
                            "%s='%s' , " +
                            "%s='' " +
                            "where %s=%s", TABLE_NAME, _CONTEXT, context, _DAYOFWEEK, dayofweek, _FAVORITE, fav_s, _COLOR, color, _RATE, rate, _NOTE, _ID, _id);
                    // tvTest.setText(query + "");
                    exeQuery(query);
                    refreshDB();

                } else {
                    Toast.makeText(getApplicationContext(), "내용을 입력하십시오.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alt.setNeutralButton(R.string.btn_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String query = String.format("delete from %s where %s=%s or %s='%s'", TABLE_NAME, _ID, _id, _NOTE, ("AUTO_" + _id));
                exeQuery(query);
                refreshDB();
                Toast.makeText(getApplicationContext(), getString(R.string.alert_delete), Toast.LENGTH_SHORT).show();
            }
        });
        alt.setNegativeButton(R.string.btn_cancel, null);

        return alt;
    }

    class ViewHolder_fav {
        LinearLayout llfav;
        TextView tvfav;
        TextView tvfav2;
        CheckBox cbfav;
    }

    class MyCursorAdapter3 extends CursorAdapter {
        Context context;

        public MyCursorAdapter3(Context context, Cursor c) {
            super(context, c);
            this.context = context;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.item_alt_favorite, parent, false);
            return v;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder_fav holder_fav;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.item_alt_favorite, parent, false);
                holder_fav = new ViewHolder_fav();
                holder_fav.llfav = (LinearLayout) view.findViewById(R.id.llitem_fav);
                holder_fav.tvfav = (TextView) view.findViewById(R.id.tvfav_item1);
                holder_fav.tvfav2 = (TextView) view.findViewById(R.id.tvfav_item2);
                holder_fav.cbfav = (CheckBox) view.findViewById(R.id.cbfav_item);
                view.setTag(holder_fav);
            }
            //view.setTag(position);
            return super.getView(position, view, parent);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            final ViewHolder_fav holder_fav;
            final Favorite_check favorite_check = new Favorite_check();
            holder_fav = (ViewHolder_fav) view.getTag();
            //final int postion=(Integer)view.getTag();
            final int postion = cursor.getPosition();
            String _context = cursor.getString(cursor.getColumnIndex(_CONTEXT));
            String _date = cursor.getString(cursor.getColumnIndex(_DATE));
            // String _fav = cursor.getString(cursor.getColumnIndex(_FAVORITE));
            holder_fav.tvfav.setText(_context);
            holder_fav.tvfav2.setText(_date);
            favorite_check.context = _context;
            holder_fav.llfav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder_fav.cbfav.isChecked()) {
                        holder_fav.cbfav.setChecked(false);
                    } else {
                        holder_fav.cbfav.setChecked(true);
                    }
                }
            });
            holder_fav.cbfav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (holder_fav.cbfav.isChecked())
                        favorite_check.check = "T";
                    else
                        favorite_check.check = "F";
                    favorite_checks.set(postion, favorite_check);
                }
            });
            try {
                favorite_checks.get(postion);
            } catch (Exception e) {
                favorite_checks.add(postion, favorite_check);
            } finally {
                favorite_checks.set(postion, favorite_check);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNow();
        refreshDB();
        //뷰페이퍼 어댑터 추가 소스
        pageAdapter.notifyDataSetChanged();
    }

    private class PageAdapter extends FragmentStatePagerAdapter {
        FragmentManager fm;


        public PageAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return top_weather;
                case 1:
                    return top_api;
                case 2:
                    return top_api2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        // 뷰 페이퍼 어댑터 구조 이해

        @Override
        public int getItemPosition(Object item) {

            return POSITION_NONE;   // notifyDataSetChanged

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            now = setting.getLong("temp_now", 0);
            mDayofWeek = setting.getString("dayofweek", "");
            mdate = setting.getString("date", "");
            mDayofWeek2 = setting.getString("dayofweek2", "");
        }
    }
}
