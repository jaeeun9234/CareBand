package com.example.careband.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.careband.data.model.UserType
import com.example.careband.viewmodel.AuthViewModel
import com.example.careband.viewmodel.LoginViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    drawerState: DrawerState? = null,
    scope: CoroutineScope,
    authViewModel: AuthViewModel
) {
    val loginViewModel: LoginViewModel = viewModel()

    // 🔒 ESC/뒤로가기 키 무시
    BackHandler(enabled = true) {
        // 아무 작업도 하지 않음 → 뒤로가기 입력 무시
    }

    var isUser by remember { mutableStateOf(true) }

    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var birth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }

    var phone by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }

    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("회원가입", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = { isUser = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isUser) MaterialTheme.colorScheme.primary else Color.LightGray
                )
            ) {
                Text("사용자")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { isUser = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isUser) MaterialTheme.colorScheme.primary else Color.LightGray
                )
            ) {
                Text("보호자")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = id, onValueChange = { id = it }, label = { Text("ID") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())
        OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirm Password") }, visualTransformation = PasswordVisualTransformation())
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })

        if (isUser) {
            OutlinedTextField(value = birth, onValueChange = { birth = it }, label = { Text("Birth") })
            OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender") })
            OutlinedTextField(value = contactInfo, onValueChange = { contactInfo = it }, label = { Text("Contact Info") })
        } else {
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
            OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text("User ID") })
            OutlinedTextField(value = relationship, onValueChange = { relationship = it }, label = { Text("Relationship") })
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(error, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (password != confirmPassword) {
                error = "비밀번호가 일치하지 않습니다."
            } else {
                val fakeEmail = "${id.trim()}@careband.com"
                val cleanPassword = password.trim()
                val userType = if (isUser) UserType.USER else UserType.CAREGIVER

                loginViewModel.register(
                    email = fakeEmail,
                    password = cleanPassword,
                    onSuccess = { uid ->
                        // Firestore에 사용자 정보 저장
                        authViewModel.saveUserToFirestore(
                            uid = uid,
                            name = name,
                            type = userType,
                            birth = if (isUser) birth else "",
                            gender = if (isUser) gender else "",
                            protectedUserId = if (!isUser) userId else null,
                            onSuccess = {
                                authViewModel.checkLoginStatus()
                                onRegisterSuccess()
                            },
                            onFailure = { firestoreError ->
                                error = firestoreError
                            }
                        )
                    },
                    onFailure = { authError ->
                        error = authError
                    }
                )
            }
        }) {
            Text("회원가입")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onLoginClick) {
            Text("로그인으로 돌아가기")
        }
    }
}
