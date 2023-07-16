package com.quizzers.repository

import androidx.lifecycle.MutableLiveData
import com.quizzers.network.models.TbdResponseModel
import com.quizzers.network.retrofit.QuizzerApi

class QuizzerRepository(private val quizzerApi: QuizzerApi) {
    private val mQuestions = MutableLiveData<SafeResponse<TbdResponseModel>>()

    val questions: MutableLiveData<SafeResponse<TbdResponseModel>>
        get() = mQuestions

    suspend fun getQuestions(options: Map<String, String>) {
        mQuestions.postValue(SafeResponse.Loading())
        try {
            val result = quizzerApi.getQuiz(options)
            if (result.body() != null) {
                if (result.body()!!.response_code == 0) {
                    mQuestions.postValue(SafeResponse.Success(result.body()))
                } else mQuestions.postValue(SafeResponse.Error("OpenTDB: ResponseCode= ${result.body()!!.response_code} "))
            }

        } catch (e: Exception) {
            mQuestions.postValue(SafeResponse.Error(e.message.toString()))
        }

    }
}