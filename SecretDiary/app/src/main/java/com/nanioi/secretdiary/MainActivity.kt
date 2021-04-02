package com.nanioi.secretdiary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    private val numberPicker1 : NumberPicker by lazy{
        findViewById<NumberPicker>(R.id.numberPicker1)
                .apply{
                    minValue = 0
                    maxValue = 9
                }
    }
    private val numberPicker2 : NumberPicker by lazy{
        findViewById<NumberPicker>(R.id.numberPicker2)
                .apply{
                    minValue = 0
                    maxValue = 9
                }
    }
    private val numberPicker3 : NumberPicker by lazy{
        findViewById<NumberPicker>(R.id.numberPicker3)
                .apply{
                    minValue = 0
                    maxValue = 9
                }
    }

    private val openButton : AppCompatButton by lazy{
        findViewById<AppCompatButton>(R.id.openButton)
    }
    private val changePWButton : AppCompatButton by lazy{
        findViewById<AppCompatButton>(R.id.changePWButton)
    }
    private var changePWMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberPicker1
        numberPicker2
        numberPicker3

        openButton.setOnClickListener{
            if(changePWMode){
                Toast.makeText(this,"비밀번호 변경 중",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val pwPreferences = getSharedPreferences("password", Context.MODE_PRIVATE)

            val pwFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

            if( pwPreferences.getString("password","000").equals(pwFromUser)){
                startActivity(Intent(this, diaryActivity::class.java))
            }else{
                showErrorAlertDialog()
            }
        }
        changePWButton.setOnClickListener{
            val pwPreferences = getSharedPreferences("password", Context.MODE_PRIVATE)
            val pwFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"
            if(changePWMode){
                //번호 저장하는 기능
                pwPreferences.edit(true){
                    putString("password",pwFromUser)
                }

                changePWMode = false
                changePWButton.setBackgroundColor(Color.BLACK)
            }else{
                //changePWMode 활성화, 비밀번호 맞는지 체크
                if( pwPreferences.getString("password","000").equals(pwFromUser)){
                    changePWMode = true
                    Toast.makeText(this,"변경할 패스워드는 입력해주세요",Toast.LENGTH_SHORT).show()

                    changePWButton.setBackgroundColor(Color.RED)
                }else{
                    showErrorAlertDialog()
                }
            }
        }
    }
    private fun showErrorAlertDialog(){
        AlertDialog.Builder(this)
                .setTitle("!실패!")
                .setMessage("비밀번호가 잘못되었습니다.")
                .setPositiveButton("확인"){ _,_ -> }
                .create()
                .show()
    }
}