package com.example.quizzers.network.models

import androidx.annotation.Keep

@Keep
data class SetNewPasswordRequest(
    val email: String,
    val otp: String,
    val password: String
)