package com.example.friendsbridgeapp

import android.provider.BaseColumns

object LocalData {
    object userData : BaseColumns {
        const val TABLE_NAME = "userData"
        const val COLUMN_NAME_ID = "ID"
        const val COLUMN_NAME_PASSWORD = "Password"
    }
    object groupData : BaseColumns{

    }
}