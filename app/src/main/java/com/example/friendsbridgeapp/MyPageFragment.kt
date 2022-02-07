package com.example.friendsbridgeapp

import android.app.AlertDialog
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
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.net.URL
import kotlin.math.log

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyPageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var profileImageView: ImageView
    private lateinit var txtNickName : TextView
    private lateinit var txtAccount : TextView

    private lateinit var btnUserDataEdit : Button
    private lateinit var btnLogOut : Button

    private lateinit var loginAuth : FirebaseAuth
    private val database = Firebase.database
    private var storage = Firebase.storage
    lateinit var profileUrl : String
    private final val GALLERY_CODE : Int = 10
    private var imgFile : Uri? = null
    private lateinit var edtNickname : EditText
    private lateinit var txtAccount2 : TextView
    private lateinit var profileImageView2 : ImageView
    private lateinit var btnSaveData : Button
    private lateinit var btnWithdraw : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileImageView = getView()!!.findViewById<ImageView>(R.id.profileImgView)
        txtNickName = getView()!!.findViewById<TextView>(R.id.txtNickName)
        txtAccount = getView()!!.findViewById<TextView>(R.id.txtAccount)

        btnUserDataEdit = getView()!!.findViewById<Button>(R.id.btnUserDataEdit)
        btnLogOut = getView()!!.findViewById<Button>(R.id.btnLogOut)

        loginAuth = Firebase.auth
        profileImageView.setImageResource(R.drawable.ic_baseline_person_24)
        profileImageView.background = ShapeDrawable(OvalShape())
        profileImageView.clipToOutline = true

        val myRef = database.getReference("users")
        val userRef = myRef.child(loginAuth.currentUser!!.uid.toString())
        userRef.addValueEventListener(object :
            ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                profileUrl = snapshot.getValue(userDataModel::class.java)!!.profileImgUrl
                txtNickName.setText(snapshot.getValue(userDataModel::class.java)!!.userName)

                val mActivity = activity as MainActivity
                mActivity.receiveUserData(snapshot.getValue(userDataModel::class.java)!!)
                Log.d("TAG", "액티비티로 데이터 전달")

                Firebase.storage.reference.child(profileUrl).downloadUrl.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Glide.with(getView()!!).load(it.result).override(400, 400)
                            .thumbnail(0.1f).into(profileImageView)
                    }
                    else{
                        Log.d("error", "업로드 실패")
                        Glide.with(getView()!!).load(R.drawable.ic_baseline_person_24).override(400,400).into(profileImageView)
                    }
                }
                txtAccount.setText(loginAuth.currentUser!!.email.toString())
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        btnUserDataEdit.setOnClickListener {

        }
        /*
        btnUserDataEdit.setOnClickListener {
            val mDialogView = LayoutInflater.from(context).inflate(R.layout.userdataedit_dialog, null)
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)
                .setTitle("회원 정보 관리")
            val mAlertDialog = mBuilder.show()
            edtNickname = mAlertDialog.findViewById<EditText>(R.id.edtNickName)
            txtAccount2 = mAlertDialog.findViewById<TextView>(R.id.txtAccount)
            profileImageView2 = mAlertDialog.findViewById<ImageView>(R.id.profileImgView)
            profileImageView2.setImageResource(R.drawable.ic_baseline_person_24)
            profileImageView2.background = ShapeDrawable(OvalShape())
            profileImageView2.clipToOutline = true
            /*
            Firebase.storage.reference.child(profileUrl).downloadUrl.addOnCompleteListener {
                if (it.isSuccessful) {
                    Glide.with(mDialogView).load(it.result).override(400, 400)
                        .thumbnail(0.1f).into(profileImageView2)
                }
                else{
                    Log.d("error", "업로드 실패")
                    Glide.with(mDialogView).load(R.drawable.ic_baseline_person_24).override(400,400).into(profileImageView2)
                }
            }

             */
            /*
            btnSaveData = mAlertDialog.findViewById<Button>(R.id.btnSaveData)
            btnWithdraw = mAlertDialog.findViewById<Button>(R.id.btnWithdraw)

            edtNickname.setText(txtNickName.text.toString())
            txtAccount2.setText(txtAccount.text.toString())
            profileImageView2.setOnClickListener {
                val intent : Intent = Intent(Intent.ACTION_PICK)
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
                startActivityForResult(intent, GALLERY_CODE)
            }*/
            /*
            btnSaveData.setOnClickListener {
                val databaseRef = database.reference.child("users").child(loginAuth.currentUser!!.uid)
                val userStorageReference = storage.reference.child("userImgs").child( "ProfileImg/" + loginAuth.currentUser!!.uid)
                databaseRef.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                            val userDB: userDataModel? =
                                snapshot.getValue(userDataModel::class.java)
                            if (userDB!!.profileImgUrl == "/userImgs/ProfileImg/defaultprofileImg.jpg") {
                                userStorageReference.putFile(imgFile!!)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val userDataModel = userDataModel(
                                                edtNickname.text.toString(),
                                                userStorageReference.path.toString()
                                            )
                                            databaseRef
                                                .push()
                                                .setValue(userDataModel)
                                            mAlertDialog.dismiss()
                                        } else {
                                            Log.d("check", "프로필 사진 업로드 실패")
                                            mAlertDialog.dismiss()
                                        }
                                    }
                            } else {
                                userStorageReference.delete().addOnCompleteListener { task ->
                                    if(task.isSuccessful){
                                        userStorageReference.putFile(imgFile!!).addOnCompleteListener { task->
                                            if(task.isSuccessful){
                                                val userDataModel = userDataModel(
                                                    edtNickname.text.toString(),
                                                    userStorageReference.path.toString()
                                                )
                                                databaseRef
                                                    .setValue(userDataModel)
                                                mAlertDialog.dismiss()
                                            }
                                            else{
                                                Log.d("check", "새로운 프로필 사진 업로드 실패")
                                            }
                                        }
                                    }
                                    else{
                                        Log.d("check", "기존 프로필 사진 삭제 실패")
                                        mAlertDialog.dismiss()
                                    }
                                }
                            }
                        }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

                Firebase.storage.reference.child("userImgs/ProfileImg/" + loginAuth.currentUser!!.uid.toString()).delete().addOnCompleteListener {task->
                    if(task.isSuccessful) {
                        Firebase.storage.reference.child("userImgs/ProfileImg/" + loginAuth.currentUser!!.uid.toString())
                            .putFile(imgFile!!).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    database.reference.child("users").child(loginAuth.currentUser!!.uid)
                                        .child("profileImgUrl")
                                        .setValue("userImgs/ProfileImg/" + loginAuth.currentUser!!.uid.toString())
                                }
                                else{
                                Log.d("check", "올리기 실패")
                                }
                        }
                    }
                    else{
                        Log.d("check", "삭제 실패")
                    }
                }
            }*/
        }*/
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GALLERY_CODE) run {
            imgFile = data!!.data
            profileImageView2.setImageURI(imgFile)
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