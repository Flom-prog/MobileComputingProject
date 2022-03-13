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
    private static final int DB_VERSION = 4;

    private static final String TABLE_NAME = "myReminder";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_PICTURE = "picture";
    private static final String COLUMN_REMINDER_TIME = "reminder_time";
    private static final String COLUMN_CREATION_TIME = "creation_time";
    private static final String COLUMN_REMINDER_SEEN = "reminder_seen";
    private static final String COLUMN_LOCATION_X = "location_x";
    private static final String COLUMN_LOCATION_Y = "location_y";

    public DatabaseManager(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {   //sql query to insert data in sqllite
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MESSAGE + " TEXT, " +
                COLUMN_PICTURE + " TEXT, " +
                COLUMN_REMINDER_TIME + " DATETIME, " +
                COLUMN_CREATION_TIME + " DATETIME, " +
                COLUMN_REMINDER_SEEN + " TEXT, " +
                COLUMN_LOCATION_X + " DOUBLE, " +
                COLUMN_LOCATION_Y + " DOUBLE);";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);   //sql query to check table with the same name or not
        onCreate(sqLiteDatabase);
    }

    public Cursor readAllReminders(String filter, String order, boolean see_all_reminders) {
        SQLiteDatabase database = this.getReadableDatabase();

        String query;

        if (see_all_reminders) query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + filter + " " + order;   //Sql query to  retrieve  data from the database
        else query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_REMINDER_SEEN + " = 'true' ORDER BY " + filter + " " + order;

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }


    public Cursor readOneReminder(int id) {
        SQLiteDatabase database = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + id;

        Cursor cursor = null;
        if (database != null) {
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public int addReminder(String message, String picture, String reminder_time, String creation_time, String reminder_seen, double location_x, double location_y) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_MESSAGE, message);      //Inserts  data into sqllite database
        contentValues.put(COLUMN_PICTURE, picture);
        contentValues.put(COLUMN_REMINDER_TIME, reminder_time);
        contentValues.put(COLUMN_CREATION_TIME, creation_time);
        contentValues.put(COLUMN_REMINDER_SEEN, reminder_seen);
        contentValues.put(COLUMN_LOCATION_X, location_x);
        contentValues.put(COLUMN_LOCATION_Y, location_y);

        float result = database.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            Toast.makeText(context, R.string.reminder_not_added, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.reminder_added, Toast.LENGTH_SHORT).show();
        }

        return (int) result;
    }

    public void deleteAllReminders() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_NAME);
    }


    public void deleteReminder(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + id);
    }

    public void updateReminder(int reminder_id, String message, String picture, String reminder_time_textview, String reminder_seen) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE " + TABLE_NAME + " SET " + COLUMN_MESSAGE + " = '" + message + "', " + COLUMN_PICTURE + " = '" + picture + "', " + COLUMN_REMINDER_TIME + " = '" + reminder_time_textview + "', " + COLUMN_REMINDER_SEEN + " = '" + reminder_seen + "' WHERE " + COLUMN_ID + " = " + reminder_id);
    }

    /*public void updateReminder(int reminder_id, String message, String picture, String reminder_time_textview, String reminder_seen, double location_x, double location_y) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE " + TABLE_NAME + " SET " + COLUMN_MESSAGE + " = '" + message + "', " + COLUMN_PICTURE + " = '" + picture + "', " + COLUMN_REMINDER_TIME + " = '" + reminder_time_textview + "', " + COLUMN_REMINDER_SEEN + " = '" + reminder_seen + "', " + COLUMN_LOCATION_X + " = " + location_x + ", " + COLUMN_LOCATION_Y + " = " + location_y + " WHERE " + COLUMN_ID + " = " + reminder_id);
    }*/

    public void updateReminderSeen(int reminder_id, String reminder_seen) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE " + TABLE_NAME + " SET " + COLUMN_REMINDER_SEEN + " = '" + reminder_seen + "' WHERE " + COLUMN_ID + " = " + reminder_id);
    }
}
