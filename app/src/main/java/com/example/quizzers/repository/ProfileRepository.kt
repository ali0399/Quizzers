package com.example.quizzers.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizzers.network.models.*
import com.example.quizzers.network.retrofit.QuizzerProfileApi
import okhttp3.MultipartBody

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
        try {
            val result = quizzerProfileApi.getProfileDetails(token)
            if (result != null)
                mProfileDetailResponse.postValue(result.body())
        } catch (e: Exception) {
            val errorBody = ProfileDetailResponseModel()
            mProfileDetailResponse.postValue(errorBody)
        }
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

    //update profile pic
    private val mPicUploadResponse = MutableLiveData<PicUploadResponse>()
    val picUploadResponse: LiveData<PicUploadResponse>
        get() {
            return mPicUploadResponse
        }

    suspend fun uploadProfilePhoto(token: String, part: MultipartBody.Part) {
        val result = quizzerProfileApi.uploadProfilePhoto(token, part)
        if (result != null)
            mPicUploadResponse.postValue(result.body())
    }

    //get leaderboard
    private val mLeaderboardResponse = MutableLiveData<LeaderboardResponseModel>()
    val leaderboardResponse: LiveData<LeaderboardResponseModel>
        get() {
            return mLeaderboardResponse
        }

    suspend fun getLeaderboard(token: String) {
        try {
            val result = quizzerProfileApi.getLeaderboard(token)
            if (result != null) {
                mLeaderboardResponse.postValue(result.body())
                Log.d("getLeaderboardRepo", "getLeaderboard: ${result.body()}")
            }
        } catch (e: Exception) {
//            val errorBody = LeaderboardResponseModel()
//            mLeaderboardResponse.postValue(errorBody)
        }
    }

    //logout

    private val mLogoutResponse = MutableLiveData<String>()
    val logoutResponse: LiveData<String>
        get() {
            return mLogoutResponse
        }

    suspend fun logout(token: String) {
        try {
            val result = quizzerProfileApi.logout(token)
            if (result.code() != 0) {
                mLogoutResponse.postValue(result.code().toString())
            }
        } catch (e: Exception) {
            Log.d("ProfileRepository", "logout: Error: $e")
        }
    }
}