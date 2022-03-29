package com.example.quizzers.network.retrofit

import com.example.quizzers.network.models.TbdResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface QuizzerApi {
    @GET("api.php")
    suspend fun getQuiz(
        @QueryMap  options: Map<String,String>
    ): Response<TbdResponseModel>
}