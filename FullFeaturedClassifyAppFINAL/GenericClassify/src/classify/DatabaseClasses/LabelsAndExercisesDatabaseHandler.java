package classify.DatabaseClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class LabelsAndExercisesDatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DataBaseOfLabels";
    private static final int DATABASE_VERSION = 4;
    private static final String TABLE_LABEL = "LabelExerciseInfo";


    private static final String KEY_ID = "id";
    private static final String KEY_EXERCISE = "exercise";
    private static final String KEY_LABEL = "label";


    public LabelsAndExercisesDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LABEL_TABLE =
                "CREATE TABLE " + TABLE_LABEL + " ( " +
                        KEY_ID + " INTEGER PRIMARY KEY," +
                        KEY_LABEL + " TEXT, " +
                        KEY_EXERCISE + " TEXT " +
                        ")";

        db.execSQL(CREATE_LABEL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LABEL);
        onCreate(db);
    }

    //CRUD

    //Create
    public void addLabel(String exercise, String label) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LABEL, label);
        values.put(KEY_EXERCISE, exercise);

        db.insert(TABLE_LABEL, null, values);
        db.close();
    }

    //Read

    public ArrayList<String> getAllLabelsForExercise(String exercise) {

        ArrayList<String> listOfLabels = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_LABEL + " WHERE " + KEY_EXERCISE + " = \"" + exercise + "\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String label = cursor.getString(cursor.getColumnIndex(KEY_LABEL));
                listOfLabels.add(label);
            } while (cursor.moveToNext());
        }
        db.close();

        return listOfLabels;
    }



    public ArrayList<String> getExercises() {
        ArrayList<String> exerciseList = new ArrayList<String>();
        String selectQuery = "SELECT DISTINCT " + KEY_EXERCISE + " FROM " + TABLE_LABEL;
        //" + KEY_ID +", " + KEY_NAME + " holding
        Log.d(null, " select query " + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String exercise = cursor.getString(cursor.getColumnIndex(KEY_EXERCISE));
                exerciseList.add(exercise);
            } while (cursor.moveToNext());
        }
        db.close();
        return exerciseList;
    }


    //Update


    //Delete
    public void deleteLabel(String exercise, String label) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LABEL, KEY_LABEL + " = \"" + label + "\" AND " + KEY_EXERCISE + " = \"" + exercise + "\"", null);
        db.close();
    }

    public void deleteExercise(String exercise) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LABEL, KEY_EXERCISE + " = \"" + exercise + "\"", null);
        db.close();
    }




}
