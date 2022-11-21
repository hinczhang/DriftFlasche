package org.standardserve.driftflasche.fileio;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TokenReadAndWrite {
    private TokenReadAndWrite(){}
    @SuppressLint("Range")
    public static String readToken(String cacheDir) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(cacheDir+"/token.db", null);
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT count(*) as C from sqlite_master where type = 'table' and name = 'token'", null);
        while(cursor.moveToNext()){
            int count = cursor.getInt(0);
            if(count==0){
                return "";
            }
        }
        cursor = db.rawQuery("select * from token", null);
        String token = "";
        while (cursor.moveToNext()) {
            token = cursor.getString(cursor.getColumnIndex("token"));
        }
        cursor.close();
        db.close();
        return token;
    }

    public static void writeToken(String cacheDir, String token) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(cacheDir+"/token.db", null);
        db.execSQL("CREATE TABLE IF NOT EXISTS token (token TEXT primary key)");
        db.execSQL("INSERT INTO token VALUES ('"+token+"')");
    }

    public static void destroyToken(String cacheDir) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(cacheDir+"/token.db", null);
        db.execSQL("DROP TABLE IF EXISTS token");
    }

}