package com.nanioi.usedtransactionapp.chat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nanioi.usedtransactionapp.DBKey.Companion.CHILD_CHAT
import com.nanioi.usedtransactionapp.DBKey.Companion.DB_USERS
import com.nanioi.usedtransactionapp.R
import com.nanioi.usedtransactionapp.chatDetail.ChatRoomActivity
import com.nanioi.usedtransactionapp.databinding.FragmentChatBinding
import com.nanioi.usedtransactionapp.home.ArticleAdapter
import com.nanioi.usedtransactionapp.home.ChatListAdapter

class ChatListFragment :Fragment(R.layout.fragment_chat){

    private var binding: FragmentChatBinding?=null
    private lateinit var chatListAdapter: ChatListAdapter
    private val chatRoomList = mutableListOf<ChatListItem>()

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val  fragmentChatBinding = FragmentChatBinding.bind(view)
        binding = fragmentChatBinding

        chatListAdapter = ChatListAdapter(onItemClicked = { chatRoom->
            //채팅방으로 이동하는 코드

            context?.let {
                val intent = Intent(it,ChatRoomActivity::class.java)
                intent.putExtra("chatKey",chatRoom.key)
                startActivity(intent)
            }
        })

        chatRoomList.clear()

        fragmentChatBinding.chatListRecyclerView.adapter = chatListAdapter
        fragmentChatBinding.chatListRecyclerView.layoutManager=LinearLayoutManager(context)

        if(auth.currentUser == null){
            return
        }
        val chatDB = Firebase.database.reference.child(DB_USERS).child(auth.currentUser!!.uid).child(CHILD_CHAT)

        chatDB.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val model = it.getValue(ChatListItem::class.java)
                    model ?: return

                    chatRoomList.add(model)
                }
                chatListAdapter.submitList(chatRoomList)
                chatListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onResume() {
        super.onResume()

        chatListAdapter.notifyDataSetChanged()
    }
}