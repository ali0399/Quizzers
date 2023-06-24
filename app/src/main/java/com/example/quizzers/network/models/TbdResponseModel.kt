package com.example.quizzers.network.models

import androidx.annotation.Keep

@Keep
data class TbdResponseModel(
    val response_code: Int?,
    val results: List<Result>?
)