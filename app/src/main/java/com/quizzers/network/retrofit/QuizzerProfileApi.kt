package com.quizzers.network.retrofit

import com.quizzers.network.models.*
import okhttp3.MultipartBody
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

    @PUT("accounts/update")
    suspend fun updateUsername(
        @Header("Authorization") token: String,
        @Body body: UsernameUpdateModel,
    ): Response<UsernameUpdateModel>

    @Multipart
    @POST("accounts/picture-update")
    suspend fun uploadProfilePhoto(
        @Header("Authorization") token: String,
        @Part filePart: MultipartBody.Part,
    ): Response<PicUploadResponse>

    @GET("accounts/leaderboard")
    suspend fun getLeaderboard(
        @Header("Authorization") token: String,
    ): Response<LeaderboardResponseModel>

    @POST("accounts/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Unit>

    @POST("accounts/reset")
    suspend fun sendResetOtp(@Body body: ResetPasswordRequest): Response<ResetOtpResponseModel>

    @PUT("accounts/reset")
    suspend fun setNewPassword(@Body body: SetNewPasswordRequest): Response<SetNewPasswordResponseModel>


}