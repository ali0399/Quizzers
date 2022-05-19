package com.example.quizzers.repository

import androidx.lifecycle.MutableLiveData
import com.example.quizzers.network.models.TbdResponseModel
import com.example.quizzers.network.retrofit.QuizzerApi

class QuizzerRepository(private val quizzerApi: QuizzerApi) {
    private val mQuestions = MutableLiveData<Response<TbdResponseModel>>()

    val questions: MutableLiveData<Response<TbdResponseModel>>
        get() = mQuestions

    suspend fun getQuestions(options: Map<String, String>) {
        try {
            val result = quizzerApi.getQuiz(options)
            if (result.body() != null) {
                if (result.body()!!.response_code == 0) {
                    mQuestions.postValue(Response.Success(result.body()))
                } else mQuestions.postValue(Response.Error("OpenTDB: ResponseCode= ${result.body()!!.response_code} "))
            }

        } catch (e: Exception) {
            mQuestions.postValue(Response.Error(e.message.toString()))
        }

    }
}