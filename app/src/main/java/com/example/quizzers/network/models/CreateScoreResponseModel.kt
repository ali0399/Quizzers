package com.example.quizzers.network.models

import androidx.annotation.Keep

@Keep
data class CreateScoreResponseModel(
    val id: String,
    val user: String,
    val attempted: Int,
    val correct: Int,
    val score: Int,
    val created_at: String
)