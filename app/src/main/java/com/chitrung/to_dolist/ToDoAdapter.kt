package com.chitrung.to_dolist

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ToDoAdapter(context : Context , todoList : MutableList<ToDoModel>) : BaseAdapter() {
    private val inflater:LayoutInflater = LayoutInflater.from(context)
    private var itemList = todoList
    private var updateAndDelete : UpdateAndDelete = context as UpdateAndDelete


    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(p0: Int): Any {
        return itemList.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var UID : String = itemList.get(p0).UID as String
        var itemTextData = itemList.get(p0).itemDataText as String
        var time = itemList.get(p0).timeString as String
        var done : Boolean = itemList.get(p0).done  as Boolean

        val view : View
        val viewHolder : ListViewHolder

        if(p1 == null){
            view = inflater.inflate(R.layout.row_itemlayout, p2 , false)
            viewHolder = ListViewHolder(view)
            view.tag = viewHolder
        }else{
            view = p1
            viewHolder = view.tag as ListViewHolder
        }
        viewHolder.textLabel.text = itemTextData
        viewHolder.textLabel1.text = time
        viewHolder.isDone.isChecked = done
        viewHolder.isDone.setOnClickListener {
            updateAndDelete.modifyItem(UID, !done)
        }
        viewHolder.isDeleted.setOnClickListener {
            updateAndDelete.onItemDelete(UID)
        }


        viewHolder.textLabel.setOnClickListener {
            updateAndDelete.onRepair(UID,viewHolder.textLabel.text.toString())
        }


        return view
    }
    private class ListViewHolder(row : View?){
        val textLabel : TextView = row!!.findViewById(R.id.item_textView) as TextView
        val textLabel1 : TextView = row!!.findViewById(R.id.time) as TextView
        val isDone : CheckBox = row!!.findViewById(R.id.checkbox)  as CheckBox
        val isDeleted : ImageButton = row!!.findViewById(R.id.close) as ImageButton
    }
}