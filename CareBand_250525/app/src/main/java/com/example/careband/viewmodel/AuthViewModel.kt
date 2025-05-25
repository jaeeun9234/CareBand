package com.example.careband.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.careband.data.model.UserType

class AuthViewModel : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _userType = MutableStateFlow(UserType.USER)
    val userType: StateFlow<UserType> = _userType

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId

    fun checkLoginState() {
        val user = FirebaseAuth.getInstance().currentUser
        _isLoggedIn.value = user != null
        _currentUserId.value = user?.uid
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        _isLoggedIn.value = false
        _currentUserId.value = null
        _userName.value = ""
    }

    fun loadUserInfoFromFirestore() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        _currentUserId.value = user.uid

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    _userName.value = document.getString("name") ?: ""
                    _userType.value = when (document.getString("type")) {
                        "caregiver" -> UserType.CAREGIVER
                        else -> UserType.USER
                    }
                }
            }
            .addOnFailureListener {
                _userName.value = ""
                _userType.value = UserType.USER
            }
    }
}

//enum class UserType {
//    USER, CAREGIVER
//}
