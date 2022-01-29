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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var edtEmail : EditText
    private lateinit var edtPassword : EditText

    private lateinit var checkEmailSave : CheckBox
    private lateinit var btnLogin : Button
    private lateinit var btnJoin : Button

    private lateinit var loginAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtEmail = findViewById<EditText>(R.id.edtEmail)
        edtPassword = findViewById<EditText>(R.id.edtPassword)

        checkEmailSave = findViewById<CheckBox>(R.id.checkEmailSave)
        btnLogin = findViewById<Button>(R.id.btnLogin)
        btnJoin = findViewById<Button>(R.id.btnJoin)

        loginAuth = Firebase.auth

        btnLogin.setOnClickListener {
            if(edtEmail.text.toString() == "" || edtPassword.text.toString() == ""){
                Toast.makeText(this, "이메일과 비밀번호는 공란일 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
            else{
                LoginByEmail(edtEmail.text.toString(), edtPassword.text.toString())
            }
        }
    }

    private fun LoginByEmail(email: String, password: String){
        loginAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){ task ->
            if(task.isSuccessful){
                Toast.makeText(this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,"로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}