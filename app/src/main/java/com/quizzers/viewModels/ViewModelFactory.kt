package com.quizzers.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.quizzers.network.RetrofitHelper
import com.quizzers.network.retrofit.QuizzerApi
import com.quizzers.repository.QuizzerRepository

class ViewModelFactory : ViewModelProvider.Factory {
    private val quizService: QuizzerApi =
        RetrofitHelper.getQuizInstance().create(QuizzerApi::class.java)
    val repository = QuizzerRepository(quizService)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuizViewModel(repository) as T
    }

}