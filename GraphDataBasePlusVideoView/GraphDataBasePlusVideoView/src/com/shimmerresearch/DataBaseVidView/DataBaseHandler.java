package com.shimmerresearch.DataBaseVidView;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


public class DataBaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "VideoAndGraphDetailsManager";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_DETAILS = "Details";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LIST = "listOfThings";
    private static final String KEY_FILE_NAME = "filename";

    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DETAILS_TABLE =
                "CREATE TABLE " + TABLE_DETAILS + " ( " +
                        KEY_ID + " INTEGER PRIMARY KEY," +
                        KEY_NAME + " TEXT," +
                        KEY_LIST + " BLOB, " +
                        KEY_FILE_NAME + " TEXT" + ")";
        db.execSQL(CREATE_DETAILS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DETAILS);
        onCreate(db);
    }

    //CRUD

    //Create
    public void addDetail(Detail detail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, detail.getName());
        values.put(KEY_LIST, createByteArray(detail.getPoints()));
        values.put(KEY_FILE_NAME, detail.getStoredFileName());

        Log.d(null, "points size: " + detail.getPoints().size() + " byte array size " + createByteArray(detail.getPoints()).length);

        db.insert(TABLE_DETAILS, null, values);
        db.close();
    }

    //Read
    public Detail getDetail(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_DETAILS + " WHERE " + KEY_ID + " = \"" + id + "\"";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            Log.d(null, "cursor is null");
            return null;
        }

        byte[] bytes = cursor.getBlob(2);
        ArrayList<Point> points = decodeByteArray(bytes);

        Detail d = new Detail(Integer.parseInt(cursor.getString(0)), cursor.getString(1), points, cursor.getString(3));
        db.close();
        cursor.close();
        return d;
    }

    //Read all
    public List<Detail> getAllDetails() {
        List<Detail> detailList = new ArrayList<Detail>();
        String selectQuery = "SELECT * FROM " + TABLE_DETAILS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Detail d = new Detail();
                d.setId(Integer.parseInt(cursor.getString(0)));
                d.setName(cursor.getString(1));

                byte[] bytes = cursor.getBlob(2);
                ArrayList<Point> points = decodeByteArray(bytes);
                d.setPoints(points);

                d.setStoredFileName(cursor.getString(3));

                detailList.add(d);
            } while (cursor.moveToNext());
        }
        db.close();
        return detailList;
    }

    public List<String> getAllNames() {
        List<String> nameList = new ArrayList<String>();
        String selectQuery = "SELECT * FROM " + TABLE_DETAILS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String s = cursor.getString(0) + " - " + cursor.getString(1);
                nameList.add(s);
            } while (cursor.moveToNext());
        }
        db.close();
        return nameList;
    }

    //Update
    //not really needed

    //Delete
    public void deleteDetail(Detail detail) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DETAILS, KEY_ID + " = ?", new String[]{String.valueOf(detail.getId())});
        db.close();
    }

    private byte[] createByteArray(Object obj) {

        byte[] bArray = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream objOstream = new ObjectOutputStream(baos);
            objOstream.writeObject(obj);
            bArray = baos.toByteArray();

        } catch (IOException e) {
            Log.d(null, "Problem in createByteArray");
        }

        return bArray;
    }

    private ArrayList<Point> decodeByteArray(byte[] bytes) {
        ArrayList<Point> list = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            list = (ArrayList<Point>) ois.readObject();
        } catch (IOException e) {
            Log.d(null, "Problem in decodeByteArray");
        } catch (ClassNotFoundException e) {
            Log.d(null, "Problem in decodeByteArray");
        }

        return list;
    }


}
