package com.example.daywear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class JoinActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        auth = Firebase.auth

//        if(auth.currentUser?.uid == null){
//            //회원가입 안되어 있으므로 join activity
//            val intent = Intent(this,JoinActivity::class.java)
//            startActivity(intent)
//        }
//        else{
//            //회원가입 되어있으므로 메인 액티비티
//            val intent = Intent(this,MainActivity::class.java)
//            startActivity(intent)
//        }

        val joinbtn = findViewById<Button>(R.id.btn_register)
        joinbtn.setOnClickListener {
//            val name = findViewById<EditText>(R.id.edit_name)
//            val phonenumber = findViewById<EditText>(R.id.edit_PN)
            val email = findViewById<EditText>(R.id.edit_email)
            val pw = findViewById<EditText>(R.id.edit_pw)

            auth.createUserWithEmailAndPassword(email.text.toString(), pw.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,"회원가입 완료.",Toast.LENGTH_LONG).show()
                        // Sign in success, update UI with the signed-in user's information
                        val intent = Intent(this,SubJoinActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("JoinActivity", "createUserWithEmail:failure", task.exception)

                    }
                }
        }
    }
}