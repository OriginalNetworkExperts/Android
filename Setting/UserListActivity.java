package com.oneproject.www.allinone;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.oneproject.www.allinone.DBHelper.TABLE_NAME;
import static com.oneproject.www.allinone.DBHelper.TABLE_NAME2;
import static com.oneproject.www.allinone.DBHelper._CONTEXT;
import static com.oneproject.www.allinone.DBHelper._ID;
import static com.oneproject.www.allinone.DBHelper._NAME;

public class UserListActivity extends AppCompatActivity {
    boolean mode = false;//false:insert true:list
    Button btn_add, btn_center, btn_delete;
    EditText etName;
    ListView lvItem_user;

    SharedPreferences setting;
    String user, mdate;

    View dialogView;

    String name;

    DBHelper mHelper;
    SQLiteDatabase db;
    Cursor cursor;

    MyCursorAdapter_user myAdapter_user;
    EditText edt;

    ArrayList<String> contents = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_list);
        this.setFinishOnTouchOutside(false);

        Intent it = getIntent();
        name = it.getStringExtra("name");
        String title = "";
        etName = (EditText) findViewById(R.id.etName);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_center = (Button) findViewById(R.id.btn_center);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        lvItem_user = (ListView) findViewById(R.id.lvItem_userlist);

        setting = getSharedPreferences("setting", 0);
        user = setting.getString("user", "test");
        mdate = setting.getString("date", "");

        if (name.equals("none")) {
            title = "사용자 목록 추가";
            mode = false;

            etName.setVisibility(View.VISIBLE);
            btn_center.setText(R.string.btn_confirm);
            btn_delete.setText(R.string.btn_cancel);
        } else {
            title = name;
            mode = true;
            etName.setVisibility(View.GONE);
            btn_center.setText(R.string.btn_apply);
        }
        setTitle(title);
        try {
            mHelper = new DBHelper(this);
            db = mHelper.getWritableDatabase();
            if (mode)
                cursor = db.rawQuery(String.format("select * from %s where %s='%s'", TABLE_NAME2, _NAME, name), null);
            else
                cursor = db.rawQuery(String.format("select * from %s where %s=''", TABLE_NAME2, _NAME), null);
            myAdapter_user = new MyCursorAdapter_user(UserListActivity.this, cursor);
        } catch (Exception e) {
        }
        lvItem_user.setAdapter(myAdapter_user);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alt = makeAlt2(UserListActivity.this, "", 0);
                alt.show();
            }
        });

        if (mode) {//보여주기->적용,삭제
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {//삭제
                    AlertDialog.Builder alt = makeAlt(UserListActivity.this, name + "을(를) " + getString(R.string.btn_delete), 1);
                    alt.show();
                }
            });
            btn_center.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {//적용
                    int i = 0;
                    while (i < contents.size()) {
                        String query = String.format("insert into %s values(null,'%s','%s','F','%s','','F','','0','AUTO');", TABLE_NAME, user, mdate, contents.get(i));
                        exeQuery(query);
                        i++;
                    }
                    Toast.makeText(UserListActivity.this,"내용이 추가되었습니다.",Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
            });
        } else {//추가하기->확인,취소
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alt = makeAlt(UserListActivity.this, getString(R.string.btn_cancel), 0);
                    alt.show();
                }
            });
            btn_center.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String _name = etName.getText().toString();
                    if (_name.equals(""))
                        Toast.makeText(UserListActivity.this, R.string.hint_userlist, Toast.LENGTH_SHORT).show();
                    else {
                        String query = String.format("update %s set %s='%s' where %s=''", TABLE_NAME2, _NAME, _name, _NAME);
                        exeQuery(query);
                        Toast.makeText(UserListActivity.this,"목록이 추가되었습니다.",Toast.LENGTH_SHORT).show();
                        setResult(RESULT_FIRST_USER);
                        finish();
                    }
                }
            });
        }

    }

    public boolean exeQuery(String query) {
        try {
            db.execSQL(query);
            return true;
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e + "", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void refreshDB() {
        if (mode)
            cursor = db.rawQuery(String.format("select * from %s where %s='%s'", TABLE_NAME2, _NAME, name), null);
        else
            cursor = db.rawQuery(String.format("select * from %s where %s=''", TABLE_NAME2, _NAME), null);
        myAdapter_user.changeCursor(cursor);
    }

    AlertDialog.Builder makeAlt(Context cxt, String title, final int temp) {
        AlertDialog.Builder alt = new AlertDialog.Builder(cxt);
        alt.setTitle(title + "하시겠습니까?");
        alt.setNegativeButton(R.string.btn_no, null);
        alt.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mode && temp == 1) {
                    String query = String.format("delete from %s where %s='%s'", TABLE_NAME2, _NAME, name);
                    exeQuery(query);
                } else {
                    String query = String.format("delete from %s where %s=''", TABLE_NAME2, _NAME);
                    exeQuery(query);
                }
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        return alt;
    }

    AlertDialog.Builder makeAlt2(Context cxt, String temp_content, final int id) {
        dialogView = View.inflate(cxt, R.layout.alt_userlist, null);
        AlertDialog.Builder alt = new AlertDialog.Builder(cxt);
        alt.setView(dialogView);
        String title="항목 ";
        if(id>0)
            title+=getString(R.string.btn_modi);
        else
            title+=getString(R.string.btn_plus);

        alt.setTitle(title);
        edt = (EditText) dialogView.findViewById(R.id.etContext);
        if(id>0)
            edt.setText(temp_content);
        alt.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String content = edt.getText().toString();
                String query = "";
                if (mode)
                    query = String.format("insert into %s values (" +
                            "null,'%s','%s','%s','');", TABLE_NAME2, user, name, content);
                else
                    query = String.format("insert into %s values (" +
                            "null,'%s','','%s','');", TABLE_NAME2, user, content);
                if (id > 0)
                    query = String.format("update %s set %s='%s' where %s=%s", TABLE_NAME2, _CONTEXT, content,_ID ,id);
                //수정

                exeQuery(query);
                refreshDB();
            }
        });
        alt.setNegativeButton(R.string.btn_cancel,null);
        if(id>0)
            alt.setNeutralButton(R.string.btn_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String query = String.format("delete from %s where %s=%s", TABLE_NAME2, _ID, id);
                    exeQuery(query);
                    refreshDB();
                    Toast.makeText(UserListActivity.this, getString(R.string.alert_delete), Toast.LENGTH_SHORT).show();
                }
            });

        return alt;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alt = makeAlt(UserListActivity.this, getString(R.string.btn_cancel), 0);
        alt.show();
    }

    private class MyCursorAdapter_user extends CursorAdapter {
        public MyCursorAdapter_user(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.item_list_user, viewGroup, false);
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            int position = cursor.getPosition();
            TextView tvList = (TextView) view.findViewById(R.id.tvlistItem);
            final int _id=cursor.getInt(cursor.getColumnIndex(_ID));
            final String _content = cursor.getString(cursor.getColumnIndex(_CONTEXT));
            tvList.setText(_content);
            tvList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //수정창
                    AlertDialog.Builder alt = makeAlt2(UserListActivity.this, _content, _id);
                    alt.show();
                }
            });
            try {
                contents.get(position);
            } catch (Exception e) {
                contents.add(position, _content);
            } finally {
                contents.set(position, _content);
            }
        }
    }
}
