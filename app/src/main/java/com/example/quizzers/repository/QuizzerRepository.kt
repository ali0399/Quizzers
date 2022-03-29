package com.example.quizzers.repository

import androidx.lifecycle.LiveData
import com.example.quizzers.network.models.TbdResponseModel
import com.example.quizzers.network.retrofit.QuizzerApi
import androidx.lifecycle.MutableLiveData

class QuizzerRepository(private val quizzerApi: QuizzerApi) {
    private val mQuestions = MutableLiveData<TbdResponseModel>()

    val quetions: TbdResponseModel?
        get()= mQuestions.value
    suspend fun getQuestions(options:Map<String,String>):TbdResponseModel?{
        val result=quizzerApi.getQuiz(options)
        if(result!=null){
            mQuestions.postValue(result.body())
        }
        return quetions
    }
}