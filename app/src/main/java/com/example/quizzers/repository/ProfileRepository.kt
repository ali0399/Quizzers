package com.example.quizzers.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizzers.network.models.CreateUserRequestModel
import com.example.quizzers.network.models.CreateUserResponseModel
import com.example.quizzers.network.models.LoginRequestModel
import com.example.quizzers.network.models.LoginResponseModel
import com.example.quizzers.network.retrofit.QuizzerProfileApi

class ProfileRepository(private val quizzerProfileApi: QuizzerProfileApi) {

    private val mCreateUserResponse=MutableLiveData<CreateUserResponseModel>()
    val createUserResponse:LiveData<CreateUserResponseModel>
        get() {
            return mCreateUserResponse
        }
    suspend fun createUser(body:CreateUserRequestModel){
        val result = quizzerProfileApi.createUser(body)
        if(result!=null){
            //todo handle error messages
            mCreateUserResponse.postValue(result.body())
        }
    }

    private val mLoginResponseModel=MutableLiveData<LoginResponseModel>()
    val loginResponse:LiveData<LoginResponseModel>
        get() {
            return mLoginResponseModel
        }
    suspend fun login(body:LoginRequestModel){
        val result = quizzerProfileApi.login(body)
        if(result!=null){
            mLoginResponseModel.postValue(result.body())
        }
    }
}