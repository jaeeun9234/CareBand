package com.example.careband.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careband.data.model.User
import com.example.careband.data.model.UserType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _userType = MutableStateFlow<UserType?>(null)
    val userType: StateFlow<UserType?> = _userType

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    init {
        checkLoginStatus()
    }

    fun checkLoginStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            _isLoggedIn.value = true
            _userId.value = currentUser.uid
            loadUserData(currentUser.uid)
        } else {
            logout()
        }
    }

    fun loadUserData(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = User(
                        id = uid,
                        name = document.getString("name") ?: "",
                        type = UserType.fromString(document.getString("userType")) ?: UserType.USER,
                        birth = document.getString("birth") ?: "",
                        gender = document.getString("gender") ?: "",
                        protectedUserId = document.getString("protectedUserId")
                    )
                    _user.value = user
                    _userName.value = user.name
                    _userType.value = user.type
                }
            }
            .addOnFailureListener {
                println("❌ 사용자 정보 불러오기 실패: ${it.message}")
            }
    }

    fun saveUserToFirestore(
        uid: String,
        name: String,
        type: UserType,
        birth: String,
        gender: String,
        protectedUserId: String? = null,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val user = hashMapOf(
            "name" to name,
            "userType" to type.name,
            "birth" to birth,
            "gender" to gender,
            "protectedUserId" to protectedUserId
        )

        db.collection("users").document(uid).set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Firestore 저장 실패") }
    }

    fun logout() {
        auth.signOut()
        _user.value = null
        _userName.value = ""
        _userType.value = null
        _userId.value = null
        _isLoggedIn.value = false
    }
}
