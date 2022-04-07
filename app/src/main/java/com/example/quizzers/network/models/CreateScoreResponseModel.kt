package com.example.quizzers.network.models

data class CreateScoreResponseModel(
    val id: String,
    val user: String,
    val attempted: Int,
    val correct: Int,
    val score: Int,
    val created_at: String
)