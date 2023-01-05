package com.example.daywear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class SetActivity : AppCompatActivity() {
    private var auth : FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        setContentView(R.layout.activity_set)

        //프로필
        val profile = findViewById<TextView>(R.id.set_profile)
        profile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        //지역 재설정
        val areaset = findViewById<TextView>(R.id.set_area)
        areaset.setOnClickListener {
            val intent = Intent(this, areaSetActivity::class.java)
            startActivity(intent)
        }

        //비밀번호 재설정
        val pwset = findViewById<TextView>(R.id.set_pw)
        pwset.setOnClickListener {
            val intent = Intent(this, pwSetActivity::class.java)
            startActivity(intent)
        }

        //로그아웃
        val logout = findViewById<TextView>(R.id.logout)
        logout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            auth?.signOut()
        }

        // 탈퇴하기 클릭 시 이벤트 처리
        val out = findViewById<TextView>(R.id.set_out)
        out.setOnClickListener {
            val intent = Intent(this, OutActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}