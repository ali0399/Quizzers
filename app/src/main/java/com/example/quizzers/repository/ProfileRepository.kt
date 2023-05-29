package com.example.quizzers.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizzers.network.models.CreateScoreRequestModel
import com.example.quizzers.network.models.CreateScoreResponseModel
import com.example.quizzers.network.models.CreateUserErrorModel
import com.example.quizzers.network.models.CreateUserRequestModel
import com.example.quizzers.network.models.CreateUserResponseModel
import com.example.quizzers.network.models.LeaderboardResponseModel
import com.example.quizzers.network.models.LoginErrorResponseModel
import com.example.quizzers.network.models.LoginRequestModel
import com.example.quizzers.network.models.LoginResponseModel
import com.example.quizzers.network.models.PicUploadResponse
import com.example.quizzers.network.models.ProfileDetailResponseModel
import com.example.quizzers.network.models.ResetOtpResponseModel
import com.example.quizzers.network.models.ResetPasswordRequest
import com.example.quizzers.network.models.SetNewPasswordRequest
import com.example.quizzers.network.models.SetNewPasswordResponseModel
import com.example.quizzers.network.models.UsernameUpdateModel
import com.example.quizzers.network.retrofit.QuizzerProfileApi
import com.example.quizzers.utils.getErrorMessage
import com.google.gson.Gson
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
            else if (result.code() == 400) {
                val err = Gson().fromJson(
                    result.errorBody()!!.charStream(),
                    CreateUserErrorModel::class.java
                )
                Log.d(TAG, "createUser 400: ${err.message}")
                mCreateUserResponse.postValue(SafeResponse.Error(err.message))
            }
        } catch (e: Exception) {
            Log.e(TAG, "createUser: catch- $e")
            mCreateUserResponse.postValue(SafeResponse.Error(e.message ?: "Something went wrong"))
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
                val errBody = Gson().fromJson(
                    result.errorBody()!!.charStream(),
                    LoginErrorResponseModel::class.java
                )
                mLoginResponseModel.postValue(SafeResponse.Error(errBody.non_field_errors[0]))
            } else if (result.body() != null && result.code() == 200) {
                Log.d(TAG, "login: OK")
                mLoginResponseModel.postValue(SafeResponse.Success(result.body()))
            }
        } catch (e: Exception) {
            Log.e(TAG, "login: catch!!! $e")
            mLoginResponseModel.postValue(SafeResponse.Error(e.message ?: "Something went wrong"))
        }
    }

    //reset password

    suspend fun resetPassword(body: LoginRequestModel) {
        mLoginResponseModel.postValue(SafeResponse.Loading())
        try {
            val result = quizzerProfileApi.login(body)
            Log.d(TAG, "login: code= ${result.code()}")
            if (result.code() == 400) {
//                Log.d(TAG,
//                    "login: ${result.raw()} ")
                val errBody = Gson().fromJson(
                    result.errorBody()!!.charStream(),
                    LoginErrorResponseModel::class.java
                )
                mLoginResponseModel.postValue(SafeResponse.Error(errBody.non_field_errors[0]))
            } else if (result.body() != null && result.code() == 200) {
                Log.d(TAG, "login: OK")
                mLoginResponseModel.postValue(SafeResponse.Success(result.body()))
            }
        } catch (e: Exception) {
            Log.d(TAG, "login: catch!!! $e")
            mLoginResponseModel.postValue(SafeResponse.Error(e.message ?: "Something went wrong"))
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
            quizzerProfileApi.createScore(
                token,
                body
            )
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
            else if (result.body() != null && result.code() == 401)
                mProfileDetailResponse.postValue(SafeResponse.Error("Authorisation error"))

        } catch (e: Exception) {
            mProfileDetailResponse.postValue(
                SafeResponse.Error(
                    e.message ?: "Something went wrong"
                )
            )
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
            mPicUploadResponse.postValue(SafeResponse.Error("Error: ${e.message ?: "Something went wrong"}"))
        }

    }

    //get leaderboard
    private val mLeaderboardResponse = MutableLiveData<SafeResponse<LeaderboardResponseModel>>()
    val leaderboardResponse: LiveData<SafeResponse<LeaderboardResponseModel>>
        get() {
            return mLeaderboardResponse
        }

    suspend fun getLeaderboard(token: String) {
        try {
            val result = quizzerProfileApi.getLeaderboard(token)
            if (result.body() != null) {
                mLeaderboardResponse.postValue(SafeResponse.Success(result.body()))
                Log.d("getLeaderboardRepo", "getLeaderboard: ${result.body()}")
            }
        } catch (e: Exception) {
            val errorBody = LeaderboardResponseModel()
            mLeaderboardResponse.postValue(SafeResponse.Error(e.message.toString()))
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
            Log.e("ProfileRepository", "logout: Error: $e")
        }
    }

    //ResetPassword

    private val mResetResponse = MutableLiveData<SafeResponse<ResetOtpResponseModel>>()
    val resetResponse: LiveData<SafeResponse<ResetOtpResponseModel>>
        get() {
            return mResetResponse
        }

    suspend fun resetPassword(email: String) {
        try {
            val result = quizzerProfileApi.sendResetOtp(ResetPasswordRequest(email = email))
            if (result.code() == 200) {
                println("ResetPassword result:" + result.code().toString())
                mResetResponse.postValue(SafeResponse.Success(result.body()))
            } else {
                mResetResponse.postValue(
                    SafeResponse.Error(
                        result.errorBody()?.getErrorMessage() ?: ""
                    )
                )
            }
        } catch (e: java.lang.Exception) {
            Log.e("ProfileRepository", "resetPassword: Error: $e")
            mResetResponse.postValue(SafeResponse.Error(e.message ?: "Something went wrong"))
        }
    }

//Set new password

    private val mSetNewResponse = MutableLiveData<SafeResponse<SetNewPasswordResponseModel>>()
    val setNewResponse: LiveData<SafeResponse<SetNewPasswordResponseModel>>
        get() {
            return mSetNewResponse
        }

    suspend fun setNewPassword(email: String, password: String, otp: String) {
        try {
            val result = quizzerProfileApi.setNewPassword(
                SetNewPasswordRequest(
                    email = email,
                    otp = otp,
                    password = password
                )
            )
            if (result.code() == 200) {
                println("ResetPassword result:" + result.code().toString())
                mSetNewResponse.postValue(SafeResponse.Success(result.body()))
            } else {
                mSetNewResponse.postValue(
                    SafeResponse.Error(
                        result.errorBody()?.getErrorMessage() ?: ""
                    )
                )
            }
        } catch (e: java.lang.Exception) {
            Log.e("ProfileRepository", "resetPassword: Error: $e")
            mSetNewResponse.postValue(SafeResponse.Error(e.message ?: "Something went wrong"))
        }
    }
}