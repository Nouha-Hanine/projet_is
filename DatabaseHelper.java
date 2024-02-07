package com.example.saqsi;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DBname= "signin.db";

    public static final String JOKES_TABLE_NAME = "jokes";
    public static final String JOKES_COLUMN_ID = "id";
    public static final String JOKES_COLUMN_CONTENT = "content";


    public static final String TIMETABLE_TABLE_NAME = "timetable";
    public static final String TIMETABLE_COLUMN_ID = "id";
    public static final String TIMETABLE_COLUMN_SUBJECT = "subject";
    public static final String TIMETABLE_COLUMN_DAY = "day";
    public static final String TIMETABLE_COLUMN_TIME = "time";
    public static final String TIMETABLE_COLUMN_ROOM = "room";
    public DatabaseHelper(@Nullable Context context) {
        super(context, DBname, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table users(email TEXT primary key, username TEXT, password TEXT)"
        );

        db.execSQL(
                "CREATE TABLE " + JOKES_TABLE_NAME + " (" +
                        JOKES_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        JOKES_COLUMN_CONTENT + " TEXT)"
        );
        insertJoke( "Why did the chicken cross the road? To get to the other side.");
        insertJoke( "What do you call cheese that isn't yours? Nacho cheese.");

        db.execSQL(
                "CREATE TABLE " + TIMETABLE_TABLE_NAME + " (" +
                        TIMETABLE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TIMETABLE_COLUMN_SUBJECT + " TEXT, " +
                        TIMETABLE_COLUMN_DAY+ " TEXT, " +
                        TIMETABLE_COLUMN_TIME + " TEXT,"+
                        TIMETABLE_COLUMN_ROOM + " TEXT)"
        );
        insertTimetableEntry("is", "tuesday", "11:30 AM", "Td Room 12");
        insertTimetableEntry( "rip", "MONDAY", "08:30 AM", "salle td 9");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(
                "drop table if exists users"
        );
    }
    public boolean insertUser(String email, String username, String password){
        SQLiteDatabase DB= this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("username", username);
        contentValues.put("password", password);

        long result = DB.insert("users", null, contentValues);
    if(result== -1) {return false;}else{return true;}//check if success or not
    }


    public boolean checkEmail(String email){
        SQLiteDatabase DB= this.getWritableDatabase();
        Cursor cursor = DB.rawQuery(
                "select * from users where email =?", new String[]{email}
        );
        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }
    public boolean checkAccount(String username, String password){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery(
            "select * from users where username = ? and password =?",  new String[]{username, password}
        );
        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }

    public void insertJoke(String content) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(JOKES_COLUMN_CONTENT, content);
        db.insert(JOKES_TABLE_NAME, null, contentValues);
    }


    public void insertTimetableEntry(String subject,String day ,String time , String room) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TIMETABLE_COLUMN_SUBJECT, subject);
        contentValues.put(TIMETABLE_COLUMN_DAY, day);
        contentValues.put(TIMETABLE_COLUMN_TIME, time);
        contentValues.put(TIMETABLE_COLUMN_ROOM, room);
        db.insert(TIMETABLE_TABLE_NAME, null, contentValues);
    }
    @SuppressLint("Range")
    public String getJoke() {
        SQLiteDatabase db = getReadableDatabase();
        String joke = null;

        Cursor cursor = db.query(JOKES_TABLE_NAME, new String[]{JOKES_COLUMN_CONTENT},
                null, null, null, null, "RANDOM()", "1");

        if (cursor != null && cursor.moveToFirst()) {
            joke = cursor.getString(cursor.getColumnIndex(JOKES_COLUMN_CONTENT));
            cursor.close();
        }

        return joke;
    }
    @SuppressLint("Range")
    public String getCurrentStudySubject() {

        String currentTime = getCurrentTime();


        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + TIMETABLE_COLUMN_SUBJECT + ", " + TIMETABLE_COLUMN_ROOM +
                " FROM " + TIMETABLE_TABLE_NAME +
                " WHERE " + TIMETABLE_COLUMN_DAY + " = ? AND " + TIMETABLE_COLUMN_TIME + " <= ?" +
                " ORDER BY " + TIMETABLE_COLUMN_TIME + " DESC LIMIT 1";

        Cursor cursor = db.rawQuery(query, new String[]{"Monday", currentTime});

        String subject = null;
        String room = null;

        if (cursor.moveToFirst()) {
            subject = cursor.getString(cursor.getColumnIndex(TIMETABLE_COLUMN_SUBJECT));
            room = cursor.getString(cursor.getColumnIndex(TIMETABLE_COLUMN_ROOM));
        }

        cursor.close();
        return (subject != null && room != null) ? subject + " in " + room : null;
    }
    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

}
