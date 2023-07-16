package com.quizzers.network.models

import androidx.annotation.Keep

@Keep
data class LoginErrorResponseModel(
    val non_field_errors: List<String>,
)