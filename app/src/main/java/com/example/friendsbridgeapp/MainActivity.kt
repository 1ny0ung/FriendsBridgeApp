package com.example.friendsbridgeapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.friendsbridgeapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity() {

    //val dataModelList = mutableListOf<DataModel>()
    val memoFragment: Fragment = MemoFragment()
    val myPageFragment : Fragment = MyPageFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnMyPage = findViewById<Button>(R.id.btnMyPage)
        btnMyPage.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentView, myPageFragment)
                .commitAllowingStateLoss()
        }

        val btnMemo = findViewById<Button>(R.id.btnMemo)
        btnMemo.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentView, memoFragment)
                .commitAllowingStateLoss()
        }
    }
}
