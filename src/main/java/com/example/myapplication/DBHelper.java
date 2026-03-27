package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "PasswordDB", null, 4); // Version 4 for User Table
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table 1: Store saved passwords
        db.execSQL("CREATE TABLE passwords (id INTEGER PRIMARY KEY AUTOINCREMENT, site TEXT, username TEXT, password TEXT, question TEXT, answer TEXT, owner TEXT)");
        // Table 2: Store registered users
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, gmail TEXT, pin TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS passwords");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    // --- USER ACCOUNT METHODS ---

    public boolean registerUser(String user, String gmail, String pin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", user);
        cv.put("gmail", gmail);
        cv.put("pin", pin);
        return db.insert("users", null, cv) != -1;
    }

    public Cursor checkLogin(String input) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Checks if input matches either username OR gmail
        return db.rawQuery("SELECT * FROM users WHERE username = ? OR gmail = ?", new String[]{input});
    }

    // --- PASSWORD ENTRY METHODS ---

    public boolean insertData(String site, String user, String pass, String ques, String ans, String owner) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("site", site);
        cv.put("username", user);
        String encryptedPass = Base64.encodeToString(pass.getBytes(), Base64.DEFAULT);
        cv.put("password", encryptedPass);
        cv.put("question", ques);
        cv.put("answer", ans);
        cv.put("owner", owner);
        return db.insert("passwords", null, cv) != -1;
    }

    public Cursor getAllData(String owner) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM passwords WHERE owner = ?", new String[]{owner});
    }

    public void deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("passwords", "id = ?", new String[]{id});
    }

    public boolean updateData(String id, String site, String user, String pass, String ques, String ans, String owner) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("site", site);
        cv.put("username", user);
        String encryptedPass = Base64.encodeToString(pass.getBytes(), Base64.DEFAULT);
        cv.put("password", encryptedPass);
        cv.put("question", ques);
        cv.put("answer", ans);
        cv.put("owner", owner);
        return db.update("passwords", cv, "id = ?", new String[]{id}) > 0;
    }

    public String decrypt(String encryptedPass) {
        byte[] decodeValue = Base64.decode(encryptedPass, Base64.DEFAULT);
        return new String(decodeValue);
    }
}