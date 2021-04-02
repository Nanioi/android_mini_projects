package com.nanioi.secretdiary

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.EditText
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener

class diaryActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper()) //메인쓰레드에 연결된 핸들러 만듦

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        val diaryEditText = findViewById<EditText>(R.id.diaryEdit)
        val detailPreferences = getSharedPreferences("diary", Context.MODE_PRIVATE)

        diaryEditText.setText(detailPreferences.getString("detail",""))

        val runnable = Runnable {
            getSharedPreferences("diary",Context.MODE_PRIVATE).edit {
                putString("detail",diaryEditText.text.toString())
            }

            Log.d("DiaryActivity","SAVE!!!!!!! ${diaryEditText.text.toString()}")
        }// 작성 시 한자한자 빈번하게 저장하는 것이 아닌 멈칫 했을경우 저장할 수 있도록 thread사용

        diaryEditText.addTextChangedListener {

            Log.d("DiaryActivity","TextChange :: $it")
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable,500)
        }
    }
}