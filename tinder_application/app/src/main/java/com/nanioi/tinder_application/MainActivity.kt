package com.nanioi.tinder_application

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private var auth : FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        if(auth.currentUser == null){ // 로그인이 안되어 있는 경우
            startActivity(Intent(this,LoginActivity::class.java))
        }else{
            startActivity(Intent(this,LikeActivity::class.java))
        }
    }
}