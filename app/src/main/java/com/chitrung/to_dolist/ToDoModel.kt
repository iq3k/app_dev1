package com.chitrung.to_dolist

class ToDoModel {
    companion object Factory{
        fun createList(): ToDoModel = ToDoModel()
    }
    var UID : String? = null
    var itemDataText : String? = null
    var done : Boolean? = false
    var timeString : String? = null
    var timeInt : Int? = null
}