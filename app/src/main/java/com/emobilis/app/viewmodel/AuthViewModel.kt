package com.emobilis.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emobilis.app.data.model.Student
import com.emobilis.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentStudent = MutableStateFlow<Student?>(null)
    val currentStudent: StateFlow<Student?> = _currentStudent

    fun register(student: Student, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repo.registerStudent(student, password)
            _authState.value = if (result.isSuccess)
                AuthState.Success("student")
            else
                AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repo.login(email, password)
            _authState.value = if (result.isSuccess)
                AuthState.Success(result.getOrDefault("student"))
            else
                AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
        }
    }

    fun loadCurrentStudent() {
        viewModelScope.launch {
            val uid = repo.getCurrentUid() ?: return@launch
            _currentStudent.value = repo.getStudent(uid)
        }
    }

    fun signOut() {
        repo.signOut()
        _authState.value = AuthState.Idle
        _currentStudent.value = null
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val role: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
