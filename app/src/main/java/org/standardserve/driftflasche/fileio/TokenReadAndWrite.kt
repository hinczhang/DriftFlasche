package org.standardserve.googletestunit.fileio

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase

object TokenReadAndWrite {
    @SuppressLint("Range")
    fun readToken(cacheDir: String): String {
        val db = SQLiteDatabase.openOrCreateDatabase("$cacheDir/token.db", null)
        var cursor = db.rawQuery("SELECT count(*) as C from sqlite_master where type = 'table' and name = 'token'", null)
        //TODO: table existence and create table problem
        while(cursor.moveToNext()){
            var count = cursor.getInt(0)
            if(count==0){
                return ""
            }
        }
        cursor = db.rawQuery("select * from token", null)
        var token = ""
        while (cursor.moveToNext()) {
            token = cursor.getString(cursor.getColumnIndex("token"))
        }
        cursor.close()
        db.close()
        return token
    }

    fun writeToken(cacheDir: String, token: String) {
        var db = SQLiteDatabase.openOrCreateDatabase("$cacheDir/token.db", null)
        db.execSQL("CREATE TABLE IF NOT EXISTS token (token TEXT primary key)")
        db.execSQL("INSERT INTO token VALUES ('$token')")
        return
    }

    fun destroyToken(cacheDir: String) {
        var db = SQLiteDatabase.openOrCreateDatabase("$cacheDir/token.db", null)
        db.execSQL("DROP TABLE IF EXISTS token")
        return
    }

}