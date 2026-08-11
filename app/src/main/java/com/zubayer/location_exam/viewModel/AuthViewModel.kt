package com.zubayer.location_exam.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubayer.location_exam.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repo: AuthRepository) : ViewModel() {
    val loginResult = MutableLiveData<Pair<Boolean, String?>>()
    val registerResult = MutableLiveData<Pair<Boolean, String?>>()
    val loading = MutableLiveData<Boolean>()

    fun login(email: String, password: String) {
        loading.postValue(true)
        viewModelScope.launch {
            val result = repo.login(email, password)
            loading.postValue(false)
            if (result.isSuccess) {
                loginResult.postValue(true to null)
            } else {
                loginResult.postValue(false to result.exceptionOrNull()?.message)
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        loading.postValue(true)
        viewModelScope.launch {
            val result = repo.register(username, email, password)
            loading.postValue(false)
            if (result.isSuccess) {
                registerResult.postValue(true to null)
            } else {
                registerResult.postValue(false to result.exceptionOrNull()?.message)
            }
        }
    }
}

