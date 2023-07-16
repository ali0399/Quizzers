package com.quizzers.network.models

import androidx.annotation.Keep

@Keep
data class CreateUserRequestModel(
    var username: String,
    var email: String,
    var password: String
)