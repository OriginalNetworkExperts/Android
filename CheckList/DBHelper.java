package com.oneproject.www.allinone;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admin on 2017-08-03.
 */

public class DBHelper extends SQLiteOpenHelper {
    final static String TABLE_NAME = "Memo";
    final static String _ID = "_id";
    final static String _USER = "_user";
    final static String _DATE = "date";
    final static String _CHECK = "_check";
    final static String _CONTEXT = "context";
    final static String _DAYOFWEEK = "dayofweek";
    final static String _FAVORITE = "favorite";
    final static String _COLOR = "color";
    final static String _RATE = "rate";
    final static String _NOTE = "note";
    //final static String QUERY_SELECT_ALL1=String.format("select * from %s",TABLE_NAME);

    final static String TABLE_NAME2 = "List";
    final static String _NAME = "name";

    final static String TABLE_NAME3 = "Result";
    final static String _HOST = "host";
    final static String _IP = "ip";
    final static String _HARDWARE = "hardware";
    final static String _NIC = "nic";
    // final static String QUERY_SELECT_ALL2=String.format("select * from %s",TABLE_NAME2);


    public DBHelper(Context context) {
        super(context, "MyData.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = String.format("create table %s(" +
                "%s integer primary key autoincrement," +
                "%s text ," +
                "%s text ," +
                "%s text ," +
                "%s text," +
                "%s text," +
                "%s text," +
                "%s text," +
                "%s text," +
                "%s text);", TABLE_NAME, _ID, _USER, _DATE, _CHECK, _CONTEXT, _DAYOFWEEK, _FAVORITE,_COLOR,_RATE, _NOTE);
        String query2 = String.format("create table %s(" +
                "%s integer primary key autoincrement," +
                "%s text," +
                "%s text," +
                "%s text," +
                "%s text);", TABLE_NAME2, _ID, _USER, _NAME, _CONTEXT, _NOTE);
        String query3 = String.format("create table %s(" +
                "%s integer primary key autoincrement," +
                "%s text," +
                "%s text," +
                "%s text," +
                "%s text);", TABLE_NAME3, _ID, _HOST, _IP, _HARDWARE, _NIC);
        db.execSQL(query);
        db.execSQL(query2);
        db.execSQL(query3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
