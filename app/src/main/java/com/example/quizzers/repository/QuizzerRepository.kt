package com.example.quizzers.repository

import androidx.lifecycle.LiveData
import com.example.quizzers.network.models.TbdResponseModel
import androidx.lifecycle.MutableLiveData
import com.example.quizzers.network.retrofit.QuizzerApi

class QuizzerRepository(private val quizzerApi: QuizzerApi) {
    private val mQuestions = MutableLiveData<TbdResponseModel>()

    val quetions: LiveData<TbdResponseModel>
        get(){
            return mQuestions
        }
    suspend fun getQuestions(options:Map<String,String>){
        val result=quizzerApi.getQuiz(options)
        if (result != null) {
            mQuestions.postValue(result.body())
        } else mQuestions.postValue(null)
    }
}