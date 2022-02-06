package com.example.friendsbridgeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {
    private lateinit var edtEmail : EditText
    private lateinit var edtPassword : EditText

    private lateinit var checkEmailSave : CheckBox
    private lateinit var btnLogin : Button
    private lateinit var btnJoin : Button

    private lateinit var btnGoogleLogin : SignInButton
    private lateinit var googleSignInClient : GoogleSignInClient
    private final val GOOGLE_LOGIN_CODE : Int = 100

    private lateinit var loginAuth : FirebaseAuth
    private val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtEmail = findViewById<EditText>(R.id.edtEmail)
        edtPassword = findViewById<EditText>(R.id.edtPassword)

        checkEmailSave = findViewById<CheckBox>(R.id.checkEmailSave)
        btnLogin = findViewById<Button>(R.id.btnLogin)
        btnJoin = findViewById<Button>(R.id.btnJoin)
        btnGoogleLogin = findViewById<SignInButton>(R.id.btnGoogleLogin)

        loginAuth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnLogin.setOnClickListener {
            if(edtEmail.text.toString() == "" || edtPassword.text.toString() == ""){
                Toast.makeText(this, "이메일과 비밀번호는 공란일 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
            else{
                LoginByEmail(edtEmail.text.toString(), edtPassword.text.toString())
            }
        }

        btnJoin.setOnClickListener {
            var intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }

        btnGoogleLogin.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
        }
    }

    private fun LoginByEmail(email: String, password: String){
        loginAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){ task ->
            if(task.isSuccessful){
                Toast.makeText(this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else{
                Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result!!.isSuccess){
                val account = result.signInAccount
                val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
                loginAuth.signInWithCredential(credential).addOnCompleteListener { task->
                    if(task.isSuccessful){
                        if(database.reference.child("users").orderByChild(task.result!!.user!!.uid.toString()) == null){
                            val userDBRef = database.reference.child("users").child(task.result!!.user!!.uid)
                            val userDataModel = userDataModel(task.result!!.user!!.uid.toString(), "https://firebasestorage.googleapis.com/v0/b/friends-bridgeapp.appspot.com/o/userProfileImgs%2FdefaultprofileImg.jpg?alt=media&token=bcfd0e83-6eda-48e6-8bdb-69bc8cfdc802")
                            userDBRef
                                .push()
                                .setValue(userDataModel)
                            Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                            var intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this, "로그인이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    }
                    else{
                        Toast.makeText(this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}