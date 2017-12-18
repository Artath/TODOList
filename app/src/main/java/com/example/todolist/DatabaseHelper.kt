package com.example.todolist

import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.content.Context
import android.database.Cursor


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, TABLE_NAME, null, 1) {

    val data: Cursor
        get() {
            val db = this.writableDatabase
            val query = "SELECT * FROM " + TABLE_NAME
            return db.rawQuery(query, null)
        }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " TEXT, " + COL3 +  " INTEGER DEFAULT 1)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, i: Int, i1: Int) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun addData(item: String): Boolean {
        val db = this.writableDatabase
        val taskValue = ContentValues()
        taskValue.put(COL2, item)
        val result = db.insert(TABLE_NAME, null, taskValue)
        return result.compareTo(-1) != 0
    }

    fun getItemID(task: String): Cursor {
        val db = this.writableDatabase
        val query = "SELECT " + COL1 + " FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = '" + task + "'"
        return db.rawQuery(query, null)
    }

    fun getItemStatus(task: String): Cursor {
        val db = this.writableDatabase
        val query = "SELECT " + COL3 + " FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = '" + task + "'"
        return db.rawQuery(query, null)
    }

    fun updateTask(newTask: String, id: Int, oldTask: String) {
        val db = this.writableDatabase
        val query = "UPDATE " + TABLE_NAME + " SET " + COL2 +
                " = '" + newTask + "' WHERE " + COL1 + " = '" + id + "'" +
                " AND " + COL2 + " = '" + oldTask + "'"
        db.execSQL(query)
    }

    fun updateStatus(newStatus: Int, id: Int, currentTask: String){
        val db = this.writableDatabase
        val query = "UPDATE " + TABLE_NAME + " SET " + COL3 +
                " = '" + newStatus + "' WHERE " + COL1 + " = '" + id + "'" +
                " AND " + COL2 + " = '" + currentTask + "'"
        db.execSQL(query)
    }

    fun deleteTask(id: Int, task: String) {
        val db = this.writableDatabase
        val query = ("DELETE FROM " + TABLE_NAME + " WHERE "
                + COL1 + " = '" + id + "'" +
                " AND " + COL2 + " = '" + task + "'")
        db.execSQL(query)
    }

    companion object {
        private val TABLE_NAME = "tasks_table"
        private val COL1 = "ID"
        private val COL2 = "task"
        private val COL3 = "status"
    }

}