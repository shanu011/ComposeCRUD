package com.rajat.composecrud.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.rajat.composecrud.FirebaseStatus
import com.rajat.composecrud.UserModel
import com.rajat.composecrud.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel :ViewModel() {
    private val userRepo = UserRepository()
    var userAs : MutableLiveData<FirebaseStatus> = MutableLiveData<FirebaseStatus>(FirebaseStatus.NoHitOnce)
    private val _userList = MutableStateFlow<List<UserModel?>?>(emptyList())
    val userList: StateFlow<List<UserModel?>?> = _userList
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchUsers()
    }

    fun fetchUsers(){
        _isLoading.value = true
        userAs.value = FirebaseStatus.IsBeingHit
        userRepo.fetchAllUsers { userList ->

            userAs.value = FirebaseStatus.FirebaseHit
          userList.let {
              _userList.value = it
              _isLoading.value = false
          }

            println("Check ViewModel: ${this.userList}")
        }
    }
    fun addUsers(context: Context,userModel:UserModel){
        _isLoading.value = true
        userRepo.addUsers(context,userModel){result->
            _isLoading.value = false
            println("Check Data Added or Not: $result")
        }
    }
}