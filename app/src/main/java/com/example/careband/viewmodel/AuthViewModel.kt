package com.example.careband.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careband.data.model.User
import com.example.careband.data.model.UserType
import com.example.careband.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val repository = UserRepository()

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
        viewModelScope.launch {
            val result = repository.getUser(uid)
            result?.let { user ->
                _user.value = user
                _userName.value = user.name
                _userType.value = user.type
            }
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
        val user = User(
            id = uid,
            name = name,
            type = type,
            birth = birth,
            gender = gender,
            protectedUserId = protectedUserId
        )

        viewModelScope.launch {
            val result = repository.saveUser(uid, user)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onFailure(result.exceptionOrNull()?.message ?: "알 수 없는 오류")
            }
        }
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
