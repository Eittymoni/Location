package com.zubayer.location_exam.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubayer.location_exam.model.AppUsers
import com.zubayer.location_exam.repository.UserRepository
import kotlinx.coroutines.launch


class MapsViewModel(
    private val repo: UserRepository
) : ViewModel() {

    private val _user = MutableLiveData<AppUsers?>()
    val user: LiveData<AppUsers?> = _user

    private val _userList = MutableLiveData<List<AppUsers>>()
    val userList: LiveData<List<AppUsers>> = _userList

    fun loadSingleUser(userId: String) {

        viewModelScope.launch {

            val user = repo.getUserById(userId)

            _user.postValue(user)

        }
    }

    fun loadAllUsers() {

        viewModelScope.launch {

            val users = repo.getAllUsers()

            _userList.postValue(users)

        }
    }
}