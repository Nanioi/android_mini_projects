package com.nanioi.usedtransactionapp.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nanioi.usedtransactionapp.DBKey.Companion.CHILD_CHAT
import com.nanioi.usedtransactionapp.DBKey.Companion.DB_ARTICLES
import com.nanioi.usedtransactionapp.DBKey.Companion.DB_USERS
import com.nanioi.usedtransactionapp.R
import com.nanioi.usedtransactionapp.chat.ChatListItem
import com.nanioi.usedtransactionapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var articleDB: DatabaseReference
    private lateinit var userDB: DatabaseReference
    private lateinit var articleAdapter: ArticleAdapter

    private val articleList = mutableListOf<ArticleModel>()
    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val articleModel = snapshot.getValue(ArticleModel::class.java) // ArticleModel 클래스로 데이터를 받아옴
            articleModel ?: return
            articleList.add(articleModel)
            articleAdapter.submitList(articleList)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}

    }
    private var binding: FragmentHomeBinding? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // activity에서 onCreate 부분
        super.onViewCreated(view, savedInstanceState)

        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        //지역변수로 선언해준 이유 -> binding 변수가 nullable이기 때문 사용할때마다 null을 풀어주는 체크를 해줘야함 -> onViewCreated 안에서만 절대 null이 될 수 없는 변수로 사용하기 위해
        binding = fragmentHomeBinding

        articleList.clear() // item 리스트 초기
        userDB = Firebase.database.reference.child(DB_USERS)
        articleDB = Firebase.database.reference.child(DB_ARTICLES)

        articleAdapter = ArticleAdapter(onItemClicked = { articleModel ->
            if (auth.currentUser != null) {
                // 로그인을 한 상태
                if (auth.currentUser!!.uid != articleModel.sellerId) {
                    val chatRoom = ChatListItem(
                            buyerId = auth.currentUser!!.uid,
                            sellerId = articleModel.sellerId,
                            itemTitle = articleModel.title,
                            key=System.currentTimeMillis()
                    )
                    userDB.child(auth.currentUser!!.uid)
                            .child(CHILD_CHAT)
                            .push()
                            .setValue(chatRoom)
                    userDB.child(articleModel.sellerId)
                            .child(CHILD_CHAT)
                            .push()
                            .setValue(chatRoom)
                    Snackbar.make(view, "채팅방이 생성되었습니다. 채팅탭에서 확인해주세요.", Snackbar.LENGTH_LONG).show()

                } else {
                    // 내가 올린 아이템
                    Snackbar.make(view, "내가 올린 아이템입니다.", Snackbar.LENGTH_LONG).show()
                }
            } else {
                Snackbar.make(view, "로그인 후 사용해주세요", Snackbar.LENGTH_LONG).show()
            }
        })

        fragmentHomeBinding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.articleRecyclerView.adapter = articleAdapter

        fragmentHomeBinding.addFloatingButton.setOnClickListener {
            if (auth.currentUser != null) {
                val intent = Intent(requireContext(), AddArticleActivity::class.java) //context가 널일 수 있어 requireContext 사용
                startActivity(intent) // 아이템 등록 창으로 이동
            } else {
                Snackbar.make(view, "로그인 후 사용해주세요", Snackbar.LENGTH_LONG).show()
            }
        }

        //한번등록하면 계속 가능, simple..-> 즉시선언, 한번사용가능
        articleDB.addChildEventListener(listener)
    }

    override fun onResume() { // 뷰가 다시 보일때
        super.onResume()

        articleAdapter.notifyDataSetChanged() // 데이터 다시 불러와 뷰 다시 그리기
    }

    override fun onDestroyView() { // 뷰에서 나갈때
        super.onDestroyView()

        articleDB.removeEventListener(listener)
    }
}