package com.example.friendsbridgeapp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    private lateinit var edtEmail : EditText
    private lateinit var edtPassword : EditText

    private lateinit var checkEmailSave : CheckBox
    private lateinit var btnLogin : Button
    private lateinit var btnJoin : Button
    private lateinit var localUserDB: LocalUserDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtEmail = findViewById<EditText>(R.id.edtEmail)
        edtPassword = findViewById<EditText>(R.id.edtPassword)

        checkEmailSave = findViewById<CheckBox>(R.id.checkEmailSave)
        btnLogin = findViewById<Button>(R.id.btnLogin)
        btnJoin = findViewById<Button>(R.id.btnJoin)

        localUserDB = LocalUserDB(this, "LocalUserDB.db", null, 1)

        btnLogin.setOnClickListener {
            val userID = edtEmail.text.toString()
            val userPassword = edtPassword.text.toString()
            if(userID == "" || userPassword == ""){
                Toast.makeText(this, "아이디와 비밀번호는 공란일 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
            else{
                val isConfirmed : Boolean = localUserDB.login(userID, userPassword)
                if(isConfirmed){
                // 로그인 후 다음 액티비티로 이동하는 코드 필요
                Toast.makeText(this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()
                }
                else{
                    if(localUserDB.checkID(userID)){
                        Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this, "존재하지 않는 ID입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}