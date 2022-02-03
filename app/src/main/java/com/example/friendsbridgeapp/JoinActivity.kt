package com.example.friendsbridgeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class JoinActivity : AppCompatActivity() {
    private lateinit var edtName : EditText
    private lateinit var edtEmail : EditText
    private lateinit var edtPassword : EditText
    private lateinit var btnJoin : Button

    private lateinit var loginAuth: FirebaseAuth
    val database = Firebase.database
    val userDBRef = database.getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        edtName = findViewById<EditText>(R.id.edtName)
        edtEmail = findViewById<EditText>(R.id.edtEmail)
        edtPassword = findViewById<EditText>(R.id.edtPassword)
        btnJoin = findViewById<Button>(R.id.btnJoin)

        loginAuth = Firebase.auth

        btnJoin.setOnClickListener {
            if(edtEmail.text.toString() == "" || edtPassword.text.toString() == ""){
                Toast.makeText(this, "이메일과 비밀번호는 공란일 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
            else{
                createAccount(edtEmail.text.toString(), edtPassword.text.toString())
            }
        }


    }
    private fun createAccount(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            loginAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val userDataModel = userDataModel(edtName.text.toString())
                        userDBRef.child("users").child(task.result!!.user!!.uid)

                        userDBRef
                            .push()
                            .setValue(userDataModel)

                        Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        var intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun checkPassword(password: String){

    }
}