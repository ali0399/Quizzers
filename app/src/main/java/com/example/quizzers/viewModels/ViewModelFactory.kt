package com.example.quizzers.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizzers.network.RetrofitHelper
import com.example.quizzers.network.retrofit.QuizzerApi
import com.example.quizzers.repository.QuizzerRepository

class ViewModelFactory : ViewModelProvider.Factory {
    val quizService = RetrofitHelper.getQuizInstance().create(QuizzerApi::class.java)
    val repository = QuizzerRepository(quizService)

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return QuizViewModel(repository) as T
    }

}