package com.example.daywear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SubJoinActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_join)

        database = Firebase.database.reference
        val user = Firebase.auth.currentUser // 데이터베이스에서 현재 사용자 불러
//        user?.let {
//            val uid = user.uid
//        }
        val uid = user?.uid
        val name = findViewById<EditText>(R.id.edit_name)
        val pnumber = findViewById<EditText>(R.id.edit_PN)
        val spinner = findViewById<Spinner>(R.id.spinner)

        //어댑터 설정
        spinner.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.array_1,
            android.R.layout.simple_spinner_item
        )

        val registerbtn = findViewById<Button>(R.id.btn_register)
        registerbtn.setOnClickListener {
            writeNewUser(uid.toString(),name.text.toString(),pnumber.text.toString(),spinner.selectedItem.toString())
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }


    }
    data class User(val username : String? = null, val number: String? = null,val region: String? = null){
        fun toMap(): Map<String, Any?> {
            return mapOf(
                "username" to username,
                "number" to number,
                "region" to region,
            )
        }
    }

    fun writeNewUser(userId: String, name : String, number:String, region:String){
        val user = User(name,number,region)
        database.child("users").child(userId).setValue(user)

    }
}