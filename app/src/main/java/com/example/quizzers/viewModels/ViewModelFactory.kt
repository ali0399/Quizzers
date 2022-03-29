package com.example.quizzers.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizzers.repository.QuizzerRepository

class ViewModelFactory(private val repository:QuizzerRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return QuizViewModel(repository) as T
    }

}