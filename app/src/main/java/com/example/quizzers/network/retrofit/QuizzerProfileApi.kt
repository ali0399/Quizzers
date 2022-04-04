package com.example.quizzers.network.retrofit

import com.example.quizzers.network.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface QuizzerProfileApi {
    @POST("accounts/create")
    suspend fun createUser(@Body body: CreateUserRequestModel): Response<CreateUserResponseModel>

    @POST("accounts/login")
    suspend fun login(@Body body: LoginRequestModel): Response<LoginResponseModel>
}