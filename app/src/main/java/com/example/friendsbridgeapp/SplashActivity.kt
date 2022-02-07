package com.example.friendsbridgeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class SplashActivity : AppCompatActivity() {

    var state: Int = 1

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        var trainImage = findViewById(R.id.train_image) as ImageView

        var animation: Animation = AnimationUtils.loadAnimation(this, R.anim.train_move)
        trainImage.startAnimation(animation)



        auth = Firebase.auth

        try {

            Log.d("SPLASH", auth.currentUser!!.uid)
            Toast.makeText(this, "원래 비회원 로그인이 되어있는 사람입니다!", Toast.LENGTH_LONG).show()
            Handler().postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 5000)

        } catch (e : Exception) {
            Log.d("SPLASH", "회원가입 시켜줘야함")

            auth.signInAnonymously()
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(this, "비회원 로그인 성공", Toast.LENGTH_LONG).show()

                            Handler().postDelayed({
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }, 5000)
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(this, "비회원 로그인 실패", Toast.LENGTH_LONG).show()
                        }
                    }

        }


    }
}