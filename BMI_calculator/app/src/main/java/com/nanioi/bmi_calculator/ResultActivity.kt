package com.nanioi.bmi_calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlin.math.pow

class ResultActivity : AppCompatActivity() {

    private val bmiValueTextView: TextView by lazy {
        findViewById(R.id.bmiValueTextView)
    }
    private val bmiResultTextView: TextView by lazy {
        findViewById(R.id.bmiResultTextView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val height = intent.getIntExtra("height", 0)
        val weight = intent.getIntExtra("weight", 0)

        val bmi = weight / (height / 100.0).pow(2.0)

        val resultText = when {
            bmi >= 35.0 -> "고도 비만 입니다."
            bmi >= 35.0 -> "중정 비만 입니다."
            bmi >= 35.0 -> "경도 비만 입니다."
            bmi >= 35.0 -> "과체중 입니다."
            bmi >= 35.0 -> "정상체중 입니다."
            else -> "저체중 입니다."
        }

        bmiValueTextView.text = bmi.toString()
        bmiResultTextView.text = resultText

    }
}