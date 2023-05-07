package com.example.quizzers.utils

import com.example.quizzers.network.models.ErrorResponseModel
import com.google.gson.Gson
import okhttp3.ResponseBody

fun String?.isValidEmail() = !isNullOrBlank() && matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))

fun ResponseBody.getErrorMessage(): String {
    return Gson().fromJson(
        this.charStream(),
        ErrorResponseModel::class.java
    ).detail
}