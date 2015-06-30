package com.shimmerresearch.MultiShimmerRecordReview.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Point;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


public class DataBaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DataBaseOfRepsAndGraphs";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_DETAILS = "DetailsTable";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DATE = "date";
    private static final String KEY_FILENAME = "videofile";
    private static final String KEY_LABEL = "label";
    private static final String KEY_EXERCISE = "exercise";
    private static final String KEY_POINTS = "listOfPoints";

    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DETAILS_TABLE =
                "CREATE TABLE " + TABLE_DETAILS + " ( " +
                        KEY_ID + " INTEGER PRIMARY KEY," +
                        KEY_NAME + " TEXT, " +
                        KEY_DATE + " TEXT, " +
                        KEY_FILENAME + " TEXT, " +
                        KEY_LABEL + " TEXT, " +
                        KEY_EXERCISE + " TEXT," +
                        KEY_POINTS + " BLOB )";

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
        Log.d(null, "from db name is " + detail.getName());
        values.put(KEY_NAME, detail.getName());
        values.put(KEY_DATE, detail.getDate());
        values.put(KEY_FILENAME, detail.getVideoFile());
        values.put(KEY_LABEL, detail.getLabel());
        values.put(KEY_EXERCISE, detail.getExercise());
        values.put(KEY_POINTS, createByteArray(detail.getPoints()));


        Log.d(null, "points and name: " + detail.getPoints().size() + detail.getName());

        db.insert(TABLE_DETAILS, null, values);
        db.close();
    }

    //Read
    public Detail getDetail(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_DETAILS + " WHERE " + KEY_ID + " = \"" + id + "\"";
        Cursor cursor = db.rawQuery(selectQuery, null);

        Detail d;
        if (cursor != null) {
            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            String date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
            String fileName = cursor.getString(cursor.getColumnIndex(KEY_FILENAME));
            String label = cursor.getString(cursor.getColumnIndex(KEY_LABEL));
            String exercise = cursor.getString(cursor.getColumnIndex(KEY_EXERCISE));
            ArrayList<Double> list = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_POINTS)));

            d = new Detail(id, name, date, fileName, label, exercise, list);
            Log.d(null, "just pulled" + name + exercise + date);
        } else {
            Log.d(null, "cursor is null");
            return null;
        }


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
                int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                String date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
                String fileName = cursor.getString(cursor.getColumnIndex(KEY_FILENAME));
                String label = cursor.getString(cursor.getColumnIndex(KEY_LABEL));
                String exercise = cursor.getString(cursor.getColumnIndex(KEY_EXERCISE));
                ArrayList<Double> list = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_POINTS)));


                detailList.add(new Detail(id, name, date, fileName, label, exercise, list));

            } while (cursor.moveToNext());
        }
        db.close();
        return detailList;
    }

    public ArrayList<HorizontalListItemForReview> getAllWithName(String name) {
        ArrayList<HorizontalListItemForReview> listForReview = new ArrayList<>();

        //need to get all rows from db with name, AND THEN, populate a list of horizontal item for review
        String selectQuery = "SELECT * FROM " + TABLE_DETAILS + " WHERE " + KEY_NAME + " = \"" + name + "\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Detail> detailList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                String date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
                String fileName = cursor.getString(cursor.getColumnIndex(KEY_FILENAME));
                String label = cursor.getString(cursor.getColumnIndex(KEY_LABEL));
                String exercise = cursor.getString(cursor.getColumnIndex(KEY_EXERCISE));
                ArrayList<Double> list = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_POINTS)));



                detailList.add(new Detail(id, name, date, fileName, label, exercise, list));

            } while (cursor.moveToNext());
        }
        db.close();

        //now populate a list of Horizontals
        for (int i = 0; i < detailList.size(); i++) {
            Detail d = detailList.get(i);
            listForReview.add(new HorizontalListItemForReview(d.getId(), d.getName(),d.getExercise(), d.getLabel(), d.getVideoFile(), d.getPoints()));
        }

        return listForReview;
    }

    public ArrayList<String> getAllNames() {
        ArrayList<String> nameList = new ArrayList<String>();
        String selectQuery = "SELECT DISTINCT " + KEY_NAME +  ", " + KEY_DATE + " FROM " + TABLE_DETAILS;
        //" + KEY_ID +", " + KEY_NAME + " holding
        Log.d(null, " select query " + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String s = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                nameList.add(s);
            } while (cursor.moveToNext());
        }
        db.close();
        return nameList;
    }

    //Update
    public void updateLabel (int id, String label) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LABEL, label);
        db.update(TABLE_DETAILS, values, " = ?", new String[]{String.valueOf(id)});
    }

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


    private ArrayList<Double> decodeByteArrayForPoints(byte[] bytes) {
        ArrayList<Double> list = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            list = (ArrayList<Double>) ois.readObject();
        } catch (IOException e) {
            Log.d(null, "Problem in decodeByteArray");
        } catch (ClassNotFoundException e) {
            Log.d(null, "Problem in decodeByteArray");
        }

        return list;
    }



}
