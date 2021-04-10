package com.nanioi.todolist

import android.graphics.Paint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.nanioi.todolist.databinding.ActivityMainBinding
import com.nanioi.todolist.databinding.ItemTodoBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


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
}

data class Todo(
        val text: String,
        var isDone: Boolean = false,
) {

}

class TodoAdapter(
        private var dataSet: List<Todo>,
        val onClickDeleteIcon: (todo: Todo) -> Unit,
        val onClickItem: (todo: Todo) -> Unit
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
        viewHolder.todoTextView.text = todo.text
        viewHolder.deleteButton.setOnClickListener {
            onClickDeleteIcon.invoke(todo)
        }

        if (todo.isDone) {
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

    fun setData(newData: List<Todo>) {
        dataSet = newData
        notifyDataSetChanged()
    }
}

class MainViewModel : ViewModel() {
    val todoLiveData = MutableLiveData<List<Todo>>()
    private val data = arrayListOf<Todo>()
    fun toggleTodo(todo: Todo) {
        todo.isDone = !todo.isDone
        todoLiveData.value = data
    }

    fun addTodo(todo: Todo) {
        data.add(todo)
        todoLiveData.value = data
    }

    fun deleteTodo(todo: Todo) {
        data.remove(todo)
        todoLiveData.value = data
    }
}