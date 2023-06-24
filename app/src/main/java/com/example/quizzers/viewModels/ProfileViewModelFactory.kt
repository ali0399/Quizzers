package com.example.quizzers.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizzers.network.RetrofitHelper
import com.example.quizzers.network.retrofit.QuizzerProfileApi
import com.example.quizzers.repository.ProfileRepository

class ProfileViewModelFactory : ViewModelProvider.Factory {
    private val profileService: QuizzerProfileApi =
        RetrofitHelper.getProfileInstance().create(QuizzerProfileApi::class.java)
    private val profileRepository = ProfileRepository(profileService)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(profileRepository) as T
    }
}