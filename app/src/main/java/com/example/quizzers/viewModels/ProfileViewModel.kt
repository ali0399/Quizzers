package com.example.quizzers.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzers.network.models.CreateScoreRequestModel
import com.example.quizzers.network.models.CreateScoreResponseModel
import com.example.quizzers.network.models.CreateUserRequestModel
import com.example.quizzers.network.models.CreateUserResponseModel
import com.example.quizzers.network.models.LeaderboardResponseModel
import com.example.quizzers.network.models.LoginRequestModel
import com.example.quizzers.network.models.LoginResponseModel
import com.example.quizzers.network.models.PicUploadResponse
import com.example.quizzers.network.models.ProfileDetailResponseModel
import com.example.quizzers.network.models.ResetOtpResponseModel
import com.example.quizzers.network.models.SetNewPasswordResponseModel
import com.example.quizzers.network.models.UsernameUpdateModel
import com.example.quizzers.repository.ProfileRepository
import com.example.quizzers.repository.SafeResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {
    private val TAG = "ProfileViewModel"
    val errorMsg: MutableLiveData<String> = MutableLiveData("")

    init {
        Log.d(TAG, "init: start")
        /*viewModelScope.launch {
            repository.createUser(requestBody as CreateUserRequestModel)
            repository.login(requestBody as LoginRequestModel)
        }*/
    }

    fun login(requestBody: LoginRequestModel) {
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

    fun updateUsername(token: String, body: UsernameUpdateModel) {
        Log.d(TAG, "getProfileDetail: start")
        viewModelScope.launch {
            repository.updateUsername(token, body)
        }
    }

    fun uploadPhoto(token: String, part: MultipartBody.Part) {
        Log.d(TAG, "getProfileDetail: start")
        viewModelScope.launch {
            repository.uploadProfilePhoto(token, part)
        }
    }

    fun getLeaderboard(token: String) {
        Log.d(TAG, "getLeaderboard: start")
        viewModelScope.launch {
            repository.getLeaderboard(token)
        }
    }

    fun logout(token: String) {
        viewModelScope.launch {
            repository.logout(token)
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            repository.resetPassword(email = email)
            return@launch
        }
    }

    fun setNewPassword(email: String, newPassword: String, otp: String) {
        viewModelScope.launch {
            repository.setNewPassword(email = email, password = newPassword, otp = otp)
        }
    }


//    fun createUser(requestBody:Any){
//        Log.d(TAG, "createUser: start")
//        viewModelScope.launch {
//            repository.createUser(requestBody as CreateUserRequestModel)
//        }
//    }

    val createUserResponse: LiveData<SafeResponse<CreateUserResponseModel>>
        get() = repository.createUserResponse
    val loginResponse: LiveData<SafeResponse<LoginResponseModel>>
        get() = repository.loginResponse
    val createScoreResponse: LiveData<CreateScoreResponseModel>
        get() = repository.scoreResponse
    val profileDetails: LiveData<SafeResponse<ProfileDetailResponseModel>>
        get() = repository.profileResponse
    val uploadResponse: LiveData<SafeResponse<PicUploadResponse>>
        get() = repository.picUploadResponse
    val usernameUpdateResponse: LiveData<UsernameUpdateModel>
        get() = repository.usernameUpdateResponse
    val leaderboardResponse: LiveData<LeaderboardResponseModel>
        get() = repository.leaderboardResponse
    val logoutResponseCd: LiveData<String>
        get() = repository.logoutResponse
    val resetResponse: LiveData<ResetOtpResponseModel>
        get() = repository.resetResponse
    val setNewPasswordResponse: LiveData<SetNewPasswordResponseModel>
        get() = repository.setNewResponse

}