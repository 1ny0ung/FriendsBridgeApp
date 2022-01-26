package com.example.friendsbridgeapp

import android.provider.BaseColumns

object LocalData {
    object userData : BaseColumns {
        const val TABLE_NAME = "userData"
        const val COLUMN_NAME_ID = "userID"
        const val COLUMN_NAME_PASSWORD = "userPassword"
    }
    object groupData : BaseColumns{

    }
}