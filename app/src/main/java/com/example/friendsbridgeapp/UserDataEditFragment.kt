package com.example.friendsbridgeapp

import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserDataEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserDataEditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var profileImgView: ImageView // 유저 프로필 사진을 보여 줄 imageView
    private lateinit var edtNickname : EditText // 유저 닉네임 수정을 위한 editText
    private lateinit var txtAccount : TextView // 유저 계정 정보 보여 줄 textView
    private lateinit var btnSaveData : Button // 바뀐 유저 정보를 저장하기 위한 버튼
    private lateinit var btnWithdraw : Button // 회원 탈퇴 버튼

    // MainActivity에서 받아온 유저 정보(uid, 닉네임, 프로필 사진 저장 경로)를 저장할 변수
    private var uid : String = ""
    private var userName : String = ""
    private var profileImgUrl : String = ""

    // 갤러리 접근을 알리기 위한 상수
    private final val GALLERY_CODE = 100
    // 갤러리에서 가져온 이미지를 저장할 변수
    private var imgFile : Uri? = null
    // 프로필 사진의 변경 여부를 알리는 변수
    // 만약 프로필 이미지 변경 없이 이름만 변경한다면, storage 접근하지 않도록 하기 위함.
    private var isPicChanged : Boolean = false

    // 파이어베이스 로그인, 데이터베이스, 스토리지 이용
    private val loginAuth = Firebase.auth
    private val database = Firebase.database
    private val storage = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

            // MainActivity가 데이터베이스에게 업데이트 받는 유저 정보를 가져와 변수에 저장
            uid = it.getString("uid").toString()
            userName = it.getString("userName").toString()
            profileImgUrl = it.getString("profileImgUrl").toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰에 있는 컴포넌트들을 변수와 연결
        profileImgView = getView()!!.findViewById(R.id.profileImgView2)
        edtNickname = getView()!!.findViewById(R.id.edtNickname2)
        txtAccount = getView()!!.findViewById(R.id.txtAccount2)
        btnSaveData = getView()!!.findViewById(R.id.btnSaveData)
        btnWithdraw = getView()!!.findViewById(R.id.btnWithdraw)

        // 프로필 사진이 들어갈 ImageView를 동그랗게 만들어 줌
        profileImgView.setImageResource(R.drawable.ic_baseline_person_24)
        profileImgView.background = ShapeDrawable(OvalShape())
        profileImgView.clipToOutline = true

        // 파이어베이스 storage에서 유저 정보에 저장되어 있던 프로필 사진 저장 경로에 접근
        Firebase.storage.reference.child(profileImgUrl).downloadUrl.addOnCompleteListener {
            // 접근 성공한 경우 glide 활용해 기존 프로필 사진 imageView에 적용
            if (it.isSuccessful) {
                Glide.with(getView()!!).load(it.result).override(400, 400)
                    .thumbnail(0.1f).into(profileImgView)
            }
            // 접근 실패한 경우 기본 이미지 적용
            else{
                Log.d("error", "업로드 실패")
                Glide.with(getView()!!).load(R.drawable.ic_baseline_person_24).override(400,400).into(profileImgView)
            }
        }

        // 유저 닉네임 정보 표시
        edtNickname.setText(userName)
        // 유저 계정 정보 표시
        txtAccount.setText(loginAuth.currentUser!!.email)

        // 만약 프로필 사진을 클릭하는 경우 갤러리에서 사진 가져오는 동작 시행
        profileImgView.setOnClickListener {
            val intent : Intent = Intent(Intent.ACTION_PICK)
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
            startActivityForResult(intent, GALLERY_CODE)
            // 프로필 사진 변경이 있었음을 표시
            isPicChanged = true
        }

        // 데이터베이스에서 현재 로그인된 유저 정보에 접근
        val userDBRef = database.reference.child("users").child(loginAuth.currentUser!!.uid)

        // 저장 버튼 클릭 이벤트
        btnSaveData.setOnClickListener {
            // 프로필 사진 변경이 있었을 경우
            if(isPicChanged){
                // 기존에 사용하던 프로필 사진 저장 경로를 따라가 그 사진을 storage에서 삭제
                storage.reference.child(profileImgUrl).delete().addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Log.d("TAG", "기존 프로필 사진 삭제 성공")

                        // 삭제 이후 갤러리에서 가져온 사진을 유저의 프로필 사진 저장 경로를 따라 storage에 저장
                        storage.reference.child(profileImgUrl).putFile(imgFile!!).addOnCompleteListener { task->
                            if(task.isSuccessful){
                                Log.d("TAG", "새 프로필 사진 업로드 성공")
                            }
                        }
                    }
                    else{
                        Log.d("TAG", "기존 프로필 사진 삭제 실패")
                    }
                }
            }

            // 프로필 사진 변경이 없었을 경우, 새 프로필 사진을 storage에 저장한 경우
            // 현재 변경된 유저 닉네임을 넣어 새로운 userDataModel 객체를 만듦
            // 그 객체를 원래의 유저 데이터 저장 위치에 저장
            val newUserData = userDataModel(uid, edtNickname.text.toString(),profileImgUrl)
            userDBRef.setValue(newUserData)

            startActivity(Intent(this.context, MainActivity::class.java))
        }
    }

    // 갤러리에서 새 프로필 사진 가져오기 위함
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GALLERY_CODE) run {
            imgFile = data!!.data
            // imageView에 갤러리에서 선택한 사진 적용
            profileImgView.setImageURI(imgFile)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_data_edit, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserDataEdit.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserDataEditFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}