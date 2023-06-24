package com.example.quizzers.network.models

import androidx.annotation.Keep

@Keep
data class CreateScoreRequestModel(
    val attempted: Int,
    val correct: Int,
    val score: Int
)