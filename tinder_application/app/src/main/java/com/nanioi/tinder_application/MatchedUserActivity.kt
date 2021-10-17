package com.nanioi.tinder_application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nanioi.tinder_application.DBkey.Companion.LIKED_BY
import com.nanioi.tinder_application.DBkey.Companion.MATCH
import com.nanioi.tinder_application.DBkey.Companion.NAME
import com.nanioi.tinder_application.DBkey.Companion.USERS

class MatchedUserActivity : AppCompatActivity() {

    private var auth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference
    private val adapter = MatchedUserAdapter()
    private val cardItems = mutableListOf<CardItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matched_user)

        userDB = Firebase.database.reference.child(USERS)
        initMatchedUserRecyclerView()
        getMatchUsers()
    }

    // 매치된 유저 정보 가져오기
    private fun getMatchUsers() {
        val matchedDB = userDB.child(getCurrentUserID()).child(LIKED_BY).child(MATCH) // 내 id의 match 아이디들 가져오기

        matchedDB.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.key?.isNotEmpty()==true){
                    getUserByKey(snapshot.key.orEmpty()) // key로 상대방 이름 가져오는 함수
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?){}
            override fun onChildRemoved(snapshot: DataSnapshot){}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError){}

        })
    }

    // key(상대방 userId)로 상대방 이름 가져오는 함수
    private fun getUserByKey(userId: String) {
        userDB.child(userId).addListenerForSingleValueEvent(object :ValueEventListener{ // 한번만 정보 가져옴
            override fun onDataChange(snapshot: DataSnapshot) {
                cardItems.add(CardItem(userId,snapshot.child(NAME).value.toString()))
                adapter.submitList(cardItems)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initMatchedUserRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.matchedUserRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun getCurrentUserID():String{
        if(auth.currentUser == null){
            Toast.makeText(this,"로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }

}