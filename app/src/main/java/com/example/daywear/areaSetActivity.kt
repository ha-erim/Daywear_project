package com.example.daywear

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

open class areaSetActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_area_set)
//
//        val btn_setting = findViewById<Button>(R.id.btn_setArea)
//
//        btn_setting.setOnClickListener {
//            Toast.makeText(this, "설정 완료", Toast.LENGTH_LONG).show()
//
//            var intent = Intent(this, SetActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//    }
//    var nx = "89"
//    var ny = "90"

    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_area_set)

        database = Firebase.database.reference
        val user = Firebase.auth.currentUser // 데이터베이스에서 현재 사용자 불러

        val btn_back = findViewById<ImageView>(R.id.btn_back)
        val btn_setting = findViewById<Button>(R.id.btn_setArea)
        val spinner = findViewById<Spinner>(R.id.spinner)

        //어댑터 설정
        spinner.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.array_1,
            android.R.layout.simple_spinner_item
        )

        //스피너 선택 시 이벤트 처리
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selected_area = spinner.selectedItem.toString()
                //setCoordinates(selected_area)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        val uid = user?.uid
        //설정 완료 버튼 클릭시 이벤트 처리
        btn_setting.setOnClickListener {
            updateRegion(uid.toString(),spinner.selectedItem.toString())

            Toast.makeText(this, "설정 완료", Toast.LENGTH_LONG).show()
            //설정완료와 동시에 해당화면 종료


            //설정완료와 동시에 이전 화면으로 넘어감
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }

        //뒤로가기 버튼 클릭 시 이벤트 처리
        btn_back.setOnClickListener {
            finish()
        }

    }
    private fun updateRegion(uid:String,region: String) {
        val childUpdates = hashMapOf<String,Any>(
            "/users/$uid/region" to region
        )
        database.updateChildren(childUpdates)
    }

    // 지역별로 좌표 변경하기
//    fun setCoordinates(selected_area: String){
//
//        if (selected_area == "대구광역시"){
//            // 좌표 설정
//            nx = "89"
//            ny = "90"
//        }
//        else if (selected_area == "경상북도"){
//            nx = "89"
//            ny = "91"
//        }
//        else if (selected_area == "경상남도"){
//            nx = "91"
//            ny = "77"
//        }
//        else if (selected_area == "서울특별시"){
//            nx = "60"
//            ny = "127"
//        }
//        else if (selected_area == "경기도"){
//            nx = "60"
//            ny = "120"
//        }
//        else if (selected_area == "강원도"){
//            nx = "73"
//            ny = "134"
//        }
//        else if (selected_area == "세종특별자치시"){
//            nx = "66"
//            ny = "103"
//        }
//        else if (selected_area == "인천광역시"){
//            nx = "55"
//            ny = "124"
//        }
//        else if (selected_area == "충청북도"){
//            nx = "69"
//            ny = "107"
//        }
//        else if (selected_area == "충청남도"){
//            nx = "68"
//            ny = "100"
//        }
//        else if (selected_area == "전라북도"){
//            nx = "63"
//            ny = "89"
//        }
//        else if (selected_area == "전라남도"){
//            nx = "51"
//            ny = "67"
//        }
//        else if (selected_area == "울산광역시"){
//            nx = "102"
//            ny = "84"
//        }
//        else if (selected_area == "부산광역시"){
//            nx = "98"
//            ny = "76"
//        }
//        else if (selected_area == "광주광역시"){
//            nx = "58"
//            ny = "74"
//        }
//        else if (selected_area == "제주특별자치도"){
//            nx = "52"
//            ny = "38"
//        }
//
//
//    }

}

