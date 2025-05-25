package com.example.careband.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.careband.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val viewModel: LoginViewModel = viewModel()
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("CareBand", fontSize = 30.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = id, onValueChange = { id = it }, label = { Text("ID") })
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(error, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val cleanedId = id.trim()
            val fakeEmail = "$cleanedId@careband.com"
            val cleanedPassword = password.trim()
            viewModel.login(
                email = fakeEmail,
                password = cleanedPassword,
                onSuccess = {
                    error = ""
                    onLoginSuccess()
                },
                onFailure = {
                    error = it
                }
            )
        }) {
            Text("로그인")
        }

        TextButton(onClick = onRegisterClick) {
            Text("회원가입")
        }
    }
}
