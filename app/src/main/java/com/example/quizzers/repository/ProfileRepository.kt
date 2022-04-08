package com.example.quizzers.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizzers.network.models.*
import com.example.quizzers.network.retrofit.QuizzerProfileApi

class ProfileRepository(private val quizzerProfileApi: QuizzerProfileApi) {
    //Create User
    private val mCreateUserResponse = MutableLiveData<CreateUserResponseModel>()
    val createUserResponse: LiveData<CreateUserResponseModel>
        get() {
            return mCreateUserResponse
        }

    suspend fun createUser(body: CreateUserRequestModel) {
        val result = quizzerProfileApi.createUser(body)
        if (result != null) {
            //todo handle error messages
            mCreateUserResponse.postValue(result.body())
        }
    }

    //Login
    private val mLoginResponseModel = MutableLiveData<LoginResponseModel>()
    val loginResponse: LiveData<LoginResponseModel>
        get() {
            return mLoginResponseModel
        }

    suspend fun login(body: LoginRequestModel) {
        val result = quizzerProfileApi.login(body)
        if (result != null) {
            mLoginResponseModel.postValue(result.body())
        }
    }

    //CreateScore
    private val mCreateScoreResponse = MutableLiveData<CreateScoreResponseModel>()
    val scoreResponse: LiveData<CreateScoreResponseModel>
        get() {
            return mCreateScoreResponse
        }

    suspend fun createScore(token: String, body: CreateScoreRequestModel) {
        val result =
            quizzerProfileApi.createScore(token,
                body)
        if (result != null) {
            mCreateScoreResponse.postValue(result.body())
        }
    }

    //get Profile detail
    private val mProfileDetailResponse = MutableLiveData<ProfileDetailResponseModel>()
    val profileResponse: LiveData<ProfileDetailResponseModel>
        get() {
            return mProfileDetailResponse
        }

    suspend fun getProfileDetail(token: String) {
        val result = quizzerProfileApi.getProfileDetails(token)
        if (result != null)
            mProfileDetailResponse.postValue(result.body())
    }

    //update username
    private val mUsernameUpdateResponse = MutableLiveData<UsernameUpdateModel>()
    val usernameUpdateResponse: LiveData<UsernameUpdateModel>
        get() {
            return mUsernameUpdateResponse
        }

    suspend fun updateUsername(token: String, body: UsernameUpdateModel) {
        val result = quizzerProfileApi.updateUsername(token, body)
        if (result != null)
            mUsernameUpdateResponse.postValue(result.body())
    }
}