package com.example.friendsbridgeapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*


class MemoFragment : Fragment() {

    val dataModelList = mutableListOf<DataModel>()

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val database = Firebase.database // 파이어베이스 데이터베이스 담을 변수
        val myRef = database.getReference("myMemo") //비공개 데이터베이스 경로 변수 
        val toAll = database.getReference("ToAll") // 전체 공개 데이터베이스 경로 변수

        val listView = getView()!!.findViewById<ListView>(R.id.mainLV) // 리스트뷰 변수

        val adapterList = ListViewAdapter(dataModelList) // dataModelList 정보를 담은 list형 adapter
        listView.adapter = adapterList // list형 adapter에 담긴 정보를 listview adapter로 전달

        Log.d("DataModel------", dataModelList.toString()) // dataModelList 정보를 log에 찍기

        myRef.child(Firebase.auth.currentUser!!.uid).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("point", dataModelList.toString())
                dataModelList.clear()
                Log.d("point", dataModelList.toString())

                for (dataModel in snapshot.children) {
                    Log.d("Data", dataModel.toString())
                    dataModelList.add(dataModel.getValue(DataModel::class.java)!!)

                }
                adapterList.notifyDataSetChanged()
                Log.d("DataModel", dataModelList.toString()) // dataModelList 정보를 log에 찍기

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        Log.d("DataModel------", dataModelList.toString())

        toAll.addValueEventListener(object :
                ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("point", dataModelList.toString())
                dataModelList.clear()
                Log.d("point", dataModelList.toString())

                for (dataModel in snapshot.children) {
                    Log.d("Data", dataModel.toString())
                    dataModelList.add(dataModel.getValue(DataModel::class.java)!!)

                }
                adapterList.notifyDataSetChanged()
                Log.d("DataModel", dataModelList.toString())

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

        // 글 작성 버튼 변수 선언
        val writeButton = getView()!!.findViewById<ImageView>(R.id.writeBtn)

        // 이미지 버튼이 클릭되면 이벤트 실행
        writeButton.setOnClickListener {

            val mDialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null)
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)
                .setTitle("메모 다이얼로그")

            // 다이얼로그 변수 선언
            val mAlertDialog = mBuilder.show()

            // 날짜 선택 버튼을 다이얼로그로 불러오는 변수 선언
            val DateSelectBtn =  mAlertDialog.findViewById<Button>(R.id.dateSelectBtn)

            // 선택한 날짜 text로 담을 변수 선언
            var dateText = ""

            // 날짜 선택 버튼이 클릭되면 이벤트 실행
            DateSelectBtn?.setOnClickListener {
                val today = GregorianCalendar() // GregorianCalendar를 가져와서 날짜 설정
                val year:Int = today.get(Calendar.YEAR) // 년도 설정
                val month:Int = today.get(Calendar.MONTH) // 달 설정
                val date:Int = today.get(Calendar.DATE) // 날짜 설정

                
                // 날짜 선택 버튼이 눌렸을 때 다이얼로그 이벤트 함수
                val dlg = DatePickerDialog(context!!, object : DatePickerDialog.OnDateSetListener {
                    // 날짜 선택 함수 구현
                    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int)
                    {
                        // 선택한 날짜 log에 찍기
                        Log.d("MAIN", "${year}, ${month + 1}, ${dayOfMonth}")

                        // 날짜 선택 버튼을 선택한 날짜로 설정
                        DateSelectBtn.setText("${year}, ${month + 1}, ${dayOfMonth}")

                        // dateText 변수에 선택한 날짜 저장
                        dateText = "${year}, ${month + 1}, ${dayOfMonth}"
                    }

                }       , year, month, date) // 선택된 년, 월, 일 정보 set
                
                // 다이얼로그 보여주기
                dlg.show()
            }

            val saveBtn = mAlertDialog.findViewById<Button>(R.id.saveBtn)
            saveBtn?.setOnClickListener {

                val Memo = mAlertDialog.findViewById<EditText>(R.id.Memo).text.toString()

                val database = Firebase.database
                val myRef = database.getReference("myMemo").child(Firebase.auth.currentUser!!.uid)

                // model 변수에 날짜와 내용을 DataModel화 후 저장
                val model = DataModel(dateText, Memo)

                myRef // 경로
                    .push()     // 정보를 계속 추가
                    .setValue(model)    // 값을 model에 저장

                mAlertDialog.dismiss()      // 다이얼로그 사라짐


            }

            val saveBtn2 = mAlertDialog.findViewById<Button>(R.id.saveBtn2)
            saveBtn2?.setOnClickListener {

                val Memo2 = mAlertDialog.findViewById<EditText>(R.id.Memo).text.toString()

                val database = Firebase.database
                val toAll = database.getReference("ToAll")

                // model 변수에 날짜와 내용을 DataModel화 후 저장
                val model = DataModel(dateText, Memo2)

                toAll // 경로
                        .push()     // 정보를 계속 추가
                        .setValue(model) // 값을 model에 저장

                mAlertDialog.dismiss()      // 다이얼로그 사라짐


            }

            // 글 작성 취소 버튼 설정
            val cancelBtn = mAlertDialog.findViewById<Button>(R.id.cancelBtn)
            
            // cancelBtn 클릭되면 이벤트 실행
            cancelBtn?.setOnClickListener {
                mAlertDialog.dismiss()     // 다이얼로그 사라짐
            }


        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment

        // 메모 프래그먼트 레이아웃 inflate
        return inflater.inflate(R.layout.fragment_memo, container, false)
    }


}