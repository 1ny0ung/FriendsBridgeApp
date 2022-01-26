package com.example.friendsbridgeapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

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
                "${LocalData.userData.COLUMN_NAME_ID} varchar(15)," +
                "${LocalData.userData.COLUMN_NAME_PASSWORD} varchar(20)" +
                ");"
        db.execSQL(sql)
    }
    fun checkID(ID: String): Boolean{
        // 매개 변수로 받아온 ID가 userTBL의 ID 목록에 있는지 확인
        val userDB = this.readableDatabase

        val projection = arrayOf(BaseColumns._ID)

        val selection = "${LocalData.userData.COLUMN_NAME_ID} = ?"
        val selectionArguments = arrayOf(ID)

        val cursor: Cursor = userDB.query(
            LocalData.userData.TABLE_NAME,
            projection,
            selection,
            selectionArguments,
            null,
            null,
            null
        )

        if(cursor.count > 0){
            //cursor.close()
            //userDB.close()
            return true }
        //cursor.close()
        //userDB.close()
        else{return false}
    }

    fun login(ID: String, Password: String) : Boolean{
        val userDB = this.readableDatabase

        val projection = arrayOf(BaseColumns._ID)

        val selection = "${LocalData.userData.COLUMN_NAME_ID} = ? AND ${LocalData.userData.COLUMN_NAME_PASSWORD} = ?"
        val selectionArguments = arrayOf(ID, Password)

        val cursor : Cursor = userDB.query(
            LocalData.userData.TABLE_NAME,
            projection,
            selection,
            selectionArguments,
            null,
            null,
            null
        )

        if(cursor.count > 0){
            //cursor.close()
            //userDB.close()
            return true}
        //cursor.close()
        //userDB.close()
        else{return false}
    }

    fun join(ID: String, Password: String){
        val userDB = this.writableDatabase
        val userInfo = ContentValues().apply {
            put(LocalData.userData.COLUMN_NAME_ID, ID)
            put(LocalData.userData.COLUMN_NAME_PASSWORD, Password)
        }
        val newRowId = userDB?.insert(LocalData.userData.TABLE_NAME, null, userInfo)
    }

}
