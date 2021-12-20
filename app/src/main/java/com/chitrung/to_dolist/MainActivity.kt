package com.chitrung.to_dolist

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() , UpdateAndDelete {
    lateinit var  database : DatabaseReference
    var todoList : MutableList<ToDoModel>? = null
    lateinit var adapter : ToDoAdapter
    private var listViewItem : ListView? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        listViewItem = findViewById<ListView>(R.id.item_listView)

        database = FirebaseDatabase.getInstance().reference

        fab.setOnClickListener{view ->
            val alertDialog = AlertDialog.Builder(this)
            val view = LinearLayout(this)
            val textEditText = EditText(this)
//            val textEditText1 = EditText(this)
//            textEditText1.setHint("yy-mm-dd | ex: 2021-08-09")
            view.setOrientation(LinearLayout.VERTICAL);
            view.addView(textEditText)
//            view.addView(textEditText1)
            alertDialog.setTitle("Add New Task - Deadline")
            alertDialog.setView(view)
//            alertDialog.setView(textEditText1)
            alertDialog.setPositiveButton("SAVE"){ dialog, i ->
                val todoItemData = ToDoModel.createList()
                todoItemData.itemDataText = textEditText.text.toString()
                val calendar: Calendar = Calendar.getInstance()
                val M = calendar.get(Calendar.MONTH) + 1
                val Y = calendar.get(Calendar.YEAR)
                val D = calendar.get(Calendar.DATE)
                val datetoString = D.toString() + '-' + M.toString() + '-' + Y.toString()
                val datetoInt = D.toInt() + (M.toInt() * 100) + (Y.toInt() * 10000)
                todoItemData.timeInt = datetoInt
                todoItemData.timeString = datetoString.toString()
//                val str = textEditText1.text.toString()
//                todoItemData.timeString = str
//
//                var tot = 0
//                var idx = 1
//                for (i in str) {
//                    if (i != '-') {
//                        tot += ((i - '0') * idx)
//                        idx *= 10
//                    }
//                }
//
//                todoItemData.timeInt = tot


                todoItemData.done = false

                val newItemData = database.child("todo").push()
                todoItemData.UID = newItemData.key

                newItemData.setValue(todoItemData)
                dialog.dismiss()
//                Toast.makeText(this, "Đã Lưu" , Toast.LENGTH_LONG).show()
            }

            alertDialog.setNeutralButton("Set Time") { dialog, i ->

                val datePickerDiaglog: DatePickerDialog = DatePickerDialog(this)
                datePickerDiaglog.setOnDateSetListener { view, year, month, dayOfMonth ->
                    val month2 = month.toInt() + 1
                    val datetoString = dayOfMonth.toString() + "-" + month2.toString() + "-" + year
                    val datetoInt = dayOfMonth.toInt() + (month2.toInt() * 100) + (year * 10000)
                    val todoItemData = ToDoModel.createList()
                    todoItemData.itemDataText= textEditText.text.toString()
                    todoItemData.timeString = datetoString.toString()
                    todoItemData.timeInt = datetoInt
                    todoItemData.done = false
                    val newItemData = database.child("todo").push()
                    todoItemData.UID = newItemData.key

                    newItemData.setValue(todoItemData)
                    dialog.dismiss()
                }
                datePickerDiaglog.show();
            }

            alertDialog.setNegativeButton("CANCEL"){ dialog, i ->
                dialog.dismiss()
            }


            alertDialog.show()
        }


        todoList = mutableListOf<ToDoModel>()
        adapter = ToDoAdapter(this, todoList!!)
        listViewItem!!.adapter = adapter
        database.orderByChild("timeInt").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext , "Không có công việc mới.!" , Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                todoList!!.clear()
                addItemToList(snapshot)
            }
        })

    }


//    fun add(key: String, value: Any) = toMap().apply { put(key, value) }

    private fun addItemToList(snapshot: DataSnapshot) {
        val items = snapshot.children.iterator()
//        val map = hashMapOf<ToDoModel, Int?>()

        if(items.hasNext()){
            val todoIndexdvalue = items.next()
            val itemsIterator = todoIndexdvalue.children.iterator()
            while(itemsIterator.hasNext()){
                val currentItem = itemsIterator.next()
                val todoItemData = ToDoModel.createList()
                val map1 = currentItem.getValue() as HashMap<String , Any>
                todoItemData.UID = currentItem.key
                todoItemData.done = map1.get("done") as Boolean?
                todoItemData.itemDataText = map1.get("itemDataText") as String?
                todoItemData.timeString = map1.get("timeString") as String?
//                val v = map1.get("timeString") as String?
//                var tot = 0
//                var idx = 1
//                if (v != null) {
//                    for (i in v) {
//                        if (i != '-') {
//                            tot += ((i - '0') * idx)
//                            idx *= 10
//                        }
//                    }
//                }
//                map.put(todoItemData, tot)
                todoList!!.add(todoItemData)
            }

        }
//        val result = map.toList().sortedBy { (_, value) -> value}.toMap()
//        for ((key, value ) in result)
//            todoList!!.add(key)
//        todoList!!.sortBy { it.timeInt }
        adapter.notifyDataSetChanged()
    }

    override fun modifyItem(itemUID: String, isDone: Boolean) {
        val itemReference = database.child("todo").child(itemUID)
        itemReference.child("done").setValue(isDone)
    }

    override fun onItemDelete(itemUID: String) {
        val itemReference = database.child("todo").child(itemUID)
        itemReference.removeValue()
        adapter.notifyDataSetChanged()
    }

    override fun onRepair(itemUID: String,text:String) {
        val itemReference = database.child("todo").child(itemUID)
        val alertDialog = AlertDialog.Builder(this)
        val textEditText : EditText= EditText(this)
        Toast.makeText(this, "click-click", Toast.LENGTH_SHORT).show()
        alertDialog.setTitle("Edit Task")
        alertDialog.setMessage("Task Current : " + text)
        alertDialog.setView(textEditText)
        alertDialog.setPositiveButton("Done"){
                dialog,i ->
            itemReference.child("itemDataText").setValue(textEditText.text.toString())
            dialog.dismiss()
            Toast.makeText(this, "item saved", Toast.LENGTH_SHORT).show()
        }
        alertDialog.setNegativeButton("Cancel"){
                dialog,i ->
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
        }
        alertDialog.show()
    }
}