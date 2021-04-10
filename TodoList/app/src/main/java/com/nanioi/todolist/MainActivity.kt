package com.nanioi.todolist

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nanioi.todolist.databinding.ActivityMainBinding
import com.nanioi.todolist.databinding.ItemTodoBinding

class MainActivity : AppCompatActivity() {
    val RC_SIGN_IN = 1000
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //로그인 안됨
        if (FirebaseAuth.getInstance().currentUser == null) {
            login()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = TodoAdapter(
                emptyList(),
                onClickDeleteIcon = { todo ->
                    viewModel.deleteTodo(todo)
                },
                onClickItem = { todo ->
                    viewModel.toggleTodo(todo)
                })
        }
        binding.addButton.setOnClickListener {
            val todo = Todo(binding.editText.text.toString())
            viewModel.addTodo(todo)
            binding.editText.text = null
        }

        //관찰, UI 업데이트
        viewModel.todoLiveData.observe(this, Observer {
            (binding.recyclerView.adapter as TodoAdapter).setData(it)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                viewModel.fetchData()
            } else {
                // Sign in failed.
                finish()
            }
        }
    }

    private fun login() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    private fun logout() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                login()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_log_out -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

data class Todo(
    val text: String,
    var isDone: Boolean = false,
) {

}

class TodoAdapter(
    private var dataSet: List<DocumentSnapshot>,
    val onClickDeleteIcon: (todo: DocumentSnapshot) -> Unit,
    val onClickItem: (todo: DocumentSnapshot) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        val checkButton: ImageButton
        val todoTextView: TextView
        val deleteButton: ImageButton

        init {
            // Define click listener for the ViewHolder's View.
            checkButton = binding.checkButton
            todoTextView = binding.todoTextView
            deleteButton = binding.deleteButton
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TodoViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_todo, viewGroup, false)

        return TodoViewHolder(ItemTodoBinding.bind(view))
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: TodoViewHolder, position: Int) {

        val todo = dataSet[position]
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.todoTextView.text = todo.getString("text") ?: ""
        viewHolder.deleteButton.setOnClickListener {
            onClickDeleteIcon.invoke(todo)
        }

        if ((todo.getBoolean("isDone") ?: false) == true) {
            viewHolder.checkButton.setBackgroundResource(R.drawable.ic_check_circle)
            viewHolder.todoTextView.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                setTypeface(null, Typeface.ITALIC)
            }
        } else {
            viewHolder.checkButton.setBackgroundResource(R.drawable.ic_before_check)
            viewHolder.todoTextView.apply {
                paintFlags = 0
                setTypeface(null, Typeface.NORMAL)
            }
        }
        viewHolder.binding.root.setOnClickListener {
            onClickItem.invoke(todo)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    fun setData(newData: List<DocumentSnapshot>) {
        dataSet = newData
        notifyDataSetChanged()
    }
}

class MainViewModel : ViewModel() {
    val db = Firebase.firestore
    val todoLiveData = MutableLiveData<List<DocumentSnapshot>>()
    val user = FirebaseAuth.getInstance().currentUser

    init {
        fetchData()
    }

    fun fetchData() {
        if (user != null) {
            db.collection(user.uid)
                .addSnapshotListener { value, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }
                    if (value != null) {
                        todoLiveData.value = value.documents
                    }
                }
        }
    }

    fun toggleTodo(todo: DocumentSnapshot) {
        user?.let { user ->
            val isDone = todo.getBoolean("isDone") ?: false
            db.collection(user.uid).document(todo.id).update("isDone", !isDone)
        }
    }

    fun addTodo(todo: Todo) {
        user?.let { user ->
            db.collection(user.uid).add(todo)
        }
    }

    fun deleteTodo(todo: DocumentSnapshot) {
        user?.let { user ->
            db.collection(user.uid).document(todo.id).delete()
        }
    }
}