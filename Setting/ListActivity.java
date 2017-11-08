package com.oneproject.www.allinone;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static com.oneproject.www.allinone.DBHelper.TABLE_NAME2;
import static com.oneproject.www.allinone.DBHelper._ID;
import static com.oneproject.www.allinone.DBHelper._NAME;

public class ListActivity extends AppCompatActivity {
    Button btn_add, btn_cancel;
    ListView lvItem_list;

    DBHelper mHelper;
    SQLiteDatabase db;
    Cursor cursor;
    MyCursorAdapter_list myCursorAdapter_list;

    final static int listactivitycode = 1004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_list);
        this.setFinishOnTouchOutside(false);
        setTitle("사용자 목록");
        try {
            mHelper = new DBHelper(this);
            db = mHelper.getWritableDatabase();
            cursor = db.rawQuery(String.format("select %s,%s from %s group by %s", _ID, _NAME, TABLE_NAME2, _NAME), null);
            //cursor=db.rawQuery(String.format("select * from %s group by %s",TABLE_NAME2,_NAME),null);
            myCursorAdapter_list = new MyCursorAdapter_list(ListActivity.this, cursor);
        } catch (Exception e) {

        }
        lvItem_list = (ListView) findViewById(R.id.lvItem_list);
        lvItem_list.setAdapter(myCursorAdapter_list);

        btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity("none");
            }
        });
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {

        } else if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
        refreshDB();
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
        cursor = db.rawQuery(String.format("select %s,%s from %s group by %s", _ID, _NAME, TABLE_NAME2, _NAME), null);
        myCursorAdapter_list.changeCursor(cursor);
    }

    public void startActivity(String _name) {
        Intent it = new Intent(ListActivity.this, UserListActivity.class);
        it.putExtra("name", _name);
        startActivityForResult(it, listactivitycode);
    }

    private class MyCursorAdapter_list extends CursorAdapter {
        public MyCursorAdapter_list(Context context, Cursor c) {
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
            TextView tvlist = (TextView) view.findViewById(R.id.tvlistItem);
            final String _name = cursor.getString(cursor.getColumnIndex(_NAME));
            tvlist.setText(_name);
            tvlist.setTextColor(getColor(R.color.color_logout));
            tvlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(_name);
                }
            });
        }
    }
}
