package com.example.friendsbridgeapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.Fragment


class MainActivity : AppCompatActivity() {

    // 각 프래그먼트 담을 변수 선언하여 객체화한 프래그먼트 담기
    val memoFragment: Fragment = MemoFragment()
    val myPageFragment : Fragment = MyPageFragment()
    val calendarFragment : Fragment = CalendarFragment()

    // 메인 화면 불러오기
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 캘린더 버튼 클릭되면 캘린더 프래그먼트 화면으로 이동
        val btnCalendar = findViewById<Button>(R.id.btnCalendar)
        btnCalendar.setOnClickListener {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentView, calendarFragment)
                    .commitAllowingStateLoss()
        }

        // 메모 버튼 클릭되면 메모 프래그먼트 화면으로 이동
        val btnMemo = findViewById<Button>(R.id.btnMemo)
        btnMemo.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentView, memoFragment)
                .commitAllowingStateLoss()
        }

        // 마이페이지 버튼 클릭되면 마이페이지 프래그먼트 화면으로 이동
        val btnMyPage = findViewById<Button>(R.id.btnMyPage)
        btnMyPage.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentView, myPageFragment)
                .commitAllowingStateLoss()
        }


    }
}
