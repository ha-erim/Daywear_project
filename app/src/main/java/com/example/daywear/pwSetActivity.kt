package com.example.daywear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class pwSetActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_pw_set)
//
//        val btn_setting = findViewById<Button>(R.id.btn_setPw)
//
//        btn_setting.setOnClickListener {
//            Toast.makeText(this, "설정 완료", Toast.LENGTH_LONG).show()
//
//            var intent = Intent(this, SetActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pw_set)

        val btn_setting = findViewById<Button>(R.id.btn_setPw)
        val txt_newPw1 = findViewById<TextInputEditText>(R.id.textInput_newPw)
        val txt_newPw2 = findViewById<TextInputEditText>(R.id.textInput_checkNewPw)
        //val lyt_pw1 = findViewById<TextInputLayout>(R.id.textInputLayout2)
        //val lyt_pw2 = findViewById<TextInputLayout>(R.id.textInputLayout3)
        val btn_back = findViewById<ImageView>(R.id.btn_back)
        val user = Firebase.auth.currentUser // DB에서 현자 사용자 불러오기


        // 설정 완료 버튼 비활성화
        btn_setting.isEnabled = false

        // 새 비밀번호 입력 시 이벤트 처리
        txt_newPw1.addTextChangedListener() {

            // 비밀번호 입력이 다 끝났을 때
            txt_newPw1.doAfterTextChanged {
                // 글자수를 만족하지 못 할 경우
                if (txt_newPw1.length() < 8 || txt_newPw1.length() > 12) {
                    Toast.makeText(this, "비밀번호를 8 ~ 12자 내로 설정해주세요.", Toast.LENGTH_LONG).show()
                } // 글자수를 만족할 경우
                else {

                }
            }


        }

        // 새 비밀번호 재입력 시 이벤트 처리
        txt_newPw2.addTextChangedListener() {

            // 비밀번호 입력이 다 끝났을 때
            txt_newPw2.doAfterTextChanged {

                // 비밀번호 일치여부 확인
                // 1. 일치할 경우
                if (txt_newPw1.text.toString().equals(txt_newPw2.text.toString())) {
                    //txt_newPw2.setText("비밀번호가 일치합니다.")

                    // 레이아웃 박스 색 변경 (초록색)

                    // 설정 완료 버튼 활성화
                    btn_setting.isEnabled = true

                    btn_setting.setOnClickListener {
                        // DB 비밀번호 업데이트

                        val newPassword = txt_newPw2.text.toString()

                        user!!.updatePassword(newPassword)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "비밀번호 업데이트 완료", Toast.LENGTH_LONG).show()
                                }
                                else{
                                    Toast.makeText(this, "업데이트 실패", Toast.LENGTH_LONG).show()
                                }
                            }
                        finish()
                    }
                }

                // 2. 일치하지 않을 경우
                else {
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show()

                    // 레이아웃 박스 색 변경 (빨간색)
                    //txt_newPw2.setTextColor(ContextCompat.getColor()

                }
            }


        }


        // 설정 완료 버튼 클릭 시 이벤트 처리
//        btn_setting.setOnClickListener {
//            Toast.makeText(this, "비밀번호 변경 완료", Toast.LENGTH_LONG).show()
//            finish()
//        }

        //뒤로가기 버튼 클릭 시 이벤트 처리
        btn_back.setOnClickListener {
            finish()
        }
    }

    //avd 내  뒤로가기 클릭 시 이벤트 처리
    override fun onBackPressed() {
        finish()
    }

}

