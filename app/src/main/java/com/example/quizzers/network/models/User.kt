package com.example.quizzers.network.models

data class User(
    val id: String,
    val username: String,
    val email: String,
    val first_name: String,
    val last_name: String,
    val total_score: Int,
    val userprofile: Any
)