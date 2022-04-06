package com.example.quizzers.network.models

data class CreateScoreRequestModel(
    val attempted: Int,
    val correct: Int,
    val score: Int
)