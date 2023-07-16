package com.quizzers.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizzers.network.models.TbdResponseModel
import com.quizzers.repository.QuizzerRepository
import com.quizzers.repository.SafeResponse
import kotlinx.coroutines.launch

class QuizViewModel(private val repository: QuizzerRepository) : ViewModel() {

    val errorMsg: MutableLiveData<String> = MutableLiveData("")

    val quizOptions: MutableLiveData<MutableMap<String, String>> =
        MutableLiveData(mutableMapOf("amount" to "10", "category" to "0", "type" to "multiple"))

    init {
        Log.d("QuizViewModel", "init: start")

    }

    fun getQuiz() {
        viewModelScope.launch {
            try {
                repository.getQuestions(quizOptions.value!!)
            } catch (exp: Exception) {
                errorMsg.value = exp.toString()
            }
        }
    }

    val quiz: LiveData<SafeResponse<TbdResponseModel>>
        get() = repository.questions

}