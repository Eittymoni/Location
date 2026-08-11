package com.zubayer.location_exam.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubayer.location_exam.model.AppUsers
import com.zubayer.location_exam.repository.AuthRepository
import com.zubayer.location_exam.repository.UserRepository
import kotlinx.coroutines.launch

class FriendListViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userList = MutableLiveData<List<AppUsers>>()
    val userList: LiveData<List<AppUsers>> get() = _userList

    fun fetchUsers() {

        viewModelScope.launch{

            val users = userRepository.getAllUsers()

            _userList.postValue(users)

        }
    }

    fun logOut() {
        authRepository.logout()
    }
}