package com.example.quizzers.network.models

import androidx.annotation.Keep

@Keep
data class ResetPasswordRequest(
    val email: String
)