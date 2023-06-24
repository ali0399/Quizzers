package com.example.quizzers.network.models

import androidx.annotation.Keep

@Keep
data class UsernameUpdateModel(
    val first_name: String,
    val last_name: String,
)