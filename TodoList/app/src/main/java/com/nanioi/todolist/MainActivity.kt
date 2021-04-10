package com.nanioi.todolist

import android.graphics.Paint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
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
                viewModel.data,
                onClickDeleteIcon = { todo ->
                    viewModel.deleteTodo(todo)
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                },
                onClickItem = { todo ->
                    viewModel.toggleTodo(todo)
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                })
        }
        binding.addButton.setOnClickListener {
            val todo = Todo(binding.editTextTextPersonName.text.toString())
            viewModel.addTodo(todo)
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
    }
}

data class Todo(
    val text: String,
    var isDone: Boolean = false,
) {

}

class TodoAdapter(
    private val dataSet: List<Todo>,
    val onClickDeleteIcon: (todo: Todo) -> Unit,
    val onClickItem: (todo: Todo) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        val todoTextView: TextView
        val deleteButton: ImageButton

        init {
            // Define click listener for the ViewHolder's View.
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
            viewHolder.todoTextView.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                setTypeface(null, Typeface.ITALIC)
            }
        } else {
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

}

class MainViewModel : ViewModel() {
    val data = arrayListOf<Todo>()
    fun toggleTodo(todo: Todo) {
        todo.isDone = !todo.isDone

    }

    fun addTodo(todo: Todo) {
        data.add(todo)
    }

    fun deleteTodo(todo: Todo) {
        data.remove(todo)
    }
}