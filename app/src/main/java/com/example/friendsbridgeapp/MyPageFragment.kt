package com.example.friendsbridgeapp

import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyPageFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View? {
        return inflater.inflate(R.layout.fragment_my_page, container, false)
    }

}




/*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

// 마이 페이지 (회원 정보 조회, 회원 정보 변경 프래그먼트로 이동, 로그아웃)
// 그룹 기능 추가 예정
class MyPageFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var profileImageView: ImageView  // 유저의 프로필 사진을 보여 줄 imageView
    private lateinit var txtNickName : TextView // 유저의 닉네임을 보여 줄 textView
    private lateinit var txtAccount : TextView // 유저의 가입 계정 (이메일)을 보여 줄 textView

    // 회원 정보 수정, 로그아웃 버튼
    private lateinit var btnUserDataEdit : Button
    private lateinit var btnLogOut : Button

    // 파이어베이스 인증 로그인 활용
    private lateinit var loginAuth : FirebaseAuth

    // MainActivity에서 받아올 uid, 닉네임, 프로필 사진 저장 경로 변수
    var profileImgUrl : String = ""
    var userName : String = ""
    var uid : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // MainActivity에서 전달 받은 uid, 닉네임, 프로필 사진 저장 경로를 변수에 저장
            uid = it.getString("uid").toString()
            userName = it.getString("userName").toString()
            profileImgUrl = it.getString("profileImgUrl").toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰에 있는 컴포넌트들을 변수와 연결
        profileImageView = getView()!!.findViewById<ImageView>(R.id.profileImgView)
        txtNickName = getView()!!.findViewById<TextView>(R.id.txtNickName)
        txtAccount = getView()!!.findViewById<TextView>(R.id.txtAccount)

        btnUserDataEdit = getView()!!.findViewById<Button>(R.id.btnUserDataEdit)
        btnLogOut = getView()!!.findViewById<Button>(R.id.btnLogOut)

        loginAuth = Firebase.auth

        // 프로필 사진을 동그랗게 변경, 기본 이미지 설정
        profileImageView.setImageResource(R.drawable.ic_baseline_person_24)
        profileImageView.background = ShapeDrawable(OvalShape())
        profileImageView.clipToOutline = true


        // 파이어베이스 storage에서 유저 프로필 사진 저장 경로의 사진을 받아옴
        // 성공 시 glide 활용해 imageView에 사진 적용
        Firebase.storage.reference.child(profileImgUrl).downloadUrl.addOnCompleteListener {
            if (it.isSuccessful) {
                Glide.with(getView()!!).load(it.result).override(400, 400)
                    .thumbnail(0.1f).into(profileImageView)
            }
            // 사진 가져오기 오류 발생 시 기본 프로필 사진 띄움
            else{
                Log.d("error", "업로드 실패")
                Glide.with(getView()!!).load(R.drawable.ic_baseline_person_24).override(400,400).into(profileImageView)
            }
        }

        // 유저 계정 정보 (이메일)을 파이어베이스 로그인 정보에서 가져와 텍스트로 보여 줌.
        txtAccount.setText(loginAuth.currentUser!!.email.toString())
        // 유저 닉네임 텍스트에 userName 값 적용
        txtNickName.setText(userName)

        // 회원 정보 수정 버튼을 누르는 경우
        btnUserDataEdit.setOnClickListener {
            // 메인 액티비티에서 미리 만들어 둔 회원 정보 수정 프래그먼트로 이동해야 함.
            // 실시간 데이터 베이스에서 메인으로 전달한 데이터를 받아야 하기 때문.
            val mActivity = activity as MainActivity
            mActivity.putUserData(mActivity.userDataEditFragment)
            mActivity.moveFragment(mActivity.userDataEditFragment)
        }

        // 로그아웃 버튼을 누르는 경우
        btnLogOut.setOnClickListener {
            // 로그아웃 후 다시 Splash 화면으로 이동
            loginAuth.signOut()
            startActivity(Intent(this.context, SplashActivity::class.java))
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View? {
        return inflater.inflate(R.layout.fragment_my_page, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyPageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}

 */