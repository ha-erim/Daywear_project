package com.example.daywear

import android.content.ContentValues.TAG
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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class OutActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_out)

        val btn_withdrawal = findViewById<Button>(R.id.btn_withdrawal)
        val txt_pw = findViewById<TextInputEditText>(R.id.textInput_pw)
        val btn_back = findViewById<ImageView>(R.id.btn_back)

        val user = Firebase.auth.currentUser!! //현재 사용자 불러오기
        val email = user?.email
        val uid = user?.uid

        // 설정 완료 버튼 비활성화
        btn_withdrawal.isEnabled = false

        // 현재 비밀번호 입력 시 이벤트 처리
        txt_pw.addTextChangedListener {
            // 비밀번호 입력이 다 끝났을 때
            txt_pw.doAfterTextChanged {
               // signIn(email.toString(),txt_pw.toString())

                // DB에서 불러온 기존 비밀번호와 입력한 현재 비밀번호 일치 여부 확인
//                if (txt_pw.text.toString().equals(/* DB에서 불러온 비밀번호 */) {
//
                //탈퇴하기 버튼 활성화
                btn_withdrawal.isEnabled = true
//
//                    }
//                else {
//
//                }
            }
        }


        // 탈퇴하기 버튼 클릭 시 이벤트 처리
        btn_withdrawal.setOnClickListener {

            //사용자 재인증
            val credential = EmailAuthProvider
                .getCredential(email.toString(), txt_pw.toString())
            user.reauthenticate(credential)
                .addOnCompleteListener { Log.d(TAG, "User re-authenticated.") }

            //DB에서 사용자 삭제
            user.delete()
                .addOnCompleteListener { task->
                    if (task.isSuccessful) {
                        //realtime database에 저장된 사용자 정보 삭제
                        database = Firebase.database.reference
                        database.child("users").child(uid.toString()).removeValue()
                            .addOnSuccessListener {
                                Log.d(TAG, "realtime-database deleted")
                            }
                            .addOnFailureListener {
                                Log.d(TAG, "deletion failed")
                            }

                        Log.d(TAG, "User account deleted.")
                        Toast.makeText(this, "계정이 삭제되었습니다.", Toast.LENGTH_LONG).show()
                        //삭제 후 스택에 쌓여있는 액티비티 모두 삭제 후 회원가입 창으로 돌아감
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }
                    else{
                        Log.d(TAG, "User deletion failed.")
                        Toast.makeText(this, "계정을 삭제할 수 없습니다.", Toast.LENGTH_LONG).show()
                    }
                }




        }

        //뒤로가기 버튼 클릭 시 이벤트 처리
        btn_back.setOnClickListener {
            finish()
        }

    }
    //비밀번호 확인을 로그인으로...?
    private fun signIn(email:String, password:String){
        if(email.isNotEmpty() && password.isNotEmpty()){
            auth?.signInWithEmailAndPassword(email,password)
                ?.addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){
                        Toast.makeText(this,"비밀번호 일치", Toast.LENGTH_LONG).show()

                    }
                    else{
                        Toast.makeText(this,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}