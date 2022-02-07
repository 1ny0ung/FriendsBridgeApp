package com.example.friendsbridgeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    // 메모 기능 프래그먼트
    val memoFragment: Fragment = MemoFragment()
    // 마이 페이지 - 회원 정보 조회, 로그아웃 기능 프래그먼트
    public val myPageFragment : Fragment = MyPageFragment()
    // 마이 페이지 - 회원 정보 수정 기능 프래그먼트
    public val userDataEditFragment : Fragment = UserDataEditFragment()
    // 캘린더 기능 프래그먼트
    val calendarFragment : Fragment = CalendarFragment()

     // Firebase Database의 유저 정보를 가져오고, 프래그먼트로 저장할 변수
    private var uid : String = ""
    private var userName : String = ""
    private var profileImgUrl : String = ""

    // Firebase 로그인
    private val loginAuth = Firebase.auth
    // Firebase 데이터베이스
    private val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 파이어베이스 데이터베이스 중 유저 정보에 접근
        val myRef = database.getReference("users")
        // 유저 정보에서 현재 로그인 된 유저 정보에 접근
        val userRef = myRef.child(loginAuth.currentUser!!.uid.toString())

        // 현재 유저의 uid, 닉네임, 프로필 사진 저장 경로를 각각의 변수에 업데이트해 저장
        userRef.addValueEventListener(object :
            ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                profileImgUrl = snapshot.getValue(userDataModel::class.java)!!.profileImgUrl
                uid = snapshot.getValue(userDataModel::class.java)!!.uid
                userName = snapshot.getValue(userDataModel::class.java)!!.userName
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        // 하단의 캘린더 버튼 누르면 캘린더 프래그먼트 호출
        val btnCalendar = findViewById<Button>(R.id.btnCalendar)
        btnCalendar.setOnClickListener {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentView, calendarFragment)
                    .commitAllowingStateLoss()
        }

        // 하단의 마이페이지 버튼 누르면 마이 페이지 프래그먼트 호출
        val btnMyPage = findViewById<Button>(R.id.btnMyPage)
        btnMyPage.setOnClickListener {
            // 데이터 베이스에서 저장한 유저 정보를 마이 페이지 프래그먼트로 전달
            putUserData(myPageFragment)
            // 마이 페이지로 이동
            moveFragment(myPageFragment)
        }

        // 하단의 메모 버튼을 누르면 메모 프래그먼트 호출
        val btnMemo = findViewById<Button>(R.id.btnMemo)
        btnMemo.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentView, memoFragment)
                .commitAllowingStateLoss()
        }

    }

    // 파이어베이스 데이터베이스에서 가져온 현재의 유저 정보를 프래그먼트들로 전달
    fun putUserData(fragment : Fragment){
        val bundle = Bundle()

        // 현재 유저의 uid, 닉네임, 프로필 사진 경로를 bundle에 넣음
        bundle.putString("uid", uid)
        bundle.putString("userName", userName)
        bundle.putString("profileImgUrl", profileImgUrl)

        fragment.arguments = bundle
    }

    // 현재의 액티비티에서 프래그먼트로 이동
    fun moveFragment(fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentView, fragment)
        transaction.commit()
    }
}
