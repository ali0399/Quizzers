package com.example.quizzers.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizzers.network.models.CreateUserRequestModel
import com.example.quizzers.repository.ProfileRepository

class ProfileViewModelFactory(private val repository:ProfileRepository,private val requestBody: Any):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(repository) as T
    }
}