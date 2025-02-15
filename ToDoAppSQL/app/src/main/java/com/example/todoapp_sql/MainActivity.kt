package com.example.todoapp_sql

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.todoapp_sql.ui.theme.ToDoAppSQLTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoAppSQLTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ToDoDashboard()
                }
            }
        }
    }
}