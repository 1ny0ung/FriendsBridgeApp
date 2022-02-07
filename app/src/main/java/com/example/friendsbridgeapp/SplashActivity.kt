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
        setContentView(R.layout.activity_splash)   // splash 화면 구현

        var trainImage = findViewById<ImageView>(R.id.train_image)

        var animation: Animation = AnimationUtils.loadAnimation(this, R.anim.train_move)
        trainImage.startAnimation(animation)


        
        // 일정 시간 이후 Activity 실행
        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 5000)

    }




}