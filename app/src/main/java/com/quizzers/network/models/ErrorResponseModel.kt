package com.quizzers.network.models

import androidx.annotation.Keep

@Keep
data class ErrorResponseModel(
    val detail: String
)