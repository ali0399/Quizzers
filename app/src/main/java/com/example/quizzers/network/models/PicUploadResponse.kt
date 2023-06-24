package com.example.quizzers.network.models

import androidx.annotation.Keep

@Keep
data class PicUploadResponse(
    val user: String,
    val display_picture: String,
)