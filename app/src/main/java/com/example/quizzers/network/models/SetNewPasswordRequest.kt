package com.example.quizzers.network.models

data class SetNewPasswordRequest(
    val email: String,
    val otp: String,
    val password: String
)