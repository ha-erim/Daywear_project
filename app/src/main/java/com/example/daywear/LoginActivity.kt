package com.example.daywear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        val findbtn = findViewById<Button>(R.id.find_btn)
        findbtn.setOnClickListener {
            val intentfind = Intent(this,FindActivity::class.java)
            startActivity(intentfind)
        }

        val joinbtn = findViewById<Button>(R.id.join_btn)
        joinbtn.setOnClickListener {
            val intent = Intent(this,JoinActivity::class.java)
            startActivity(intent)
        }

        val loginbtn = findViewById<Button>(R.id.login_btn)
        loginbtn.setOnClickListener {
//            val intent = Intent(this,MainActivity::class.java)
//            startActivity(intent)
            val email = findViewById<EditText>(R.id.email_area)
            val pw = findViewById<EditText>(R.id.pw_area)
            signIn(email.text.toString(),pw.text.toString())
        }
    }

    //로그아웃하지 않을 시 자동 로그인, 회원가입시 바로 로그인?
    public override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
    }

    //로그인
    private fun signIn(email:String, password:String){
        if(email.isNotEmpty() && password.isNotEmpty()){
            auth?.signInWithEmailAndPassword(email,password)
                ?.addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){
                        Toast.makeText(this,"로그인 성공", Toast.LENGTH_LONG).show()
                        moveMainPage(auth?.currentUser)
                    }
                    else{
                        Toast.makeText(this,"로그인 실패.",Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    //유저 정보 넘겨주고 메인 액티비티 호출
    fun moveMainPage(user:FirebaseUser?){
        if(user!=null){
            startActivity(Intent(this,MainActivity::class.java))
            finish() //현재 액티비티 제거
        }
    }
}