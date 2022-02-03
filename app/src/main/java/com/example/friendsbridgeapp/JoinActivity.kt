package com.example.friendsbridgeapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class JoinActivity : AppCompatActivity() {
    private lateinit var edtName : EditText
    private lateinit var edtEmail : EditText
    private lateinit var edtPassword : EditText
    private lateinit var btnJoin : Button
    private lateinit var btnProfileImg : ImageView

    private lateinit var loginAuth: FirebaseAuth
    val database = Firebase.database
    val storage = Firebase.storage
    private final val GALLERY_CODE : Int = 10
    private var imgFile : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        edtName = findViewById<EditText>(R.id.edtName)
        edtEmail = findViewById<EditText>(R.id.edtEmail)
        edtPassword = findViewById<EditText>(R.id.edtPassword)
        btnJoin = findViewById<Button>(R.id.btnJoin)
        btnProfileImg = findViewById<ImageView>(R.id.btnProfileImg)

        loginAuth = Firebase.auth

        btnProfileImg.setOnClickListener {
            val intent : Intent = Intent(Intent.ACTION_PICK)
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
            startActivityForResult(intent, GALLERY_CODE)
        }

        btnJoin.setOnClickListener {
            if(edtEmail.text.toString() == "" || edtPassword.text.toString() == ""){
                Toast.makeText(this, "이메일과 비밀번호는 공란일 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
            /*else if (imgFile == null){
                Toast.makeText(this, "이미지 사진을 선택해 주세요.", Toast.LENGTH_SHORT).show()
            }*/
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
                        /*val userStorageReference = storage.getReference().child("userProfileImgs").child(task.result!!.user!!.uid.toString())
                        val userDBRef = database.getReference().child("users").child(task.result!!.user!!.uid)
                        val uploadTask = userStorageReference.putFile(imgFile!!)
                        val urlTask = uploadTask.continueWithTask{ task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                }
                            }
                            userStorageReference.downloadUrl
                        }.addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                val userDataModel = userDataModel(edtName.text.toString(), userStorageReference.downloadUrl.getResult().toString())
                                userDBRef
                                    .push()
                                    .setValue(userDataModel)
                            }
                        }*/
                        val userDBRef = database.reference.child("users").child(task.result!!.user!!.uid)
                        val userStorageReference = storage.reference.child("userProfileImgs").child(task.result!!.user!!.uid)
                        val uploadTask = userStorageReference.putFile(imgFile!!)
                        val urlTask = uploadTask.continueWithTask { task ->
                            if(!task.isSuccessful){
                                task.exception?.let {
                                    throw it
                                }
                            }
                            userStorageReference.downloadUrl
                        }.addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                val downloadUri = task.result
                                val userDataModel = userDataModel(edtName.text.toString(), downloadUri.toString())
                                userDBRef
                                    .push()
                                    .setValue(userDataModel)
                                Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                var intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                            }
                            else{
                                Toast.makeText(this,"프로필 사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun checkPassword(password: String){

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GALLERY_CODE) run {
            imgFile = data!!.data
            btnProfileImg.setImageURI(imgFile)
        }
        else{
            return
        }
    }

}