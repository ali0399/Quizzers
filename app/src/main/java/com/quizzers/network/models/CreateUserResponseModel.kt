package com.quizzers.network.models

import androidx.annotation.Keep

@Keep
data class CreateUserResponseModel(
    val user: User,
    val token: String
)