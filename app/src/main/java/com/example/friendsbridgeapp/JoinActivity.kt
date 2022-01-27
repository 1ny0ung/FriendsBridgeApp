package com.example.friendsbridgeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class JoinActivity : AppCompatActivity() {
    private lateinit var edtEmail : EditText
    private lateinit var edtPassword : EditText
    private lateinit var btnJoin : Button
    private lateinit var localUserDB: LocalUserDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        edtEmail = findViewById<EditText>(R.id.edtEmail)
        edtPassword = findViewById<EditText>(R.id.edtPassword)
        btnJoin = findViewById<Button>(R.id.btnJoin)

        localUserDB = LocalUserDB(this, "LocalUserDB.db", null, 1)

        btnJoin.setOnClickListener {
            val userID = edtEmail.text.toString()
            val userPassword = edtPassword.text.toString()

            if(userID == "" || userPassword == ""){
                Toast.makeText(this, "아이디와 비밀번호는 공란일 수 없습니다.", Toast.LENGTH_SHORT).show()
            }

            // ID, Password 유효성 검사문 넣기
            // ID, 비밀번호에 영문자, 숫자 포함되어야 하고 6자 이상이어야 함!

            else{
                val isIDExists : Boolean = localUserDB.checkID(edtEmail.text.toString())
                if(isIDExists){
                    Toast.makeText(this, "이미 존재하는 아이디입니다. 다른 아이디를 설정해 주세요.", Toast.LENGTH_SHORT).show()
                }
                else{
                    localUserDB.join(edtEmail.text.toString(), edtPassword.text.toString())
                    Toast.makeText(this, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show()
                    // 로그인 화면으로 전환
                }
            }
        }
    }
}