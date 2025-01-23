package com.example.todoapp_sql.controller

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.todoapp_sql.data.DbHelper
import com.example.todoapp_sql.data.ToDo

class ToDoController(context: Context) {
    private val dbHelper = DbHelper(context)

    fun insertToDo(todo: ToDo): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val values = ContentValues().apply {
                put("name", todo.name)
                put("priority", todo.priority)
                put("end_time", todo.end_time)
                put("description", todo.description)
                put("status", todo.status)
            }
            val result = db.insert("todos", null, values)
            Log.d("ToDoController", "Insert result: $result for ToDo: $todo")
            result != -1L
        } catch (e: Exception) {
            Log.e("ToDoController", "Insert failed", e)
            false
        } finally {
            db.close()
        }
    }

    fun updateToDo(todo: ToDo): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val values = ContentValues().apply {
                put("name", todo.name)
                put("priority", todo.priority)
                put("end_time", todo.end_time)
                put("description", todo.description)
                put("status", todo.status)
            }
            val result = db.update("todos", values, "id = ?", arrayOf(todo.id.toString()))
            Log.d("ToDoController", "Update result: $result for ToDo ID: ${todo.id}")
            result > 0
        } catch (e: Exception) {
            Log.e("ToDoController", "Update failed", e)
            false
        } finally {
            db.close()
        }
    }

    fun deleteToDo(todoId: Int): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val result = db.delete("todos", "id = ?", arrayOf(todoId.toString()))
            Log.d("ToDoController", "Delete result: $result for ToDo ID: $todoId")
            result > 0
        } catch (e: Exception) {
            Log.e("ToDoController", "Delete failed", e)
            false
        } finally {
            db.close()
        }
    }

    fun getAllToDos(): List<ToDo> {
        val db = dbHelper.readableDatabase
        val todos = mutableListOf<ToDo>()
        val cursor = db.rawQuery("SELECT * FROM todos", null)
        return try {
            if (cursor.moveToFirst()) {
                do {
                    val todo = ToDo(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        priority = cursor.getString(cursor.getColumnIndexOrThrow("priority")),
                        end_time = cursor.getString(cursor.getColumnIndexOrThrow("end_time")),
                        description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        status = cursor.getInt(cursor.getColumnIndexOrThrow("status"))
                    )
                    Log.d("ToDoController", "Fetched ToDo: $todo")
                    todos.add(todo)
                } while (cursor.moveToNext())
            }
            Log.d("ToDoController", "Total ToDos fetched: ${todos.size}")
            todos
        } catch (e: Exception) {
            Log.e("ToDoController", "Fetching ToDos failed", e)
            todos
        } finally {
            cursor.close()
            db.close()
        }
    }
}
