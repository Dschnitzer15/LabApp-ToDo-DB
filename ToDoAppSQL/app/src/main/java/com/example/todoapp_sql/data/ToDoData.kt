package com.example.todoapp_sql.data

import androidx.room.ColumnInfo

data class ToDo(
    val id: Int = 0,
    val name: String,
    val priority: String,
    val end_time: String,
    val description: String,
    val status: Int = 0
)

