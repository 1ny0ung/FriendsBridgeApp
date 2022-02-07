package com.example.friendsbridgeapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

// 로그인 액티비티
class LoginActivity : AppCompatActivity() {
    // 이메일, 패스워드 입력 창
    private lateinit var edtEmail : EditText
    private lateinit var edtPassword : EditText

    // 로그인 버튼
    private lateinit var btnLogin : Button
    // 로그인 정보가 없는 경우를 위해 회원 가입 액티비티로 넘어가는 버튼
    private lateinit var btnJoin : Button

    // 구글 계정을 통한 회원가입 / 로그인 버튼
    private lateinit var btnGoogleLogin : SignInButton
    private lateinit var googleSignInClient : GoogleSignInClient
    private final val GOOGLE_LOGIN_CODE : Int = 100

    // 파이어베이스 인증 로그인
    private lateinit var loginAuth : FirebaseAuth
    // 파이어베이스 데이터 베이스
    private val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 뷰에 있던 컴포넌트들과 변수를 id로 연결
        edtEmail = findViewById<EditText>(R.id.edtEmail)
        edtPassword = findViewById<EditText>(R.id.edtPassword)

        btnLogin = findViewById<Button>(R.id.btnLogin)
        btnJoin = findViewById<Button>(R.id.btnJoin)
        btnGoogleLogin = findViewById<SignInButton>(R.id.btnGoogleLogin)

        loginAuth = Firebase.auth

        // 구글 계정 로그인을 위해 사용
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 이메일, 패스워드 로그인 버튼 클릭 이벤트
        btnLogin.setOnClickListener {
            // 이메일, 패스워드 창이 공란인 경우 메세지 출력
            if(edtEmail.text.toString() == "" || edtPassword.text.toString() == ""){
                Toast.makeText(this, "이메일과 비밀번호는 공란일 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
            // 이메일, 패스워드 로그인 함수 호출
            else{
                LoginByEmail(edtEmail.text.toString(), edtPassword.text.toString())
            }
        }

        // 회원 가입 버튼 클릭 이벤트
        btnJoin.setOnClickListener {
            // 회원 가입 액티비티(.JoinActivity)로 이동
            var intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }

        // 구글 계정 로그인 버튼 클릭 이벤트
        btnGoogleLogin.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
        }
    }

    // 이메일, 패스워드 로그인 함수
    private fun LoginByEmail(email: String, password: String){
        loginAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){ task ->
            // 로그인에 성공하는 경우
            if(task.isSuccessful){
                Toast.makeText(this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                // 메인 액티비티로 이동
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            // 로그인에 실패한 경우 실패 메세지 출력
            else{
                Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 구글 계정 로그인을 위한 OnActivityResult 함수
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 구글 계정 로그인을 시도한 경우
        if(requestCode == GOOGLE_LOGIN_CODE){
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            // 구글 계정 로그인, 회원 가입 api의 인텐트 호출
            if(result!!.isSuccess){
                // credential을 통해 가입
                val account = result.signInAccount
                val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
                loginAuth.signInWithCredential(credential).addOnCompleteListener { task->
                    // 가입, 로그인이 가능한 경우
                    if(task.isSuccessful){
                        // 파이어베이스 database에 접근하기 위한 reference
                        val userDBRef = database.reference.child("users")
                        // 파이어베이스 database의 실시간 데이터 받아오는 함수
                        userDBRef.addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                // 만약 database의 "user" 항목이 user uid 이름의 자식 항목을 가지는 경우
                                // 가입 정보가 있는 것 - 로그인시킴
                                if(snapshot.hasChild(loginAuth.currentUser!!.uid.toString())){
                                    // 로그인, 메인 액티비티(.MainActivity)로 이동
                                    Toast.makeText(this@LoginActivity, "로그인이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                    finish()
                                }
                                // user uid 이름의 자식 항목이 없는 경우
                                // 가입 정보가 없는 것 - 회원 가입 시킴
                                else{
                                    // 파이어베이스 데이터베이스의 "users" 항목에 접근
                                    val userDBRef = database.reference.child("users")
                                    // 파이어베이스 데이터베이스의 "users" 항목에 user의 uid 이름의 항목을 만들어, 유저 정보 저장 데이터 모델 객체를 값으로 설정
                                    // uid, 닉네임, 프로필 사진 저장 경로 저장
                                    val userDataModel = userDataModel(task.result!!.user!!.uid, task.result!!.user!!.uid, "userImgs/ProfileImg/defaultprofileImg.jpg")
                                    userDBRef
                                        .setValue(userDataModel)
                                    // 회원 가입 절차 완료 후 메인 액티비티(.MainActivity)로 이동
                                    Toast.makeText(this@LoginActivity, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                    var intent = Intent(this@LoginActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }
                    else{
                        Toast.makeText(this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}