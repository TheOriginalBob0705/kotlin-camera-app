package com.example.cameraapp.login.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cameraapp.login.database.User
import com.example.cameraapp.login.database.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SignUpResult {
    object Success : SignUpResult()
    data class Error(val message: String) : SignUpResult()
}

class SignUpViewModel(private val userRepository: UsersRepository) : ViewModel() {

    private val _signUpResult = MutableStateFlow<SignUpResult?>(null)
    val signUpResult = _signUpResult.asStateFlow()

    fun signUp(username: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            if (username.isBlank() || password.isBlank() || password != confirmPassword || userRepository.getUserByUsername(username) != null) {
                _signUpResult.value = SignUpResult.Error("Could not create user")
            } else {
                val newUser = User(username, password)
                userRepository.registerUser(newUser)
                _signUpResult.value = SignUpResult.Success
            }
        }
    }
}
