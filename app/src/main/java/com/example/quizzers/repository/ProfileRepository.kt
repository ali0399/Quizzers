package com.example.quizzers.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizzers.network.models.*
import com.example.quizzers.network.retrofit.QuizzerProfileApi
import okhttp3.MultipartBody

const val TAG = "ProfileRepository"
class ProfileRepository(private val quizzerProfileApi: QuizzerProfileApi) {
    //Create User
    private val mCreateUserResponse = MutableLiveData<SafeResponse<CreateUserResponseModel>>()
    val createUserResponse: LiveData<SafeResponse<CreateUserResponseModel>>
        get() {
            return mCreateUserResponse
        }

    suspend fun createUser(body: CreateUserRequestModel) {
        mCreateUserResponse.postValue(SafeResponse.Loading())
        try {
            val result = quizzerProfileApi.createUser(body)
            if (result.body() != null && result.code() == 200)
                mCreateUserResponse.postValue(SafeResponse.Success(result.body()))
        } catch (e: Exception) {
            mCreateUserResponse.postValue(SafeResponse.Error(e.message.toString()))
        }
    }

    //Login
    private val mLoginResponseModel = MutableLiveData<SafeResponse<LoginResponseModel>>()
    val loginResponse: LiveData<SafeResponse<LoginResponseModel>>
        get() {
            return mLoginResponseModel
        }

    suspend fun login(body: LoginRequestModel) {
        mLoginResponseModel.postValue(SafeResponse.Loading())
        try {
            val result = quizzerProfileApi.login(body)
            Log.d(TAG, "login: code= ${result.code()}")
            if (result.code() == 400) {
//                Log.d(TAG,
//                    "login: ${result.raw()} ")
//                val errMsg = (result.body() as LoginErrorResponseModel).non_field_errors[0]
                mLoginResponseModel.postValue(SafeResponse.Error("Incorrect Credentials."))
            } else if (result.body() != null && result.code() == 200) {
                Log.d(TAG, "login: OK")
                mLoginResponseModel.postValue(SafeResponse.Success(result.body()))
            }
        } catch (e: Exception) {
            Log.d(TAG, "login: catch!!! $e")
            mLoginResponseModel.postValue(SafeResponse.Error(e.message.toString()))
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
        if (result.body() != null) {
            mCreateScoreResponse.postValue(result.body())
        }
    }

    //get Profile detail
    private val mProfileDetailResponse = MutableLiveData<SafeResponse<ProfileDetailResponseModel>>()
    val profileResponse: LiveData<SafeResponse<ProfileDetailResponseModel>>
        get() {
            return mProfileDetailResponse
        }

    suspend fun getProfileDetail(token: String) {
        mProfileDetailResponse.postValue(SafeResponse.Loading())
        try {
            val result = quizzerProfileApi.getProfileDetails(token)
            if (result.body() != null && result.code() == 200)
                mProfileDetailResponse.postValue(SafeResponse.Success(result.body()))
        } catch (e: Exception) {
            mProfileDetailResponse.postValue(SafeResponse.Error(e.message.toString()))
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
        if (result.body() != null)
            mUsernameUpdateResponse.postValue(result.body())
    }

    //update profile pic
    private val mPicUploadResponse = MutableLiveData<SafeResponse<PicUploadResponse>>()
    val picUploadResponse: LiveData<SafeResponse<PicUploadResponse>>
        get() {
            return mPicUploadResponse
        }

    suspend fun uploadProfilePhoto(token: String, part: MultipartBody.Part) {
        mPicUploadResponse.postValue(SafeResponse.Loading())
        try {
            val result = quizzerProfileApi.uploadProfilePhoto(token, part)
            if (result.body() != null && result.code() == 200)
                mPicUploadResponse.postValue(SafeResponse.Success(result.body()))
            else if (result.code() == 400)
                mPicUploadResponse.postValue(SafeResponse.Error("Network Error!"))
        } catch (e: Exception) {
            mPicUploadResponse.postValue(SafeResponse.Error("Error: $e"))
        }

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
            if (result.body() != null) {
                mLeaderboardResponse.postValue(result.body())
                Log.d("getLeaderboardRepo", "getLeaderboard: ${result.body()}")
            }
        } catch (e: Exception) {
            val errorBody = LeaderboardResponseModel()
            mLeaderboardResponse.postValue(errorBody)
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