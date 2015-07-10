package com.example.matsuotakurou.markdown;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by matsuotakurou on 2015/05/07.
 */

    public class MyDbHelper extends SQLiteOpenHelper {

        public static final String DB_NAME = "mymemo.db";
        public static final int DB_VERSION = 10;

        public MyDbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(MyMemoContract.Memos.CREATE_TABLE);
            sqLiteDatabase.execSQL(MyMemoContract.Memos.INIT_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            sqLiteDatabase.execSQL(MyMemoContract.Memos.DROP_TABLE);
            onCreate(sqLiteDatabase);
        }
    }

