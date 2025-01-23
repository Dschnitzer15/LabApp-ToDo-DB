package com.example.todoapp_sql

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todoapp_sql.data.ToDo
import com.example.todoapp_sql.controller.ToDoController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoDashboard() {
    val context = LocalContext.current
    val toDoController = remember { ToDoController(context) }
    val navController = rememberNavController()

    // State for all todos and filtered lists
    var allToDos by remember { mutableStateOf(emptyList<ToDo>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) } // State for error messages
    val snackbarHostState = remember { SnackbarHostState() } // SnackbarHostState for managing Snackbars
    val activeToDos = allToDos.filter { it.status == 0 } // Status 0 = aktiv
    val completedToDos = allToDos.filter { it.status == 1 } // Status 1 = erledigt

    // Load todos from the database
    LaunchedEffect(Unit) {
        try {
            allToDos = toDoController.getAllToDos()
        } catch (e: Exception) {
            errorMessage = "Fehler beim Laden der ToDos: ${e.message}"
        }
    }

    // Show error message in a Snackbar if present
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            errorMessage = null // Reset the error message after showing
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("ToDo Dashboard") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("addTodo")
            }) {
                Text("+")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) } // Correctly pass the SnackbarHostState
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "activeTodos",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("activeTodos") {
                ToDoListScreen(
                    title = "Aktive ToDos",
                    todos = activeToDos,
                    onEdit = { toDo ->
                        try {
                            toDoController.updateToDo(toDo)
                            allToDos = toDoController.getAllToDos()
                        } catch (e: Exception) {
                            errorMessage = "Fehler beim Aktualisieren: ${e.message}"
                        }
                    },
                    onDelete = { toDo ->
                        try {
                            toDoController.deleteToDo(toDo.id)
                            allToDos = toDoController.getAllToDos()
                        } catch (e: Exception) {
                            errorMessage = "Fehler beim Löschen: ${e.message}"
                        }
                    }
                )
            }
            composable("completedTodos") {
                ToDoListScreen(
                    title = "Erledigte ToDos",
                    todos = completedToDos,
                    onEdit = { toDo ->
                        try {
                            toDoController.updateToDo(toDo)
                            allToDos = toDoController.getAllToDos()
                        } catch (e: Exception) {
                            errorMessage = "Fehler beim Aktualisieren: ${e.message}"
                        }
                    },
                    onDelete = { toDo ->
                        try {
                            toDoController.deleteToDo(toDo.id)
                            allToDos = toDoController.getAllToDos()
                        } catch (e: Exception) {
                            errorMessage = "Fehler beim Löschen: ${e.message}"
                        }
                    }
                )
            }
            composable("addTodo") {
                AddToDoScreen(
                    onSave = { newToDo ->
                        try {
                            toDoController.insertToDo(newToDo)
                            allToDos = toDoController.getAllToDos()
                            navController.popBackStack()
                        } catch (e: Exception) {
                            errorMessage = "Fehler beim Speichern: ${e.message}"
                        }
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}


@Composable
fun ToDoListScreen(
    title: String,
    todos: List<ToDo>,
    onEdit: (ToDo) -> Unit,
    onDelete: (ToDo) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineLarge)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(todos.size) { index ->
                val todo = todos[index]
                ToDoCard(
                    name = todo.name,
                    priority = todo.priority,
                    description = todo.description,
                    onEdit = { onEdit(todo) },
                    onDelete = { onDelete(todo) }
                )
            }
        }
    }
}

@Composable
fun ToDoCard(
    name: String,
    priority: String,
    description: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = name, style = MaterialTheme.typography.labelLarge)
            Text(text = "Priorität: $priority")
            Text(text = description)

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onEdit) { Text("Bearbeiten") }
                TextButton(onClick = onDelete) { Text("Löschen") }
            }
        }
    }
}

@Composable
fun AddToDoScreen(
    onSave: (ToDo) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }
    var end_time by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Neues ToDo hinzufügen", style = MaterialTheme.typography.headlineLarge)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )

        OutlinedTextField(
            value = priority,
            onValueChange = { priority = it },
            label = { Text("Priorität (z.B. Hoch, Mittel, Niedrig)") }
        )

        OutlinedTextField(
            value = end_time,
            onValueChange = { end_time = it },
            label = { Text("Endzeitpunkt (z.B. 2025-01-31)") }
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Beschreibung") }
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = {
                onSave(
                    ToDo(
                        name = name,
                        priority = priority,
                        end_time = end_time,
                        description = description,
                        status = 0
                    )
                )
            }) {
                Text("Speichern")
            }
            Button(onClick = onCancel) {
                Text("Abbrechen")
            }
        }
    }
}
