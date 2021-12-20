package com.chitrung.to_dolist

interface UpdateAndDelete{
    fun modifyItem(itemUID : String , isDone : Boolean)
    fun onItemDelete(itemUID : String)
    fun onRepair(itemUID: String,text: String)
}