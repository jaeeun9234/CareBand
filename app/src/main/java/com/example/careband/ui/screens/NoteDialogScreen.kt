package com.example.careband.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.careband.data.model.Note

@Composable
fun NoteDialogScreen(
    onDismiss: () -> Unit,
    onSave: (Note) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Note") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.height(100.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(Note(title, description))
            }) {
                Text("Ok")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}
