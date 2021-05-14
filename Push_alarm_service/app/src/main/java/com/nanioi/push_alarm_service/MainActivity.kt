package com.nanioi.push_alarm_service

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private val resultTextView:TextView by lazy{
        findViewById(R.id.resultTextView)
    }
    private val firebaseToken:TextView by lazy{
        findViewById(R.id.firebaseTokenTextView)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}