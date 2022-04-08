package com.example.quizzers.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzers.network.models.*
import com.example.quizzers.repository.ProfileRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {
    private val TAG = "ProfileViewModel"

    init {
        Log.d(TAG, "init: start")
        /*viewModelScope.launch {
            repository.createUser(requestBody as CreateUserRequestModel)
            repository.login(requestBody as LoginRequestModel)
        }*/
    }

    fun login(requestBody: LoginRequestModel){
        Log.d(TAG, "login: start")
        viewModelScope.launch {
            repository.login(requestBody)
        }
    }

    fun createUser(requestBody: CreateUserRequestModel) {
        Log.d(TAG, "createUser: start")
        viewModelScope.launch {
            repository.createUser(requestBody)
        }
    }

    fun createScore(token: String, requestBody: CreateScoreRequestModel) {
        Log.d(TAG, "createUser: start")
        viewModelScope.launch {
            repository.createScore(token, requestBody)
        }
    }

    fun getProfileDetail(token: String) {
        Log.d(TAG, "getProfileDetail: start")
        viewModelScope.launch {
            repository.getProfileDetail(token)
        }
    }


//    fun createUser(requestBody:Any){
//        Log.d(TAG, "createUser: start")
//        viewModelScope.launch {
//            repository.createUser(requestBody as CreateUserRequestModel)
//        }
//    }

    val createUserResponse: LiveData<CreateUserResponseModel>
        get() = repository.createUserResponse
    val loginResponse: LiveData<LoginResponseModel>
        get() = repository.loginResponse
    val createScoreResponse: LiveData<CreateScoreResponseModel>
        get() = repository.scoreResponse
    val profileDetails: LiveData<ProfileDetailResponseModel>
        get() = repository.profileResponse

}