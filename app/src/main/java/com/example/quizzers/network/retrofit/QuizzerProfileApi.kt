package com.example.quizzers.network.retrofit

import com.example.quizzers.network.models.*
import retrofit2.Response
import retrofit2.http.*

interface QuizzerProfileApi {
    @POST("accounts/create")
    suspend fun createUser(@Body body: CreateUserRequestModel): Response<CreateUserResponseModel>

    @POST("accounts/login")
    suspend fun login(@Body body: LoginRequestModel): Response<LoginResponseModel>

    @POST("score/create")
    suspend fun createScore(
        @Header("Authorization") token: String,
        @Body body: CreateScoreRequestModel,
    ): Response<CreateScoreResponseModel>

    @GET("accounts/details")
    suspend fun getProfileDetails(@Header("Authorization") token: String): Response<ProfileDetailResponseModel>

}