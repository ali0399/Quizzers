package com.example.quizzers.network.models

data class LeaderboardResponseModelItem(
    val id: String,
    val username: String,
    val email: String,
    val first_name: String,
    val last_name: String,
    val total_score: Int,
    val userprofile: Userprofile?,
)