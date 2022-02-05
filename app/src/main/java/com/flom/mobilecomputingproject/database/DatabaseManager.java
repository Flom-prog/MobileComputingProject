package com.flom.mobilecomputingproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.flom.mobilecomputingproject.R;

public class DatabaseManager extends SQLiteOpenHelper {

    private Context context;
    private static final String DB_NAME = "Reminder";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "myReminder";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_REMINDER_TIME = "reminder_time";
    private static final String COLUMN_CREATION_TIME = "creation_time";

    public DatabaseManager(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {   //sql query to insert data in sqllite
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MESSAGE + " TEXT, " +
                COLUMN_REMINDER_TIME + " DATETIME, " +
                COLUMN_CREATION_TIME + " DATETIME);";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);   //sql query to check table with the same name or not
        onCreate(sqLiteDatabase);
    }

    public void addReminder(String message, String reminder_time, String creation_time) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_MESSAGE, message);      //Inserts  data into sqllite database
        contentValues.put(COLUMN_REMINDER_TIME, reminder_time);
        contentValues.put(COLUMN_CREATION_TIME, creation_time);

        float result = database.insert(TABLE_NAME, null, contentValues);    //returns -1 if data successfully inserts into database

        if (result == -1) {
            Toast.makeText(context, R.string.reminder_not_added, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.reminder_added, Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor readAllReminders(String filter, String order) {
        SQLiteDatabase database = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + filter + " " + order;   //Sql query to  retrieve  data from the database

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public void deleteAllReminders() {
        SQLiteDatabase database = this.getReadableDatabase();
        database.execSQL("DELETE FROM " + TABLE_NAME);
    }


    /*// below is the method for updating our courses
    public void updateCourse(String originalCourseName, String courseName, String courseDescription,
                             String courseTracks, String courseDuration) {

        // calling a method to get writable database.
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(NAME_COL, courseName);
        values.put(DURATION_COL, courseDuration);
        values.put(DESCRIPTION_COL, courseDescription);
        values.put(TRACKS_COL, courseTracks);

        // on below line we are calling a update method to update our database and passing our values.
        // and we are comparing it with name of our course which is stored in original name variable.
        db.update(TABLE_NAME, values, "name=?", new String[]{originalCourseName});
        db.close();
    }

    // below is the method for deleting our course.
    public void deleteCourse(String courseName) {

        // on below line we are creating
        // a variable to write our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are calling a method to delete our
        // course and we are comparing it with our course name.
        db.delete(TABLE_NAME, "name=?", new String[]{courseName});
        db.close();
    }*/
}
