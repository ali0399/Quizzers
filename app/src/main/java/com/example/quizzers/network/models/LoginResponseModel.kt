package com.example.quizzers.network.models

data class LoginResponseModel(
    val expiry: String,
    val token: String
)