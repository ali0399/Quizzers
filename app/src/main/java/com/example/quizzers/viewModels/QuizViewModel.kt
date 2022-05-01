package com.example.quizzers.viewModels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzers.network.models.Result
import com.example.quizzers.network.models.TbdResponseModel
import com.example.quizzers.repository.QuizzerRepository
import kotlinx.coroutines.launch

class QuizViewModel(private val repository: QuizzerRepository) : ViewModel() {

    val errorMsg: MutableLiveData<String> = MutableLiveData("")

    init {
        Log.d("QuizViewModel", "init: start")
        viewModelScope.launch {
            try {
                repository.getQuestions(mapOf("amount" to "10", "type" to "multiple"))
            } catch (exp: Exception) {
                errorMsg.value = exp.toString()
            }
        }
    }

    val quiz: LiveData<TbdResponseModel>
        get() = repository.quetions

}