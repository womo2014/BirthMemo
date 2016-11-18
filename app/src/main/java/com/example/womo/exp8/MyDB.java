package com.example.womo.exp8;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by womo on 2016/11/17.
 */

public class MyDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "Ex8DB";
    private static final String TABLE_NAME = "Records";
    private static final int DB_VERSION = 1;


    MyDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE if not exists "
                + TABLE_NAME
                + " (_id INTEGER PRIMARY KEY, name TEXT, birth TEXT, gift TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    void addRecord(Record record) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", record.name);
        cv.put("birth", record.birth);
        cv.put("gift", record.gift);
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }

    void updateRecord(Record record) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("birth", record.birth);
        cv.put("gift", record.gift);
        String whereClause = "name=\""+record.name+"\"";
        db.update(TABLE_NAME, cv, whereClause, null);
    }

    void deleteRecord(Record record) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "name=\""+record.name+"\"";
        db.delete(TABLE_NAME, whereClause, null);
    }

    Cursor queryAllData() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    Record toRecord(Cursor cursor) {
        return new Record(
                cursor.getString(cursor.getColumnIndex("name")),
                cursor.getString(cursor.getColumnIndex("birth")),
                cursor.getString(cursor.getColumnIndex("gift"))
        );
    }

    boolean isNameDuplicate(String name) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_NAME, null, "name=\""+name+"\"", null, null, null, null);
        boolean isDuplicate = c.moveToNext();
        c.close();
        return isDuplicate;
    }

    class Record {
        String name;
        String birth;
        String gift;
        Record() {}
        Record(String _name, String _birth, String _gift) {
            name = _name;
            birth = _birth;
            gift = _gift;
        }
    }
}
