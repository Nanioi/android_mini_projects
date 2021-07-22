package com.nanioi.usedtransactionapp.home

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nanioi.usedtransactionapp.DBKey.Companion.DB_ARTICLES
import com.nanioi.usedtransactionapp.R
import com.nanioi.usedtransactionapp.databinding.FragmentHomeBinding

class HomeFragment :Fragment(R.layout.fragment_home){

    private lateinit var articleDB : DatabaseReference
    private lateinit var articleAdapter : ArticleAdapter

    private val articleList = mutableListOf<ArticleModel>()
    private val listener = object :ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val articleModel = snapshot.getValue(ArticleModel::class.java)
            articleModel ?: return
            articleList.add(articleModel)
            articleAdapter.submitList(articleList)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot){}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?){}

        override fun onCancelled(error: DatabaseError){}

    }
    private var binding: FragmentHomeBinding? = null
    private val auth : FirebaseAuth by lazy{
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // activity에서 onCreate 부분
        super.onViewCreated(view, savedInstanceState)

        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        //지역변수로 선언해준 이유 -> binding 변수가 nullable이기 때문 사용할때마다 null을 풀어주는 체크를 해줘야함 -> onViewCreated 안에서만 절대 null이 될 수 없는 변수로 사용하기 위해
        binding = fragmentHomeBinding

        articleList.clear()
        articleDB = Firebase.database.reference.child(DB_ARTICLES)
        articleAdapter = ArticleAdapter()

        fragmentHomeBinding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.articleRecyclerView.adapter = articleAdapter

        //한번등록하면 계속 가능, simple..-> 즉시선언, 한번사용가능
        articleDB.addChildEventListener(listener)
    }

    override fun onResume() { // 뷰가 다시 보일때
        super.onResume()

        articleAdapter.notifyDataSetChanged()
    }
    override fun onDestroyView() { // 뷰에서 나갈때
        super.onDestroyView()

        articleDB.removeEventListener(listener)
    }
}