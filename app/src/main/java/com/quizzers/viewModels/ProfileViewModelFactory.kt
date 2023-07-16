package com.quizzers.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.quizzers.network.RetrofitHelper
import com.quizzers.network.retrofit.QuizzerProfileApi
import com.quizzers.repository.ProfileRepository

class ProfileViewModelFactory : ViewModelProvider.Factory {
    private val profileService: QuizzerProfileApi =
        RetrofitHelper.getProfileInstance().create(QuizzerProfileApi::class.java)
    private val profileRepository = ProfileRepository(profileService)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(profileRepository) as T
    }
}