package com.example.quizzers.network

import androidx.viewbinding.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private const val BASE_URL = "https://opentdb.com/"// api.php?amount=10&type=multiple

    private val BASE_URL_PROFILE =
        if (BuildConfig.BUILD_TYPE == "release" || BuildConfig.BUILD_TYPE == "stage")
            "https://quizerz-prod-api.mdateequrrahman.in" //"http://ec2-3-83-80-40.compute-1.amazonaws.com/"
        else
            "http://127.0.0.1:8000/"

    fun getQuizInstance(): Retrofit {
        val logging = HttpLoggingInterceptor()
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    fun getProfileInstance(): Retrofit {
        val logging = HttpLoggingInterceptor()
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL_PROFILE)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }
}