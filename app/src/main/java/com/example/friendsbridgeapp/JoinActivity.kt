package com.example.friendsbridgeapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.regex.Pattern

// 회원 가입 액티비티
class JoinActivity : AppCompatActivity() {
    // 닉네임, 이메일, 패스워드 입력창
    private lateinit var edtName : EditText
    private lateinit var edtEmail : EditText
    private lateinit var edtPassword : EditText
    // 회원 가입, 프로필 사진 이미지 설정 버튼
    private lateinit var btnJoin : Button
    private lateinit var btnProfileImg : ImageView

    // 파이어베이스 인증 로그인
    private lateinit var loginAuth: FirebaseAuth
    // 파이어베이스 데이터베이스
    val database = Firebase.database
    // 파이어베이스 스토리지
    val storage = Firebase.storage

    // 갤러리 접근을 알리는 상수
    private final val GALLERY_CODE : Int = 10
    // imageView에 적용될 이미지 uri 변수
    private var imgFile : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        // id로 뷰에 있는 컴포넌트들과 변수들 연결
        edtName = findViewById<EditText>(R.id.edtName)
        edtEmail = findViewById<EditText>(R.id.edtEmail)
        edtPassword = findViewById<EditText>(R.id.edtPassword)
        btnJoin = findViewById<Button>(R.id.btnJoin)
        btnProfileImg = findViewById<ImageView>(R.id.btnProfileImg)

        loginAuth = Firebase.auth

        // 프로필 이미지 사진이 있는 ImageView를 클릭하는 경우
        // 프로필 사진 등록 이벤트
        btnProfileImg.setOnClickListener {
            val intent : Intent = Intent(Intent.ACTION_PICK)
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
            startActivityForResult(intent, GALLERY_CODE)
        }

        // 회원가입 버튼 클릭 이벤트
        btnJoin.setOnClickListener {
            // 만약 닉네임, 이메일, 패스워드 입력 창 중 공란이 있다면 오류 표시
            if(edtName.text.toString() == "" || edtEmail.text.toString() == "" || edtPassword.text.toString() == ""){
                Toast.makeText(this, "닉네임, 이메일, 비밀번호는 공란일 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
            // 만약 프로필 사진을 설정하지 않았다면 오류 표시
            else if (imgFile == null){
                Toast.makeText(this, "이미지 사진을 선택해 주세요.", Toast.LENGTH_SHORT).show()
            }
            // 이메일, 패스워드 유효성 검사 통과하면 이메일, 패스워드 계정 생성 함수 호출
            else if(checkEmail(edtEmail.text.toString()) && checkPassword(edtPassword.text.toString())){
                createAccount(edtEmail.text.toString(), edtPassword.text.toString())
            }
        }
    }

    // 이메일과 패스워드로 계정을 생성하는 함수
    private fun createAccount(email: String, password: String) {
        // firebase의 이메일, 패스워드 가입 함수 호출
        loginAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                // 계정 생성에 성공하는 경우
                if (task.isSuccessful) {
                    // database에 새로운 유저의 정보를 추가하기 위한 reference
                    val userDBRef = database.reference.child("users").child(task.result!!.user!!.uid)
                    // storage에 새로운 유저의 프로필 사진을 저장하기 위한 reference
                    // userImgs 폴더 / profileImg 폴더로 경로 지정, 파일 명은 user의 uid
                    val userStorageReference = storage.reference.child("userImgs").child( "ProfileImg/" + task.result!!.user!!.uid)
                    userStorageReference.putFile(imgFile!!).addOnCompleteListener { task ->
                        // 프로필 사진 저장에 성공하는 경우 database에 user의 uid, 닉네임, 프로필 사진의 저장 경로를 저장
                        if(task.isSuccessful){
                            val userDataModel = userDataModel(loginAuth.currentUser!!.uid, edtName.text.toString(), "userImgs/ProfileImg/" + loginAuth.currentUser!!.uid)
                            userDBRef
                                .setValue(userDataModel)
                            // 회원 가입 완료를 알림
                            Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                            // 시작 화면으로 이동
                            var intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        // 프로필 사진 저장에 실패한 경우
                        else{
                            task.exception?.let {
                                throw it
                            }
                            // 프로필 사진 업로드의 실패를 알리는 메세지
                            Toast.makeText(this,"프로필 사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                // 계정 생성에 실패하는 경우 회원가입에 실패했음을 알림
                else {
                    Toast.makeText(this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 이메일 유효성 검사 함수
    private fun checkEmail(email: String) : Boolean{
        // 올바른 이메일 형식이 아닌 경우 오류 메세지를 출력하고 false 반환
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "올바른 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show()
            return false
        }
        // 올바른 이메일 형식인 경우 true 반환
        else{
            return true
        }
    }

    // 패스워드 유효성 검사 함수
    private fun checkPassword(password: String) : Boolean{
        // 올바른 패스워드 형식(8-20자, 영문자와 숫자, 특수문자를 포함)이 아닌 경우 오류 메세지를 출력하고 false 반환
        if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", password))
        {
            Toast.makeText(this,"올바른 비밀번호 형식이 아닙니다.",Toast.LENGTH_SHORT).show();
            return false
        }
        // 올바른 패스워드 형식인 경우 true 반환
        else{
            return true
        }
    }

    // 갤러리에 접근하기 위한 함수, 갤러리에서 받아온 데이터를 저장하고 imageView에 그 이미지 적용
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GALLERY_CODE) run {
            imgFile = data!!.data
            btnProfileImg.setImageURI(imgFile)
        }
    }
}