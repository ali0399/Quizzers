package com.quizzers.network.models

import androidx.annotation.Keep

@Keep
data class LoginRequestModel(
    var username: String,
    var password: String
)