package com.zubayer.location_exam.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubayer.location_exam.model.AppUsers
import com.zubayer.location_exam.repository.ProfileRepository
import com.zubayer.location_exam.repository.UserRepository
import kotlinx.coroutines.launch

class MyProfileViewModel(
    private val repo: ProfileRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableLiveData<AppUsers?>()
    val user: LiveData<AppUsers?> = _user

    fun loadUser(userId: String) {

        viewModelScope.launch {

            val user = userRepository.getUserById(userId)

            _user.postValue(user)

        }
    }

    fun updateUsername(
        userId: String,
        newName: String,
        onResult: (Boolean) -> Unit
    ) {

        viewModelScope.launch {

            val result = repo.updateUsername(userId, newName)

            onResult(result.isSuccess)
        }
    }
}