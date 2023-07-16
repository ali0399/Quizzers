package com.quizzers.network.models

import androidx.annotation.Keep

@Keep
data class LoginResponseModel(
    val expiry: String,
    val token: String
)