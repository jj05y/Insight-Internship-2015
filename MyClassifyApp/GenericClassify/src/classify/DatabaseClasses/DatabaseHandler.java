package classify.DatabaseClasses;

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
import java.util.HashMap;
import java.util.List;

import classify.Constants.C;
import classify.ListItems.ItemForClassify;
import classify.ListItems.ItemForReview;
import classify.ListItems.ItemForVerification;
import classify.ListItems.Row;


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DataBaseOfMovements";
    private static final int DATABASE_VERSION = 4;
    private static final String TABLE_DETAILS = "DetailsTable";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DATE = "date";
    private static final String KEY_FILENAME = "videofile";
    private static final String KEY_ACTUAL_LABEL = "actuallabel";
    private static final String KEY_PREDICTED_LABEL= "predictedLabel";
    private static final String KEY_EXERCISE = "exercise";
    private static final String KEY_ACCEL_MAG_POINTS = "listOfAMagPoints";
    private static final String KEY_ACCEL_X_POINTS = "listOfAXPoints";
    private static final String KEY_ACCEL_Y_POINTS = "listOfAYPoints";
    private static final String KEY_ACCEL_Z_POINTS = "listOfAZPoints";
    private static final String KEY_PITCH_POINTS = "listOfPITCHPoints";
    private static final String KEY_ROLL_POINTS = "listOfROLLPoints";
    private static final String KEY_YAW_POINTS = "listOfYAWPoints";
    private static final String KEY_GYRO_MAG_POINTS = "listGyroMagPoints";
    private static final String KEY_GYRO_X_POINTS = "listOfGyroXPoints";
    private static final String KEY_GYRO_Y_POINTS = "listOfGyroYPoints";
    private static final String KEY_GYRO_Z_POINTS = "listOfGyroZPoints";
    private static final String KEY_QUAT_W_POINTS = "listOfQuatWPoints";
    private static final String KEY_QUAT_X_POINTS = "listOfQuatXPoints";
    private static final String KEY_QUAT_Y_POINTS = "listOfQuatYPoints";
    private static final String KEY_QUAT_Z_POINTS = "listOfQuatZPoints";
    private static final String KEY_REP = "rep";
    private static final String KEY_FEATURE_STRING = "featurestring";

    public DatabaseHandler(Context context) {
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
                        KEY_ACTUAL_LABEL + " INTEGER, " +
                        KEY_PREDICTED_LABEL + " INTEGER, " +
                        KEY_EXERCISE + " INTEGER," +
                        KEY_REP + " INTEGER, " +
                        KEY_ACCEL_MAG_POINTS + " BLOB, " +
                        KEY_ACCEL_X_POINTS + " BLOB, " +
                        KEY_ACCEL_Y_POINTS + " BLOB, " +
                        KEY_ACCEL_Z_POINTS + " BLOB, " +
                        KEY_PITCH_POINTS + " BLOB, " +
                        KEY_ROLL_POINTS + " BLOB, " +
                        KEY_YAW_POINTS + " BLOB, " +
                        KEY_GYRO_MAG_POINTS + " BLOB, " +
                        KEY_GYRO_X_POINTS + " BLOB, " +
                        KEY_GYRO_Y_POINTS + " BLOB, " +
                        KEY_GYRO_Z_POINTS + " BLOB," +
                        KEY_QUAT_W_POINTS + " BLOB," +
                        KEY_QUAT_X_POINTS + " BLOB," +
                        KEY_QUAT_Y_POINTS + " BLOB," +
                        KEY_QUAT_Z_POINTS + " BLOB," +
                        KEY_FEATURE_STRING + " TEXT" +
                        ")";

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
        values.put(KEY_ACTUAL_LABEL, detail.getActualLabel());
        values.put(KEY_PREDICTED_LABEL, detail.getPredictedLabel());
        values.put(KEY_EXERCISE, detail.getExercise());
        values.put(KEY_ACCEL_MAG_POINTS, createByteArray(detail.getAccelMagPoints()));
        values.put(KEY_ACCEL_X_POINTS, createByteArray(detail.getAccelXPoints()));
        values.put(KEY_ACCEL_Y_POINTS, createByteArray(detail.getAccelYPoints()));
        values.put(KEY_ACCEL_Z_POINTS, createByteArray(detail.getAccelZPoints()));
        values.put(KEY_PITCH_POINTS, createByteArray(detail.getPitchPoints()));
        values.put(KEY_ROLL_POINTS, createByteArray(detail.getRollPoints()));
        values.put(KEY_YAW_POINTS, createByteArray(detail.getYawPoints()));
        values.put(KEY_GYRO_MAG_POINTS, createByteArray(detail.getGyroMagPoints()));
        values.put(KEY_GYRO_X_POINTS, createByteArray(detail.getGyroXPoints()));
        values.put(KEY_GYRO_Y_POINTS, createByteArray(detail.getGyroYPoints()));
        values.put(KEY_GYRO_Z_POINTS, createByteArray(detail.getGyroZPoints()));
        values.put(KEY_QUAT_W_POINTS, createByteArray(detail.getQuatWPoints()));
        values.put(KEY_QUAT_X_POINTS, createByteArray(detail.getQuatXPoints()));
        values.put(KEY_QUAT_Y_POINTS, createByteArray(detail.getQuatYPoints()));
        values.put(KEY_QUAT_Z_POINTS, createByteArray(detail.getQuatZPoints()));
        values.put(KEY_REP, detail.getRep());
        values.put(KEY_FEATURE_STRING, detail.getFeatureString());


        Log.d(null, "points and name: " + detail.getAccelMagPoints().size() + detail.getName());

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
            int actualLabel = cursor.getInt(cursor.getColumnIndex(KEY_ACTUAL_LABEL));
            int predictedLabel = cursor.getInt(cursor.getColumnIndex(KEY_PREDICTED_LABEL));
            int exercise = cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE));
            HashMap<String, ArrayList<Double>> list = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ACCEL_MAG_POINTS)));
            HashMap<String, ArrayList<Double>> list1 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ACCEL_X_POINTS)));
            HashMap<String, ArrayList<Double>> list2 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ACCEL_Y_POINTS)));
            HashMap<String, ArrayList<Double>> list3 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ACCEL_Z_POINTS)));
            HashMap<String, ArrayList<Double>> list4 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_PITCH_POINTS)));
            HashMap<String, ArrayList<Double>> list5 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ROLL_POINTS)));
            HashMap<String, ArrayList<Double>> list6 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_YAW_POINTS)));
            HashMap<String, ArrayList<Double>> list7 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_QUAT_W_POINTS)));
            HashMap<String, ArrayList<Double>> list8 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_QUAT_X_POINTS)));
            HashMap<String, ArrayList<Double>> list9 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_QUAT_Y_POINTS)));
            HashMap<String, ArrayList<Double>> list10 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_QUAT_Z_POINTS)));
            HashMap<String, ArrayList<Double>> list11 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_GYRO_X_POINTS)));
            HashMap<String, ArrayList<Double>> list12 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_GYRO_Y_POINTS)));
            HashMap<String, ArrayList<Double>> list13 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_GYRO_Z_POINTS)));
            HashMap<String, ArrayList<Double>> list14 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_GYRO_MAG_POINTS)));
            int rep = cursor.getInt(cursor.getColumnIndex(KEY_REP));
            String featureString = cursor.getString(cursor.getColumnIndex(KEY_FEATURE_STRING));

            d = new Detail(id, name, date, fileName, actualLabel, predictedLabel, exercise, list, list1, list2, list3, list4, list5, list6, list7, list8, list9, list10, list11, list12, list13, list14, rep, featureString);
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
                int actualLabel = cursor.getInt(cursor.getColumnIndex(KEY_ACTUAL_LABEL));
                int predictedLabel = cursor.getInt(cursor.getColumnIndex(KEY_PREDICTED_LABEL));
                int exercise = cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE));
                HashMap<String, ArrayList<Double>> list = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ACCEL_MAG_POINTS)));
                HashMap<String, ArrayList<Double>> list1 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ACCEL_X_POINTS)));
                HashMap<String, ArrayList<Double>> list2 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ACCEL_Y_POINTS)));
                HashMap<String, ArrayList<Double>> list3 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ACCEL_Z_POINTS)));
                HashMap<String, ArrayList<Double>> list4 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_PITCH_POINTS)));
                HashMap<String, ArrayList<Double>> list5 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ROLL_POINTS)));
                HashMap<String, ArrayList<Double>> list6 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_YAW_POINTS)));
                HashMap<String, ArrayList<Double>> list7 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_QUAT_W_POINTS)));
                HashMap<String, ArrayList<Double>> list8 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_QUAT_X_POINTS)));
                HashMap<String, ArrayList<Double>> list9 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_QUAT_Y_POINTS)));
                HashMap<String, ArrayList<Double>> list10 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_QUAT_Z_POINTS)));
                HashMap<String, ArrayList<Double>> list11 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_GYRO_X_POINTS)));
                HashMap<String, ArrayList<Double>> list12 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_GYRO_Y_POINTS)));
                HashMap<String, ArrayList<Double>> list13 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_GYRO_Z_POINTS)));
                HashMap<String, ArrayList<Double>> list14 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_GYRO_MAG_POINTS)));
                int rep = cursor.getInt(cursor.getColumnIndex(KEY_REP));
                String featureString = cursor.getString(cursor.getColumnIndex(KEY_FEATURE_STRING));


                detailList.add(new Detail(id, name, date, fileName, actualLabel, predictedLabel ,exercise, list, list1, list2, list3, list4, list5, list6, list7, list8, list9, list10, list11, list12, list13, list14, rep, featureString));

            } while (cursor.moveToNext());
        }
        db.close();
        return detailList;
    }

    public ArrayList<ItemForReview> getAllWithName(String name) {
        ArrayList<ItemForReview> listForReview = new ArrayList<>();

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
                int actualLabel = cursor.getInt(cursor.getColumnIndex(KEY_ACTUAL_LABEL));
                int predictedLabel = cursor.getInt(cursor.getColumnIndex(KEY_PREDICTED_LABEL));
                int exercise = cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE));
                HashMap<String, ArrayList<Double>> list = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ACCEL_MAG_POINTS)));
                HashMap<String, ArrayList<Double>> list1 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ACCEL_X_POINTS)));
                HashMap<String, ArrayList<Double>> list2 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ACCEL_Y_POINTS)));
                HashMap<String, ArrayList<Double>> list3 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ACCEL_Z_POINTS)));
                HashMap<String, ArrayList<Double>> list4 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_PITCH_POINTS)));
                HashMap<String, ArrayList<Double>> list5 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ROLL_POINTS)));
                HashMap<String, ArrayList<Double>> list6 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_YAW_POINTS)));
                HashMap<String, ArrayList<Double>> list7 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_QUAT_W_POINTS)));
                HashMap<String, ArrayList<Double>> list8 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_QUAT_X_POINTS)));
                HashMap<String, ArrayList<Double>> list9 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_QUAT_Y_POINTS)));
                HashMap<String, ArrayList<Double>> list10 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_QUAT_Z_POINTS)));
                HashMap<String, ArrayList<Double>> list11 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_GYRO_X_POINTS)));
                HashMap<String, ArrayList<Double>> list12 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_GYRO_Y_POINTS)));
                HashMap<String, ArrayList<Double>> list13 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_GYRO_Z_POINTS)));
                HashMap<String, ArrayList<Double>> list14 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_GYRO_MAG_POINTS)));
                int rep = cursor.getInt(cursor.getColumnIndex(KEY_REP));
                String featureString = cursor.getString(cursor.getColumnIndex(KEY_FEATURE_STRING));

                detailList.add(new Detail(id, name, date, fileName, actualLabel, predictedLabel, exercise, list, list1, list2, list3, list4, list5, list6, list7, list8, list9, list10, list11, list12, list13, list14, rep, featureString));

            } while (cursor.moveToNext());
        }
        db.close();

        //now populate a list of Horizontals
        for (int i = 0; i < detailList.size(); i++) {
            Detail d = detailList.get(i);
            listForReview.add(new ItemForReview(d.getName(), d.getActualLabel(), d.getPredictedLabel(), d.getVideoFile(), d.getExercise(), d.getId(), d.getRep(), d.getAccelMagPoints(), d.getAccelXPoints(), d.getAccelYPoints(), d.getAccelZPoints(), d.getPitchPoints(), d.getRollPoints(), d.getYawPoints(), d.getQuatWPoints(), d.getQuatXPoints(), d.getQuatYPoints(), d.getQuatZPoints(), d.getGyroXPoints(), d.getGyroYPoints(), d.getGyroZPoints(), d.getGyroMagPoints()));
        }

        return listForReview;
    }

    public ArrayList<ItemForReview> getAllWithExerciseAndLabel(int exercise, int label) {
        ArrayList<ItemForReview> listForReview = new ArrayList<>();

        //need to get all rows from db with name, AND THEN, populate a list of horizontal item for review
        String selectQuery = "SELECT * FROM " + TABLE_DETAILS + " WHERE " + KEY_EXERCISE + " = " + exercise + " AND " + KEY_ACTUAL_LABEL + " = " + label;
        if (label != -1){
            selectQuery += " AND " + KEY_ACTUAL_LABEL + " = " + label;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Detail> detailList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                String date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
                String fileName = cursor.getString(cursor.getColumnIndex(KEY_FILENAME));
                int rep = cursor.getInt(cursor.getColumnIndex(KEY_REP));
                int actualLabelFromDb = cursor.getInt(cursor.getColumnIndex(KEY_ACTUAL_LABEL));
                int predictedLabel = cursor.getInt(cursor.getColumnIndex(KEY_PREDICTED_LABEL));
                HashMap<String, ArrayList<Double>> list = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ACCEL_MAG_POINTS)));
                HashMap<String, ArrayList<Double>> list1 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ACCEL_X_POINTS)));
                HashMap<String, ArrayList<Double>> list2 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ACCEL_Y_POINTS)));
                HashMap<String, ArrayList<Double>> list3 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ACCEL_Z_POINTS)));
                HashMap<String, ArrayList<Double>> list4 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_PITCH_POINTS)));
                HashMap<String, ArrayList<Double>> list5 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_ROLL_POINTS)));
                HashMap<String, ArrayList<Double>> list6 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_YAW_POINTS)));
                HashMap<String, ArrayList<Double>> list7 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_QUAT_W_POINTS)));
                HashMap<String, ArrayList<Double>> list8 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_QUAT_X_POINTS)));
                HashMap<String, ArrayList<Double>> list9 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_QUAT_Y_POINTS)));
                HashMap<String, ArrayList<Double>> list10 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_QUAT_Z_POINTS)));
                HashMap<String, ArrayList<Double>> list11 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_GYRO_X_POINTS)));
                HashMap<String, ArrayList<Double>> list12 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_GYRO_Y_POINTS)));
                HashMap<String, ArrayList<Double>> list13 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_GYRO_Z_POINTS)));
                HashMap<String, ArrayList<Double>> list14 = decodeByteArrayForPoints(cursor.getBlob(cursor.getColumnIndex(KEY_GYRO_MAG_POINTS)));
                String featureString = cursor.getString(cursor.getColumnIndex(KEY_FEATURE_STRING));


                detailList.add(new Detail(id, name, date, fileName, actualLabelFromDb, predictedLabel, exercise, list, list1, list2, list3, list4, list5, list6, list7, list8, list9, list10, list11, list12, list13, list14, rep, featureString));

            } while (cursor.moveToNext());
        }
        db.close();

        //now populate a list of Horizontals
        for (int i = 0; i < detailList.size(); i++) {
            Detail d = detailList.get(i);
            listForReview.add(new ItemForReview(d.getName(), d.getActualLabel(), d.getPredictedLabel(), d.getVideoFile(), d.getExercise(), d.getId(), d.getRep(),d.getAccelMagPoints(),d.getAccelXPoints(),d.getAccelYPoints(),d.getAccelZPoints(),d.getPitchPoints(),d.getRollPoints(),d.getYawPoints(),  d.getQuatWPoints(), d.getQuatXPoints(), d.getQuatYPoints(), d.getQuatZPoints(), d.getGyroXPoints(), d.getGyroYPoints(), d.getGyroZPoints(), d.getGyroMagPoints()));
        }

        return listForReview;


    }

    public ArrayList<String> getAllNames() {
        ArrayList<String> nameList = new ArrayList<String>();
        String selectQuery = "SELECT DISTINCT " + KEY_NAME + " FROM " + TABLE_DETAILS;
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

    public ArrayList<Row> getRowStrings() {
        ArrayList<Row> rowList = new ArrayList<Row>();
        String selectQuery = "SELECT * FROM " + TABLE_DETAILS;
        //" + KEY_ID +", " + KEY_NAME + " holding
        Log.d(null, " select query " + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int rowID = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                String date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
                int rep = cursor.getInt(cursor.getColumnIndex(KEY_REP));
                String fileName = cursor.getString(cursor.getColumnIndex(KEY_FILENAME));

                String s = "Row: " + rowID + " - " + "  Name: " + name + "  Date: " + date + "  Rep#: " + rep;
                rowList.add(new Row(s, rowID, fileName));
            } while (cursor.moveToNext());
        }
        db.close();
        return rowList;
    }

    public ArrayList<ItemForClassify> getItemsForClassify(int exercise) {
        ArrayList<ItemForClassify> list = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_DETAILS + " WHERE " + KEY_EXERCISE  + " = " + exercise;
        Log.d(null, " select query " + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                int rowID = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                int actualLabel = cursor.getInt(cursor.getColumnIndex(KEY_ACTUAL_LABEL));
                int predictedLabel = cursor.getInt(cursor.getColumnIndex(KEY_PREDICTED_LABEL));
                String featureString = cursor.getString(cursor.getColumnIndex(KEY_FEATURE_STRING));
                int rep = cursor.getInt(cursor.getColumnIndex(KEY_REP));

                list.add(new ItemForClassify(rowID,actualLabel,predictedLabel,name,featureString, rep));

            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public ArrayList<ItemForVerification> getItemsForVerification(int exercise) {
        ArrayList<ItemForVerification> list = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_DETAILS + " WHERE " + KEY_PREDICTED_LABEL + " != 0 AND " + KEY_EXERCISE  + " = " + exercise ;
        Log.d(null, " select query " + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                int rowID = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                int actualLabel = cursor.getInt(cursor.getColumnIndex(KEY_ACTUAL_LABEL));
                int predictedLabel = cursor.getInt(cursor.getColumnIndex(KEY_PREDICTED_LABEL));
                String filename = cursor.getString(cursor.getColumnIndex(KEY_FILENAME));
                int rep = cursor.getInt(cursor.getColumnIndex(KEY_REP));
                list.add(new ItemForVerification(rowID, name, actualLabel, predictedLabel, filename, rep));

            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }
    //Update
    public void updateActualLabel(int id, int label) {
        Log.d("spinner", "putting actual label in: " + C.LABELS[label] + " row number: " + id);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ACTUAL_LABEL, label);
        db.update(TABLE_DETAILS, values, KEY_ID + " = " + id, null);
    }

    public void updatePredictedLabel(int id, int label) {
        Log.d("predict", "putting predicted label in: " + C.LABELS[label] + " row number: " + id);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PREDICTED_LABEL, label);
        db.update(TABLE_DETAILS, values, KEY_ID + " = " + id, null);
    }

    public void updateExercise(int id, int exercise) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EXERCISE, exercise);
        db.update(TABLE_DETAILS, values, KEY_ID + " = " + id, null);
        Log.d("spinner", "putting exercise in: " + C.EXERCISES[exercise] + " row number: " + id);
    }

    //Delete
    public void deleteDetail(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DETAILS, KEY_ID + " = " + id, null);
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


    private HashMap<String, ArrayList<Double>> decodeByteArrayForPoints(byte[] bytes) {
        HashMap<String, ArrayList<Double>> list = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            list = (HashMap<String, ArrayList<Double>>) ois.readObject();
        } catch (IOException e) {
            Log.d(null, "Problem in decodeByteArray");
        } catch (ClassNotFoundException e) {
            Log.d(null, "Problem in decodeByteArray");
        }

        return list;
    }



}
