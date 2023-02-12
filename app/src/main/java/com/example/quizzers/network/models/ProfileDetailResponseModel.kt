package com.example.quizzers.network.models

data class ProfileDetailResponseModel(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val first_name: String = "",
    val last_name: String = "",
    val total_score: Int = 0,
    val userprofile: Userprofile? = null,
    val position: Int = 0,
)

data class Userprofile(
    val display_picture: String,
)