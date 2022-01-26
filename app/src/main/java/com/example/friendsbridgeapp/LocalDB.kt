package com.example.friendsbridgeapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import java.util.prefs.PreferencesFactory

class LocalUserDB(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
):SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        if(db != null){
            createDB(db)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val sql = "DROP TABLE IF EXISTS ${LocalData.userData.TABLE_NAME}"
        if(db != null){
            db.execSQL(sql)
            onCreate(db)
        }
    }

    fun createDB(db: SQLiteDatabase){
        val sql = "CREATE TABLE IF NOT EXISTS ${LocalData.userData.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${LocalData.userData.COLUMN_NAME_ID} TEXT," +
                "${LocalData.userData.COLUMN_NAME_PASSWORD} TEXT" +
                ");"
        db.execSQL(sql)
    }
}