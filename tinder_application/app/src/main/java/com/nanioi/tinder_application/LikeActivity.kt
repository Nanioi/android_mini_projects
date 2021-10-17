package com.nanioi.tinder_application

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nanioi.tinder_application.DBkey.Companion.DIS_LIKE
import com.nanioi.tinder_application.DBkey.Companion.LIKE
import com.nanioi.tinder_application.DBkey.Companion.LIKED_BY
import com.nanioi.tinder_application.DBkey.Companion.MATCH
import com.nanioi.tinder_application.DBkey.Companion.NAME
import com.nanioi.tinder_application.DBkey.Companion.USERS
import com.nanioi.tinder_application.DBkey.Companion.USER_ID
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction

class LikeActivity : AppCompatActivity(),CardStackListener {
    private var auth :FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB:DatabaseReference
    private val adapter = CardItemAdapter()
    private val cardItems = mutableListOf<CardItem>()
    private val manager by lazy {
        CardStackLayoutManager(this,this)
    } // 사용시 추가

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like)

        userDB = Firebase.database.reference.child(USERS)

        val currentUserDB = userDB.child(getCurrentUserID())
        // currentUserDB에서 값 받아오는 방법 -> listner 달기
        currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(NAME).value==null){
                    showNameInputPopup()
                    return
                }
                getUnSelectedUsers() //유저정보 갱신
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        initCardStackView()
        initMatchedListButton()
        initSignOutButton()
    }

    private fun initCardStackView() {
        val stackView = findViewById<CardStackView>(R.id.cardStackView)

        stackView.layoutManager = manager  // 이때 manager 처음으로 사용하면서 초기화됨
        stackView.adapter = adapter
    }
    private fun initMatchedListButton() {
        val matchedListButton = findViewById<Button>(R.id.matchListButton)

        matchedListButton.setOnClickListener {
            startActivity(Intent(this,MatchedUserActivity::class.java))
        }
    }
    private fun initSignOutButton() {
        val signOutButton = findViewById<Button>(R.id.signOutButton)

        signOutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }


    private fun getUnSelectedUsers() {
        userDB.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.child(USER_ID).value != getCurrentUserID()
                        && snapshot.child(LIKED_BY).child(LIKE).hasChild(getCurrentUserID()).not()
                        && snapshot.child(LIKED_BY).child(DIS_LIKE).hasChild(getCurrentUserID()).not() ){
                    // 내가 한번도 선택하지 않은 유저

                    val userId = snapshot.child(USER_ID).value.toString()
                    var name = "undecided" // 초기값
                    if(snapshot.child(NAME).value != null){
                        name = snapshot.child(NAME).value.toString()
                    }
                    cardItems.add(CardItem(userId,name))
                    adapter.submitList(cardItems)
                    adapter.notifyDataSetChanged()
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //변경이 된 유저 정보
                cardItems.find{ it.userId == snapshot.key}?.let{
                    it.name = snapshot.child(NAME).value.toString()
                }
                adapter.submitList(cardItems)
                adapter.notifyDataSetChanged() // 데이터 갱신
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun showNameInputPopup() {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("이름을 입력해주세요")
            .setView(editText)
            .setPositiveButton("저장"){_,_ ->
                if (editText.text.isEmpty()){
                    showNameInputPopup()
                }else{
                    saveUserName(editText.text.toString())
                }
            }
            .setCancelable(false)
            .show()
    }

    private fun saveUserName(name: String) {
        val userId = getCurrentUserID()
        val currentUserDB = userDB.child(userId)
        val user = mutableMapOf<String,Any>()
        user[USER_ID]=userId
        user[NAME]=name
        currentUserDB.updateChildren(user)

        //입력받은 이름 추가하여 유저정보 업데이트
        getUnSelectedUsers()
    }

    private fun getCurrentUserID():String{
        if(auth.currentUser == null){
            Toast.makeText(this,"로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }

    // like/dislike 처리함수
    private fun like(){
        val card = cardItems[manager.topPosition -1]
        cardItems.removeFirst() // 아이템 지워버림

        //상대방 like에 현재 유저id child 추가 후 true 값 저장
        userDB.child(card.userId)
            .child(LIKED_BY)
            .child(LIKE)
            .child(getCurrentUserID())
            .setValue(true)

        //상대방이 나를 좋아한 사람이 있는지
        saveMatchIfOtherUserLikedMe(card.userId)

        Toast.makeText(this,"${card.name}님을 Like하셨습니다.",Toast.LENGTH_SHORT).show()
    }
    private fun dislike(){
        val card = cardItems[manager.topPosition -1]
        cardItems.removeFirst()

        userDB.child(card.userId)
                .child(LIKED_BY)
                .child(DIS_LIKE)
                .child(getCurrentUserID())
                .setValue(true)

        Toast.makeText(this,"${card.name}님을 disLike하셨습니다.",Toast.LENGTH_SHORT).show()
    }
    //매칭된 시점 보기
    private fun saveMatchIfOtherUserLikedMe(otherUserId: String) {
        val isLikedMeOtherUserDB = userDB.child(getCurrentUserID()).child(LIKED_BY).child(LIKE).child(otherUserId) // 나를 좋아한 사람들

        isLikedMeOtherUserDB.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value == true) { // 상대방이 나를 좋아함 ( 내가 좋아요 누른 사람들을 보는 것이므로 매치됨 )
                    userDB.child(getCurrentUserID())
                        .child(LIKED_BY)
                        .child(MATCH)
                        .child(otherUserId)
                        .setValue(true) // 내 id match -> 매치된 상대방 id 저장

                    userDB.child(otherUserId)
                        .child(LIKED_BY)
                        .child(MATCH)
                        .child(getCurrentUserID()) // 매치된 상대방 id에 내 id 저장
                        .setValue(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {}

    // 카드 스와이프 시 이벤트 처리 함수
    override fun onCardSwiped(direction: Direction?) {
        when(direction){
            Direction.Right -> like()
            Direction.Left -> dislike()
            else->{
            }
        }
    }
    override fun onCardRewound() {}

    override fun onCardCanceled() {}

    override fun onCardAppeared(view: View?, position: Int) {}

    override fun onCardDisappeared(view: View?, position: Int) {}
}